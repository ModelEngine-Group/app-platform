/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.InspirationQueryCondition;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domain.PromptProperty;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.po.InspirationPo;
import modelengine.fit.jober.aipp.repository.AppBuilderInspirationRepository;
import modelengine.fit.jober.aipp.service.AppBuilderPromptService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.jade.common.globalization.LocaleService;

import com.alibaba.fastjson.JSONObject;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 为 {@link AppBuilderPromptService}提供实现
 *
 * @author 姚江
 * @since 2024-04-26
 */
@Component
public class AppBuilderPromptServiceImpl implements AppBuilderPromptService {
    private static final Logger log = Logger.get(AppBuilderPromptServiceImpl.class);

    private static final String UI_WORD_KEY = "aipp.service.impl.prompt.mine";

    private final AppBuilderAppFactory appFactory;

    private final AppBuilderInspirationRepository inspirationRepo;

    private final MetaService metaService;

    private final LocaleService localeService;

    public AppBuilderPromptServiceImpl(AppBuilderAppFactory appFactory, AppBuilderInspirationRepository inspirationRepo,
            MetaService metaService, LocaleService localeService) {
        this.appFactory = appFactory;
        this.inspirationRepo = inspirationRepo;
        this.metaService = metaService;
        this.localeService = localeService;
    }

    @Override
    public Rsp<AppBuilderPromptDto> queryInspirations(String appId, String categoryId, OperationContext context,
            boolean isDebug) {
        PromptProperty promptProperty = this.findInspirationProperty(appId);
        AppBuilderPromptCategoryDto category = promptProperty.getCategoryById(categoryId);
        List<InspirationPo> customInspirationList =
                this.getCustomInspirationListWithCategoryId(categoryId, category, appId, context);

        // 判断是否直接查看的是“我的”类目下的灵感大全
        boolean isCustomCategory = promptProperty.isCustomCategory(categoryId);
        List<AppBuilderPromptDto.AppBuilderInspirationDto> inspirations;
        if (isCustomCategory) {
            this.checkCustomQueryValid(categoryId, isDebug, customInspirationList);
            category = this.buildCustomCategory(categoryId, customInspirationList);
            inspirations = this.getInspirationsWhenIsCustomCategory(customInspirationList);
        } else {
            inspirations = this.getInspirationsWhenIsNotCustomCategory(isDebug,
                    category,
                    customInspirationList,
                    promptProperty.getInspirationsByCategoryId(categoryId));
        }
        return Rsp.ok(AppBuilderPromptDto.builder()
                .inspirations(inspirations)
                .categories(category.getChildren())
                .build());
    }

    private void checkCustomQueryValid(String categoryId, boolean isDebug, List<InspirationPo> customInspirationList) {
        if (isDebug || customInspirationList.isEmpty()) {
            throw new IllegalStateException("Category " + categoryId + " not found.");
        }
    }

    private List<InspirationPo> getCustomInspirationListWithCategoryId(String categoryId,
            AppBuilderPromptCategoryDto category, String appId, OperationContext context) {
        String parentId = Optional.ofNullable(category)
                .map(c -> Objects.equals(categoryId, "root") ? null : categoryId)
                .orElse(null);
        return getCustomInspirationList(appId, context, parentId, category == null ? categoryId : null);
    }

    private List<AppBuilderPromptDto.AppBuilderInspirationDto> getInspirationsWhenIsCustomCategory(
            List<InspirationPo> customInspirationList) {
        List<AppBuilderPromptDto.AppBuilderInspirationDto> inspirations = new ArrayList<>();
        this.mergeCustomInspirations(customInspirationList, inspirations);
        return inspirations;
    }

    private AppBuilderPromptCategoryDto buildCustomCategory(String categoryId,
            List<InspirationPo> customInspirationList) {
        // 直接查看的是“我的”类目, 需要手动增加“我的”类目信息
        String msg = this.localeService.localize(UI_WORD_KEY);
        return AppBuilderPromptCategoryDto.builder()
                .id(categoryId)
                .title(msg)
                .disable(true)
                .parent(customInspirationList.get(0).getParentId())
                .children(new ArrayList<>())
                .build();
    }

    private List<AppBuilderPromptDto.AppBuilderInspirationDto> getInspirationsWhenIsNotCustomCategory(boolean isDebug,
            AppBuilderPromptCategoryDto category, List<InspirationPo> customInspirationList,
            List<AppBuilderPromptDto.AppBuilderInspirationDto> promptInspirationList) {
        // 运行时增加用户新增的“我的”类目以及灵感信息
        if (isDebug) {
            return promptInspirationList;
        }
        this.mergeCustomCategories(customInspirationList, Collections.singletonList(category));
        this.mergeCustomInspirations(customInspirationList, promptInspirationList);
        return promptInspirationList;
    }

