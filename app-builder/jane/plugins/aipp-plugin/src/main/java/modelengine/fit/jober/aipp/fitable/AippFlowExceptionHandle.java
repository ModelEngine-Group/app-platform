/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.MetaInstanceUtils;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.globalization.LocaleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 流程异常处理服务
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class AippFlowExceptionHandle implements FlowExceptionService {
    private static final Logger log = Logger.get(AippFlowExceptionHandle.class);

    private static final String UI_WORD_KEY = "aipp.fitable.AippFlowExceptionHandle";

    private static final String UI_WORD_KEY_HINT = "aipp.fitable.AippFlowExceptionHandle.hint";

    private final AippLogService aippLogService;

    private final MetaInstanceService metaInstanceService;

    private final LocaleService localeService;

    private final ToolExceptionHandle toolExceptionHandle;

    private final AppChatSessionService appChatSessionService;

    private final BrokerClient brokerClient;

    public AippFlowExceptionHandle(@Fit AippLogService aippLogService, @Fit MetaInstanceService metaInstanceService,
            @Fit LocaleService localeService, @Fit AppChatSessionService appChatSessionService,
            @Fit ToolExceptionHandle toolExceptionHandle, @Fit BrokerClient brokerClient) {
        this.aippLogService = aippLogService;
        this.metaInstanceService = metaInstanceService;
        this.localeService = localeService;
        this.appChatSessionService = appChatSessionService;
        this.toolExceptionHandle = toolExceptionHandle;
        this.brokerClient = brokerClient;
    }

    private void addErrorLog(String aippInstId, List<Map<String, Object>> contexts, boolean enableErrorDetails,
            Locale locale, String errorMessage) {
        Instance instance = MetaInstanceUtils.getInstanceDetailByInstanceId(aippInstId, null, this.metaInstanceService);
        String instanceStatus = instance.getInfo().get(AippConst.INST_STATUS_KEY);
        if (MetaInstStatusEnum.TERMINATED.name().equals(instanceStatus)) {
            log.debug("Aipp instance is already terminated. [aippInstId={}]", aippInstId);
            return;
        }
        List<AippInstLog> instLogs = this.aippLogService.queryInstanceLogSince(aippInstId, null);
        if (!instLogs.isEmpty()) {
            if (AippInstLogType.ERROR.name().equals(instLogs.get(instLogs.size() - 1).getLogType())) {
                log.warn("already add error log, aippInstId {}", aippInstId);
                return;
            }
        }
        String msg = this.localeService.localize(locale, UI_WORD_KEY);
        if (enableErrorDetails) {
            if (StringUtils.isNotEmpty(errorMessage)) {
                msg += "\n" + this.localeService.localize(locale, UI_WORD_KEY_HINT) + ": " + errorMessage;
            }
        }
        this.aippLogService.insertErrorLog(msg, contexts);
    }

    /**
     * 异常回调实现
     *
     * @param nodeId 异常发生的节点Id
     * @param contexts 流程上下文
     * @param errorMessage 异常错误信息
     */
    @Fitable("modelengine.fit.jober.aipp.fitable.AippFlowExceptionHandler")
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> contexts, FlowErrorInfo errorMessage) {
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        String versionId = ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        log.error("versionId {} nodeId {} errorMessage {}", versionId, nodeId, errorMessage.getErrorMessage());
        log.debug("handleException businessData {}", businessData);
        String aippInstId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ERROR.name())
                .build();
        OperationContext context = DataUtils.getOpContext(businessData);
        metaInstanceService.patchMetaInstance(versionId, aippInstId, declarationInfo, context);
        this.appChatSessionService.getSession(aippInstId).ifPresent(e -> {
            this.toolExceptionHandle.handleFitException(errorMessage);
            String finalErrorMsg = this.toolExceptionHandle.getFixErrorMsg(errorMessage, e.getLocale(), true);
            boolean enableErrorDetails = e.isDebug() || isModelError(errorMessage);
            addErrorLog(aippInstId, contexts, enableErrorDetails, e.getLocale(), finalErrorMsg);
            log.error("handleException completed, aippInstId {}", aippInstId);
        });

        String parentExceptionFitableId = ObjectUtils.cast(businessData.get(AippConst.PARENT_EXCEPTION_FITABLE_ID));
        if (StringUtils.isNotEmpty(parentExceptionFitableId)) {
            try {
                this.brokerClient.getRouter(FlowExceptionService.class,
                                FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE)
                        .route(new FitableIdFilter(parentExceptionFitableId))
                        .invoke(nodeId, contexts, errorMessage);
                log.info("Call parent exception fitable successfully, fitableId:{}, aippInstId {}.",
                        parentExceptionFitableId, aippInstId);
            } catch (FitException exception) {
                log.error("Call parent exception fitable error, fitableId:{}, aippInstId {}.", parentExceptionFitableId,
                        aippInstId);
                log.error("exception: ", exception);
            }
        }
    }

    private boolean isModelError(FlowErrorInfo errorMessage) {
        return Objects.equals(errorMessage.getErrorCode(), AippErrCode.MODEL_SERVICE_INVOKE_ERROR.getCode());
    }
}
