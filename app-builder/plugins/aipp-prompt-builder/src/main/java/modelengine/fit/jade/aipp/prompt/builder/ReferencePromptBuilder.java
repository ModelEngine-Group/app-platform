/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.builder;

import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.prompt.PromptBuilder;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.PromptStrategy;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.code.PromptBuilderRetCode;
import modelengine.fit.jade.aipp.prompt.constant.Constant;
import modelengine.fit.jade.aipp.prompt.constant.InternalConstant;
import modelengine.fit.jade.aipp.prompt.constant.PromptBuilderOrder;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.LineSeparator;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.UuidUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.schema.SchemaValidator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 溯源提示词构造器。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
@Component
@Order(PromptBuilderOrder.REFERENCE)
public class ReferencePromptBuilder implements PromptBuilder {
    private static final String KNOWLEDGE_PLACEHOLDER = "knowledgeData";
    private static final String CURRENT_DATE_PLACEHOLDER = "currentDate";
    private static final String KNOWLEDGE_SEPARATOR = "\n";
    private static final String KNOWLEDGE_ID = "id";
    private static final String KNOWLEDGE_TEXT = "text";
    private static final String KNOWLEDGE_METADATA = "metadata";
    private static final String METADATA_URL = "url";
    private static final String METADATA_SOURCE = "source";
    private static final String METADATA_TIMESTAMP = "timestamp";
    private static final String METADATA_DATE = "date";
    private static final String KNOWLEDGE_SCHEMA = "/knowledge_reference_schema.json";
    private static final int REFERENCE_ID_LENGTH = 6;
    private static final List<String> REFERENCE_TEMPLATE_LIST =
            Arrays.asList(InternalConstant.REFERENCE_TEMPLATE_EN, InternalConstant.REFERENCE_TEMPLATE_ZH);

    private final LocaleService localeService;
    private final SchemaValidator validator;
    private final String schema;
    private final Map<String, String> templateI18nMap = new HashMap<>();

    ReferencePromptBuilder(LocaleService localeService, SchemaValidator validator) throws IOException {
        this.localeService = localeService;
        this.validator = validator;
        this.schema = IoUtils.content(ReferencePromptBuilder.class, KNOWLEDGE_SCHEMA);
        for (String path : REFERENCE_TEMPLATE_LIST) {
            String content = IoUtils.content(ReferencePromptBuilder.class, path);
            this.templateI18nMap.put(path, content.replace(LineSeparator.CRLF.value(), LineSeparator.LF.value()));
        }
    }

    @Override
    public Optional<PromptMessage> build(UserAdvice userAdvice, Map<String, Object> context) {
        Validation.notNull(userAdvice, "The user advice cannot be null.");
        Validation.notNull(userAdvice.getVariables(), "The prompt variables cannot be null.");
        if (!this.match(context)) {
            return Optional.empty();
        }

        List<Map<String, Object>> knowledgeList =
                this.dedupeKnowledge(ObjectUtils.cast(context.get(Constant.KNOWLEDGE_CONTEXT_KEY)));
        Map<String, Map<String, Object>> referenceKnowledge = knowledgeList.stream()
                .collect(Collectors.toMap(item -> this.generateReferenceId(), Function.identity(), (k1, k2) -> k1,
                        LinkedHashMap::new));
        String templateFilePath = this.localeService.localize(InternalConstant.TEMPLATE_LOCALE_KEY);
        String referenceTemplate = this.templateI18nMap.get(templateFilePath);
        Validation.notBlank(referenceTemplate, "The reference prompt template cannot be blank.");

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String referenceMessage = new DefaultStringTemplate(referenceTemplate).render(MapBuilder.<String, String>get()
                .put(KNOWLEDGE_PLACEHOLDER, this.formatKnowledge(referenceKnowledge))
                .put(CURRENT_DATE_PLACEHOLDER, currentDate)
                .build());
        String systemMessage = this.getBackground(userAdvice.getBackground()) + referenceMessage;
        String humanMessage = new DefaultStringTemplate(userAdvice.getTemplate()).render(userAdvice.getVariables());
        Map<String, Object> metadata =
                MapBuilder.<String, Object>get().put(Constant.PROMPT_METADATA_KEY, referenceKnowledge).build();
        return Optional.of(new PromptMessage(systemMessage, humanMessage, metadata));
    }

    private String generateReferenceId() {
        String uuid = UuidUtils.randomUuidString().replace("-", StringUtils.EMPTY);
        if (uuid.length() > REFERENCE_ID_LENGTH) {
            return uuid.substring(0, REFERENCE_ID_LENGTH);
        }
        return uuid;
    }

    @Override
    public PromptStrategy strategy() {
        return PromptStrategy.REFERENCE;
    }

