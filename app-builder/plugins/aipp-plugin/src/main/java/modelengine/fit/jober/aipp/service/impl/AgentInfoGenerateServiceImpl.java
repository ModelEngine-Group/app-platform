/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.jade.carver.validation.ValidateTagMode.validateTagMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.utils.ContentProcessUtils;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.AgentInfoGenerateService;
import modelengine.fit.jober.aipp.service.AippModelService;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.PluginToolService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link AgentInfoGenerateService} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-12-2
 */
@Component
public class AgentInfoGenerateServiceImpl implements AgentInfoGenerateService {
    private static final Logger log = Logger.get(AgentInfoGenerateServiceImpl.class);

    private static final String UI_WORD_KEY = "aipp.service.impl.agent.agent";

    private final AippModelService aippModelService;

    private final AippModelCenter aippModelCenter;

    private final PluginToolService toolService;

    private final LocaleService localeService;

    private final AppBuilderAppRepository appRepository;

    private final String agentNameFormat = "^[\\u4E00-\\u9FA5A-Za-z0-9][\\u4E00-\\u9FA5A-Za-z0-9-_]*$";

    public AgentInfoGenerateServiceImpl(AippModelService aippModelService, AippModelCenter aippModelCenter,
            PluginToolService toolService, LocaleService localeService, AppBuilderAppRepository appRepository) {
        this.aippModelService = aippModelService;
        this.aippModelCenter = aippModelCenter;
        this.toolService = toolService;
        this.localeService = localeService;
        this.appRepository = appRepository;
    }

    @Override
    public String generateName(String desc, OperationContext context) {
        String name = this.generateByTemplate(desc, "prompt/promptGenerateName.txt", context);
        if (!name.matches(this.agentNameFormat) || name.trim().isEmpty() || !this.isNameUnique(context, name)) {
            name = this.localeService.localize(UI_WORD_KEY) + UUIDUtil.uuid();
        }
        return name;
    }

    private boolean isNameUnique(OperationContext context, String name) {
        AppQueryCondition queryCondition =
                AppQueryCondition.builder().tenantId(context.getTenantId()).name(name).build();
        if (!this.appRepository.selectWithCondition(queryCondition).isEmpty()) {
            log.error("Create aipp failed, [name={}, tenantId={}]", name, context.getTenantId());
            return false;
        }
        return true;
    }

    @Override
    public String generateGreeting(String desc, OperationContext context) {
        return this.generateByTemplate(desc, "prompt/promptGenerateGreeting.txt", context);
    }

    @Override
    public String generatePrompt(String desc, OperationContext context) {
        String prompt = this.generateByTemplate(desc, "prompt/promptGeneratePrompt.txt", context);
        String format;
        try {
            format = IoUtils.content(AgentInfoGenerateService.class.getClassLoader(),
                    "prompt/promptGeneratePromptFormat.txt");
        } catch (IOException e) {
            log.error("read prompt format file fail.", e);
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
        return StringUtils.format(format, prompt);
    }

    @Override
    public List<String> selectTools(String desc, String creator, OperationContext context) {
        return this.getToolsResult(desc, creator, context);
    }

    private ArrayList<String> getToolsResult(String desc, String creator, OperationContext context) {
        StringBuilder toolsCandidate = new StringBuilder();
        ListResult<PluginToolData> tools = this.getTools(creator);
        int count = tools.getCount();
        List<PluginToolData> toolData = tools.getData();
        for (int i = 0; i < count; i++) {
            toolsCandidate.append(StringUtils.format("[ID:{0},Name:{1},Desc:{2}]\n", i, toolData.get(i).getName(),
                    toolData.get(i).getDescription()));
        }
        String result = this.generateByTemplate("<Tools>\n" + toolsCandidate + "</Tools>\n" + "input: " + desc,
                "prompt/promptOfSelectTools.txt", context);

        ArrayList<Integer> toolsIndex;
        ObjectMapper mapper = new ObjectMapper();
        try {
            toolsIndex = mapper.readValue(result,
                    mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        } catch (JsonProcessingException e) {
            log.error("Parse the return tools index from LLM failed.", e);
            throw new AippException(AippErrCode.JSON_DECODE_FAILED, e.getMessage());
        }
        ArrayList<String> toolsResult = new ArrayList<>();
        for (int i = 0; i < Math.min(5, toolsIndex.size()); i++) {
            toolsResult.add(toolData.get(toolsIndex.get(i)).getUniqueName());
        }
        return toolsResult;
    }

    private ListResult<PluginToolData> getTools(String creator) {
        PluginToolQuery pluginQuery = new PluginToolQuery.Builder().toolName(null).includeTags(new HashSet<String>() {{
            add("FIT");
        }}).excludeTags(new HashSet<String>() {{
            add("APP");
        }}).mode(validateTagMode("AND")).offset(null).limit(null).creator(creator).isDeployed(true).build();
        return this.toolService.getPluginTools(pluginQuery);
    }

    private String generateByTemplate(String input, String templatePath, OperationContext context) {
        Map<String, String> values = MapBuilder.<String, String>get().put("input", input).build();
        String template;
        try {
            template = IoUtils.content(AgentInfoGenerateService.class.getClassLoader(), templatePath);
        } catch (IOException e) {
            log.error("read prompt template file fail.", e);
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
        ModelAccessInfo model = this.aippModelCenter.getDefaultModel(AippConst.CHAT_MODEL_TYPE, context);
        String prompt = new DefaultStringTemplate(template).render(values);
        String rawContent = aippModelService.chat(model.getServiceName(), model.getTag(), 0.0, prompt);
        return ContentProcessUtils.filterReasoningContent(rawContent);
    }
}