/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fit.jade.aipp.model.service.AippModelCenterExtension;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义的模型中心服务实现。
 *
 * @author songyongtan
 * @since 2025/3/6
 */
@Component("customAippModelCenter")
public class CustomAippModelCenter implements AippModelCenterExtension {
    private static final Logger LOG = Logger.get(CustomAippModelCenter.class);

    private final UserModelRepo userModelRepo;

    private final AippModelCenterExtension defaultModelCenter;

    /**
     * 构造方法。
     *
     * @param userModelRepo repo 层。
     * @param defaultModelCenter 默认实现，找不到时会使用默认的进行兜底。
     */
    public CustomAippModelCenter(UserModelRepo userModelRepo,
            @Fit(alias = "defaultAippModelCenter") AippModelCenterExtension defaultModelCenter) {
        this.userModelRepo = userModelRepo;
        this.defaultModelCenter = defaultModelCenter;
    }

    @Override
    public ModelListDto fetchModelList(String type, String scene, OperationContext context) {
        LOG.info("[Custom][fetchModelList] operator={}, type={}, scene={}.", context.getOperator(), type, scene);
        List<ModelPo> modelList = this.userModelRepo.getModelList(context.getOperator());
        if (CollectionUtils.isEmpty(modelList)) {
            return this.defaultModelCenter.fetchModelList(type, scene, context);
        }
        // 这里自定义按照用户分类返回数据的tag需要特殊处理，tag中额外存入用户信息，先快速打通功能，赶上320。格式："tag,userId"
        List<ModelAccessInfo> modelDtoList = modelList.stream()
                .map(po -> ModelAccessInfo.builder()
                        .serviceName(po.getName())
                        .tag(CustomTag.pack(context, po))
                        .build())
                .collect(Collectors.toList());
        return ModelListDto.builder().models(modelDtoList).total(modelDtoList.size()).build();
    }

    @Override
    public ModelAccessInfo getModelAccessInfo(String tag, String modelName, OperationContext context) {
        LOG.info("[Custom][getModelAccessInfo] operator={}, tag={}, name={}.",
                context == null ? "null" : context.getOperator(), tag, modelName);
        // context暂时不使用，当前内置工具调用大模型场景用不了，统一基于tag特殊处理。
        CustomTag customTag = CustomTag.unpack(tag);
        if (customTag == null) {
            return this.defaultModelCenter.getModelAccessInfo(tag, modelName, context);
        }
        ModelAccessPo accessInfo = this.userModelRepo.getModelAccessInfo(customTag.userId, customTag.tag, modelName);
        if (accessInfo == null) {
            return null;
        }
        return ModelAccessInfo.builder()
                .serviceName(accessInfo.getModelPO().getName())
                .baseUrl(accessInfo.getModelPO().getBaseUrl())
                .tag(tag)
                .accessKey(accessInfo.getApiKey())
                .build();
    }

    @Override
    public ModelAccessInfo getDefaultModel(String type, OperationContext context) {
        LOG.info("[Custom][getDefaultModel] operator={}, type={}.", context.getOperator(), type);
        ModelPo defaultModel = this.userModelRepo.getDefaultModel(context.getOperator());
        if (defaultModel == null) {
            return this.defaultModelCenter.getDefaultModel(type, context);
        }
        return ModelAccessInfo.builder()
                .serviceName(defaultModel.getName())
                .baseUrl(defaultModel.getBaseUrl())
                .tag(CustomTag.pack(context, defaultModel))
                .build();
    }

    private static class CustomTag {
        private String tag;

        private String userId;

        private CustomTag(String tag, String userId) {
            this.tag = tag;
            this.userId = userId;
        }

        private static String pack(OperationContext context, ModelPo po) {
            return po.getTag() + "," + context.getOperator();
        }

        private static CustomTag unpack(String tag) {
            // 格式："tag,userId"
            String[] split = tag.split(",");
            if (split.length != 2) {
                return null;
            }
            return new CustomTag(split[0], split[1]);
        }
    }
}
