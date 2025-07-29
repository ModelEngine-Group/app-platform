/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import modelengine.fit.jober.aipp.constants.AippConst;

import modelengine.fitframework.util.MapBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试工具类。
 *
 * @author 张越
 * @since 2025-01-12
 */
public class TestUtils {
    /**
     * 构建memory配置.
     *
     * @param isMemoryEnable 是否启用memory.
     * @param type 类型.
     * @param value 值.
     * @return 配置对象.
     */
    public static List<Map<String, Object>> buildMemoryConfigs(boolean isMemoryEnable, String type,
            String value) {
        List<Map<String, Object>> memoryConfigs = new ArrayList<>();
        memoryConfigs.add(MapBuilder.<String, Object>get()
                .put("name", AippConst.MEMORY_SWITCH_KEY)
                .put("value", isMemoryEnable)
                .build());
        memoryConfigs.add(MapBuilder.<String, Object>get().put("name", "type").put("value", type).build());
        memoryConfigs.add(MapBuilder.<String, Object>get().put("name", "value").put("value", value).build());
        return memoryConfigs;
    }
}