    private boolean match(Map<String, Object> context) {
        if (MapUtils.isEmpty(context)) {
            return false;
        }
        if (!context.containsKey(Constant.KNOWLEDGE_CONTEXT_KEY)) {
            return false;
        }
        Object knowledge = context.get(Constant.KNOWLEDGE_CONTEXT_KEY);
        try {
            this.validator.validate(this.schema, knowledge);
        } catch (FitException ignore) {
            return false;
        }
        // 只要有一个不为空即可
        return ObjectUtils.<List<List<?>>>cast(knowledge).stream().anyMatch(item -> !item.isEmpty());
    }

    private String getBackground(String background) {
        if (StringUtils.isBlank(background)) {
            return StringUtils.EMPTY;
        }
        String systemBackground = this.localeService.localize(InternalConstant.BACKGROUND_KEY);
        return systemBackground + InternalConstant.BLOCK_SEPARATOR + background + InternalConstant.BLOCK_SEPARATOR;
    }

    private List<Map<String, Object>> dedupeKnowledge(List<List<Map<String, Object>>> knowledgeData) {
        this.validateKnowledge(knowledgeData);
        Set<String> idSet = new HashSet<>();
        return knowledgeData.stream()
                .flatMap(Collection::stream)
                .filter(item -> !idSet.contains(item.get(KNOWLEDGE_ID)))
                .peek(item -> idSet.add(ObjectUtils.cast(item.get(KNOWLEDGE_ID))))
                .collect(Collectors.toList());
    }

    private String formatKnowledge(Map<String, Map<String, Object>> referenceKnowledge) {
        StringBuilder sb = new StringBuilder();
        referenceKnowledge.forEach((key, value) -> {
            StringBuilder knowledgeItem = new StringBuilder();
            knowledgeItem.append(StringUtils.format("[{0}] {1}", key, value.get(KNOWLEDGE_TEXT)));
            
            // 处理 metadata 信息
            if (value.containsKey(KNOWLEDGE_METADATA)) {
                Map<String, Object> metadata = ObjectUtils.cast(value.get(KNOWLEDGE_METADATA));
                if (metadata != null && !metadata.isEmpty()) {
                    List<String> metadataInfo = new ArrayList<>();
                    
                    // 添加 URL（网络搜索数据的标识）
                    if (metadata.containsKey(METADATA_URL) && metadata.get(METADATA_URL) != null) {
                        metadataInfo.add(StringUtils.format("URL: {0}", metadata.get(METADATA_URL)));
                    }
                    
                    // 添加来源
                    if (metadata.containsKey(METADATA_SOURCE) && metadata.get(METADATA_SOURCE) != null) {
                        metadataInfo.add(StringUtils.format("Source: {0}", metadata.get(METADATA_SOURCE)));
                    }
                    
                    // 添加时间戳
                    if (metadata.containsKey(METADATA_TIMESTAMP) && metadata.get(METADATA_TIMESTAMP) != null) {
                        metadataInfo.add(StringUtils.format("Timestamp: {0}", metadata.get(METADATA_TIMESTAMP)));
                    }
                    
                    // 添加日期
                    if (metadata.containsKey(METADATA_DATE) && metadata.get(METADATA_DATE) != null) {
                        metadataInfo.add(StringUtils.format("Date: {0}", metadata.get(METADATA_DATE)));
                    }
                    
                    if (!metadataInfo.isEmpty()) {
                        knowledgeItem.append(" [").append(String.join(", ", metadataInfo)).append("]");
                    }
                }
            }
            
            knowledgeItem.append(KNOWLEDGE_SEPARATOR);
            sb.append(knowledgeItem);
        });
        return sb.toString();
    }

    private void validateKnowledge(List<List<Map<String, Object>>> knowledgeData) {
        if (knowledgeData.isEmpty()) {
            throw new ModelEngineException(PromptBuilderRetCode.PROMPT_BUILDER_KNOWLEDGE_EMPTY);
        }
        if (knowledgeData.size() > InternalConstant.KNOWLEDGE_NODE_LIMIT) {
            throw new ModelEngineException(PromptBuilderRetCode.PROMPT_BUILDER_KNOWLEDGE_COUNT_LIMIT,
                    knowledgeData.size(), InternalConstant.KNOWLEDGE_NODE_LIMIT);
        }
        Integer totalLength = knowledgeData.stream()
                .flatMap(Collection::stream)
                .map(item -> ObjectUtils.<String>cast(item.get(KNOWLEDGE_TEXT)).length())
                .reduce(0, (total, element) -> total + element);

        if (totalLength > InternalConstant.KNOWLEDGE_CONTENT_LIMIT) {
            throw new ModelEngineException(PromptBuilderRetCode.PROMPT_BUILDER_KNOWLEDGE_CONTENT_LIMIT, totalLength,
                    InternalConstant.KNOWLEDGE_CONTENT_LIMIT);
        }
        if (totalLength == 0) {
            throw new ModelEngineException(PromptBuilderRetCode.PROMPT_BUILDER_KNOWLEDGE_EMPTY);
        }
    }
}
