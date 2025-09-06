/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.store.service.PluginToolService;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 更新节点错误信息.
 *
 * @author 李智超
 * @since 2024-10-08
 */
@Component
public class ToolExceptionHandle {
    private static final Logger log = Logger.get(AippFlowExceptionHandle.class);

    private static final String TOOL_ID = "toolId";

    private final LocaleService localeService;

    private final PluginToolService pluginToolService;

    /**
     * 构造函数
     *
     * @param localeService localeService
     * @param pluginToolService pluginToolService
     */
    public ToolExceptionHandle(@Fit LocaleService localeService, @Fit PluginToolService pluginToolService) {
        this.localeService = localeService;
        this.pluginToolService = pluginToolService;
    }

    /**
     * 拼接错误信息
     *
     * @param errorMessage 异常错误信息
     * @param locale 语言
     * @param isDialogue 是否是对话窗口
     * @return 错误码信息
     */
    public String getFixErrorMsg(FlowErrorInfo errorMessage, Locale locale, Boolean isDialogue) {
        if (errorMessage == null || errorMessage.getErrorCode() == null) {
            return StringUtils.EMPTY;
        }

        String handledErrorMsg = getLocalizedErrorMessage(errorMessage, locale);
        String toolName = getToolName(errorMessage).orElse("");
        String template = getErrorMessageTemplate(isDialogue, locale, !toolName.isEmpty());

        return formatErrorMessage(isDialogue, toolName, template, handledErrorMsg, errorMessage);
    }

    private String getLocalizedErrorMessage(FlowErrorInfo errorMessage, Locale locale) {
        String localizedMessage = localeService.localize(locale, String.valueOf(errorMessage.getErrorCode()),
                ObjectUtils.cast(errorMessage.getArgs()));
        return StringUtils.isNotBlank(localizedMessage) ? localizedMessage : errorMessage.getErrorMessage();
    }

    private Optional<String> getToolName(FlowErrorInfo errorMessage) {
        Map<String, String> properties = errorMessage.getProperties();
        if (properties != null && properties.containsKey(TOOL_ID) && StringUtils.isNotBlank(properties.get(TOOL_ID))) {
            return Optional.ofNullable(pluginToolService.getPluginTool(properties.get(TOOL_ID)).getName());
        }
        return Optional.empty();
    }

    private String getErrorMessageTemplate(Boolean isDialogue, Locale locale, boolean hasToolName) {
        AippErrCode errCode = isDialogue
                ? (hasToolName
                ? AippErrCode.DIALOGUE_TOOL_NODE_ERROR_OUTPUT
                : AippErrCode.DIALOGUE_GENERAL_NODE_ERROR_OUTPUT)
                : (hasToolName ? AippErrCode.ELSA_TOOL_NODE_ERROR_OUTPUT : AippErrCode.ELSA_GENERAL_NODE_ERROR_OUTPUT);
        return localeService.localize(locale, String.valueOf(errCode.getErrorCode()));
    }

    private String formatErrorMessage(Boolean isDialogue, String toolName, String template, String handledErrorMsg,
            FlowErrorInfo errorMessage) {
        if (isDialogue) {
            return toolName.isEmpty()
                    ? StringUtils.format(template, errorMessage.getNodeName(), handledErrorMsg)
                    : StringUtils.format(template, errorMessage.getNodeName(), toolName, handledErrorMsg);
        } else {
            return toolName.isEmpty()
                    ? StringUtils.format(template, handledErrorMsg)
                    : StringUtils.format(template, toolName, handledErrorMsg);
        }
    }

    /**
     * 处理FIT错误码
     *
     * @param errorMessage 异常错误信息
     */
    public static void handleFitException(FlowErrorInfo errorMessage) {
        if (errorMessage == null || errorMessage.getErrorCode() == null) {
            return;
        }

        switch (errorMessage.getErrorCode()) {
            case 0x7F010001: // 无法找到服务
                errorMessage.setErrorCode(AippErrCode.FIT_TOOL_LOOKUP_ERROR.getErrorCode());
                return;
            case 0x7F000103: // FITable ID重复
                errorMessage.setErrorCode(AippErrCode.DUPLICATE_PLUGIN_EXCEPTION.getErrorCode());
                return;
            default:
                updateErrorCodeBasedOnRange(errorMessage);
                break;
        }
    }

    private static void updateErrorCodeBasedOnRange(FlowErrorInfo errorMessage) {
        int errorCode = errorMessage.getErrorCode();
        if (isGeneralError(errorCode)) {
            errorMessage.setErrorCode(AippErrCode.FIT_TOOL_INVOCATION_EXCEPTION.getErrorCode());
        } else if (isRoutingOrLoadBalancingError(errorCode)) {
            errorMessage.setErrorCode(AippErrCode.FIT_TOOL_LOOKUP_ERROR.getErrorCode());
        } else if (isCommunicationError(errorCode)) {
            errorMessage.setErrorCode(AippErrCode.FIT_NETWORK_EXCEPTION.getErrorCode());
        } else {
            log.warn("Unrecognized error code: {}", errorCode);
        }
    }

    private static boolean isGeneralError(int errorCode) {
        return isInRange(errorCode, 0x7F010000, 0x7F01FFFF) || isInRange(errorCode, 0x7F050000, 0x7F05FFFF)
                || isInRange(errorCode, 0x7F060000, 0x7F06FFFF) || isInRange(errorCode, 0x7F100000, 0x7F1FFFFF)
                || isInRange(errorCode, 0x7F200000, 0x7F2FFFFF) || isInRange(errorCode, 0x7FF00000, 0x7FFFFFFF);
    }

    private static boolean isRoutingOrLoadBalancingError(int errorCode) {
        return isInRange(errorCode, 0x7F020000, 0x7F02FFFF) || isInRange(errorCode, 0x7F030000, 0x7F03FFFF);
    }

    private static boolean isCommunicationError(int errorCode) {
        return isInRange(errorCode, 0x7F040000, 0x7F04FFFF);
    }

    private static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}