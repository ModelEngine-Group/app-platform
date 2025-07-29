/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统配置领域对象.
 *
 * @author 张越
 * @since 2024-11-30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Slf4j
public class AippSystemConfig extends BaseDomain {
    private Long id;
    private String configKey;
    private String configValue;
    private String configGroup;
    private String configParent;
    private JSONObject json;

    /**
     * 获取value中的string属性.
     *
     * @param key 键值.
     * @return 属性值.
     */
    public String getValueAttributeString(String key) {
        if (this.json == null) {
            this.json = JSON.parseObject(this.configValue);
        }
        return this.json.getString(key);
    }
}