    private List<InspirationPo> getCustomInspirationList(String appId, OperationContext context, String parentId,
            String categoryId) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        String aippId = appBuilderApp.getAppSuiteId();
        return this.inspirationRepo.selectWithCondition(new InspirationQueryCondition(aippId,
                parentId,
                categoryId,
                context.getOperator()));
    }

    private void mergeCustomInspirations(List<InspirationPo> customInspirationList,
            List<AppBuilderPromptDto.AppBuilderInspirationDto> inspirations) {
        if (customInspirationList.isEmpty()) {
            return;
        }
        // 在灵感大全中增加“我的”类目下的灵感大全
        List<AppBuilderPromptDto.AppBuilderInspirationDto> customInspirations = customInspirationList.stream()
                .map(customInsp -> JsonUtils.parseObject(customInsp.getValue(),
                        AppBuilderPromptDto.AppBuilderInspirationDto.class))
                .collect(Collectors.toList());
        inspirations.addAll(customInspirations);
    }

    private void mergeCustomCategories(List<InspirationPo> customInspirationList,
            List<AppBuilderPromptCategoryDto> categories) {
        Map<String, String> categoryMap = customInspirationList.stream()
                .collect(Collectors.toMap(InspirationPo::getParentId,
                        InspirationPo::getCategoryId,
                        (oldValue, newValue) -> oldValue));
        this.mergeCustomCategoriesRecurse(categories, categoryMap);
    }

    private PromptProperty findInspirationProperty(String appId) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        List<AppBuilderFormProperty> formProperties = appBuilderApp.getFormProperties();
        return formProperties.stream()
                .filter(fp -> fp.getName().equals("inspiration"))
                .findAny()
                .map(PromptProperty::new)
                .orElseThrow(() -> new IllegalStateException("Don't find property 'inspiration'."));
    }

    @Override
    public Rsp<List<AppBuilderPromptCategoryDto>> listPromptCategories(String appId, OperationContext context,
            boolean isDebug) {
        PromptProperty promptProperty = this.findInspirationProperty(appId);
        List<AppBuilderPromptCategoryDto> categories = promptProperty.getAllCategories();
        if (isDebug) {
            return Rsp.ok(categories);
        }
        // 运行时，需要增加“我的”类目信息
        List<InspirationPo> customInspirationList = this.getCustomInspirationList(appId, context, null, null);
        this.mergeCustomCategories(customInspirationList, categories);
        return Rsp.ok(categories);
    }

    private void mergeCustomCategoriesRecurse(List<AppBuilderPromptCategoryDto> categories,
            Map<String, String> categoryMap) {
        if (categoryMap.isEmpty()) {
            return;
        }

        String msg = this.localeService.localize(UI_WORD_KEY);
        categories.forEach(category -> {
            String categoryId = category.getId();
            if (categoryMap.containsKey(categoryId)) {
                String customCategoryId = categoryMap.get(categoryId);
                AppBuilderPromptCategoryDto customCategory = new AppBuilderPromptCategoryDto(msg,
                        customCategoryId,
                        categoryId + ":" + customCategoryId,
                        true,
                        new ArrayList<>());
                category.getChildren().add(customCategory);
                categoryMap.remove(categoryId);
            }

            if (!category.getChildren().isEmpty()) {
                this.mergeCustomCategoriesRecurse(category.getChildren(), categoryMap);
            }
        });
    }

    @Override
    public void addCustomInspiration(String appId, String parentId,
            AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto, OperationContext context) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        String aippId = appBuilderApp.getAppSuiteId();
        String customId;
        // 查询是否已存在"我的"类目
        Optional<String> categoryId =
                this.inspirationRepo.findCustomCategoryId(aippId, parentId, context.getOperator());
        customId = categoryId.orElseGet(() -> UUIDUtil.uuid().substring(0, 6));
        InspirationPo inspirationPo = buildInspirationPo(parentId, inspirationDto, context, customId, aippId);
        this.inspirationRepo.addCustomInspiration(inspirationPo);
    }

    private static InspirationPo buildInspirationPo(String parentId,
            AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto, OperationContext context, String customId,
            String aippId) {
        // 需要把parent:id塞到value的category中
        inspirationDto.setCategory(StringUtils.format("{0}:{1}", parentId, customId));
        return InspirationPo.builder()
                .aippId(aippId)
                .parentId(parentId)
                .categoryId(customId)
                .inspirationId(inspirationDto.getId())
                .value(JSONObject.toJSONString(inspirationDto))
                .createUser(context.getOperator())
                .build();
    }

    @Override
    public void updateCustomInspiration(String appId, String categoryId, String inspirationId,
            AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto, OperationContext context) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        String aippId = appBuilderApp.getAppSuiteId();
        Validation.equals(inspirationId,
                inspirationDto.getId(),
                () -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, "inspiration id"));
        this.inspirationRepo.updateCustomInspiration(inspirationId,
                buildInspirationPo(null, inspirationDto, context, categoryId, aippId));
    }

    @Override
    public void deleteCustomInspiration(String appId, String categoryId, String inspirationId,
            OperationContext context) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        String aippId = appBuilderApp.getAppSuiteId();
        this.inspirationRepo.deleteCustomInspiration(aippId, categoryId, inspirationId, context.getOperator());
    }
}
