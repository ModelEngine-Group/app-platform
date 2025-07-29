/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fit.jober.aipp.dto.chat.PromptCategory;
import modelengine.fit.jober.aipp.dto.chat.PromptInfo;
import modelengine.fit.jober.aipp.genericable.adapter.AppBuilderPromptServiceAdapter;
import modelengine.fit.jober.aipp.service.AppBuilderPromptService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link AppBuilderPromptService} 的适配器类的实现类。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Component
public class AppBuilderPromptServiceAdapterImpl implements AppBuilderPromptServiceAdapter {
    private final AppBuilderPromptService appBuilderPromptService;
    private final ObjectSerializer objectSerializer;

    public AppBuilderPromptServiceAdapterImpl(AppBuilderPromptService appBuilderPromptService,
            @Fit(alias = "json") ObjectSerializer objectSerializer) {
        this.appBuilderPromptService =
                notNull(appBuilderPromptService, "The app builder prompt service cannot be null.");
        this.objectSerializer = notNull(objectSerializer, "The object serializer cannot be null.");
    }

    @Override
    public List<PromptCategory> listPromptCategories(String appId, OperationContext operationContext, boolean isDebug) {
        Rsp<List<AppBuilderPromptCategoryDto>> rsp =
                this.appBuilderPromptService.listPromptCategories(appId, operationContext, isDebug);
        return rsp.getData()
                .stream()
                .map(appBuilderPromptCategoryDto -> this.objectSerializer.<PromptCategory>deserialize(
                        this.objectSerializer.serialize(appBuilderPromptCategoryDto), PromptCategory.class))
                .collect(Collectors.toList());
    }

    @Override
    public PromptInfo queryInspirations(String appId, String categoryId, OperationContext operationContext,
            boolean isDebug) {
        Rsp<AppBuilderPromptDto> rsp =
                this.appBuilderPromptService.queryInspirations(appId, categoryId, operationContext, isDebug);
        AppBuilderPromptDto appBuilderPromptDto = rsp.getData();
        return this.appBuilderPromptDtoConvertToAdapter(appBuilderPromptDto);
    }

    PromptInfo appBuilderPromptDtoConvertToAdapter(AppBuilderPromptDto appBuilderPromptDto) {
        return this.objectSerializer.deserialize(objectSerializer.serialize(appBuilderPromptDto), PromptInfo.class);
    }
}
