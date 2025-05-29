/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.publish;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;

import lombok.AllArgsConstructor;

/**
 * 流程发布器.
 *
 * @author 张越
 * @since 2025-01-16
 */
@AllArgsConstructor
public class FlowPublisher implements Publisher {
    private final FlowsService flowsService;

    @Override
    public void publish(PublishContext context, AppVersion appVersion) {
        String configData = JsonUtils.toJsonString(context.getAppearance());
        FlowInfo createFlowInfo = this.flowsService.createFlows(configData, context.getOperationContext());
        try {
            FlowInfo flowInfo = this.flowsService.publishFlows(createFlowInfo.getFlowId(),
                    context.getPublishData().getVersion(), configData,
                    context.getOperationContext());
            context.setFlowInfo(flowInfo);
        } catch (JobberException e) {
            AippErrCode retCode = (e.getCode() == ErrorCodes.FLOW_ALREADY_EXIST.getErrorCode())
                    ? AippErrCode.FLOW_ALREADY_EXIST
                    : AippErrCode.APP_PUBLISH_FAILED;
            throw new AippException(context.getOperationContext(), retCode);
        }
    }
}
