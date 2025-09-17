/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jade.aipp.template.render.TemplateService;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 直接回复节点实现
 *
 * @author 孙怡菲
 * @since 2025-09-11
 */
@Component
public class ReplyNode implements FlowableService {
    private final AippLogService aippLogService;

    private final TemplateService templateService;

    private final static String TEMPLATE_KEY = "template";

    private final static String VARIABLES_KEY = "variables";

    public ReplyNode(AippLogService aippLogService, TemplateService templateService) {
        this.aippLogService = aippLogService;
        this.templateService = templateService;
    }

    @Override
    @Fitable("modelengine.fit.jober.aipp.fitable.ReplyNodeComponent")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        String template = ObjectUtils.cast(businessData.get(TEMPLATE_KEY));
        Map<String, Object> args = ObjectUtils.cast(businessData.get(VARIABLES_KEY));
        String msg = this.templateService.renderTemplate(template, args);
        this.sendMsg(msg, businessData);
        return flowData;
    }

    private void sendMsg(String msg, Map<String, Object> businessData) {
        this.aippLogService.insertLogWithInterception(AippInstLogType.MSG.name(),
                AippLogData.builder().msg(msg).build(),
                businessData);
    }
}
