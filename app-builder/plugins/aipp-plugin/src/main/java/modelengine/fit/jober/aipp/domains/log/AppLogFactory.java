/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.log;

import static modelengine.fit.jober.aipp.enums.AippInstLogType.FORM;

import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.util.JsonUtils;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * {@link AppLog} 的工厂类
 *
 * @author 张越
 * @since 2025-02-07
 */
@Component
@RequiredArgsConstructor
public class AppLogFactory {
    private static final String FORM_DATA = "formData";
    private static final String FORM_APPEARANCE = "formAppearance";

    /**
     * 通过 {@link AippInstLog} 和任务id创建一个实例对象.
     *
     * @param logData 日志对象.
     * @return {@link AppLog} 对象.
     */
    public AppLog create(AippInstLog logData) {
        if (StringUtils.equals(FORM.name(), logData.getLogType())) {
            AippLogData form = JsonUtils.parseObject(logData.getLogData(), AippLogData.class);
            if (form != null) {
                Map<String, String> newLogData = MapBuilder.<String, String>get()
                        .put(FORM_DATA, form.getFormData())
                        .put(FORM_APPEARANCE, form.getFormAppearance())
                        .build();
                logData.setLogData(JsonUtils.toJsonString(newLogData));
            }
        }
        return new AppLog(logData);
    }
}
