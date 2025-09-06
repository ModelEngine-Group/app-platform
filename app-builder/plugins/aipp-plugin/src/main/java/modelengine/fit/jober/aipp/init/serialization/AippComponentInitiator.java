/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.init.serialization;

import modelengine.jade.common.locale.LocaleUtil;

import modelengine.fit.jober.aipp.common.ResourceLoader;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Initialize;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * aipp流程和表单组件初始化
 *
 * @author 刘信宏
 * @since 2024-01-06
 */
@Component
public class AippComponentInitiator {
    /**
     * 流程/表单组件数据
     */
    public static final Map<String, String> COMPONENT_DATA = new HashMap<>();

    private static final Logger log = Logger.get(AippComponentInitiator.class);

    private static final String RESOURCE_PATH = "component";

    private static final String FLOW_ZH_PATH = "/flow_zh.json";

    private static final String FLOW_EN_PATH = "/flow_en.json";

    private static final String FORM_ZH_PATH = "/form_zh.json";

    private static final String FORM_EN_PATH = "/form_en.json";

    private static final String BASIC_NODE_ZH_PATH = "/basic_node_zh.json";

    private static final String BASIC_NODE_EN_PATH = "/basic_node_en.json";

    private static final String EVALUATION_NODE_ZH_PATH = "/evaluation_node_zh.json";

    private static final String EVALUATION_NODE_EN_PATH = "/evaluation_node_en.json";

    private final Plugin plugin;

    public AippComponentInitiator(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 根据语言获取国际化对象。
     *
     * @param enKey 表示英文键值的 {@link String}。
     * @param zhKey 表示中文键值的 {@link String}。
     * @param targetClass 表示类的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示泛型的 {@code <}{@link T}{@code >}。
     * @return 表示泛型实例的 {@link T}。
     */
    public static <T> T getLocaleObject(String enKey, String zhKey, Class<T> targetClass) {
        return JsonUtils.parseObject(LocaleUtil.getLocale().getLanguage().equals(Locale.ENGLISH.getLanguage())
                ? COMPONENT_DATA.get(enKey)
                : COMPONENT_DATA.get(zhKey), targetClass);
    }

    @Initialize
    private void loadComponentData() throws IOException {
        log.info("load aipp component data.");
        COMPONENT_DATA.put(AippConst.FLOW_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_ZH_PATH));
        COMPONENT_DATA.put(AippConst.FORM_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_ZH_PATH));
        COMPONENT_DATA.put(AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASIC_NODE_ZH_PATH));
        COMPONENT_DATA.put(AippConst.FLOW_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_EN_PATH));
        COMPONENT_DATA.put(AippConst.FORM_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_EN_PATH));
        COMPONENT_DATA.put(AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASIC_NODE_EN_PATH));
        COMPONENT_DATA.put(AippConst.EVALUATION_NODE_COMPONENT_DATA_ZH_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + EVALUATION_NODE_ZH_PATH));
        COMPONENT_DATA.put(AippConst.EVALUATION_NODE_COMPONENT_DATA_EN_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + EVALUATION_NODE_EN_PATH));
    }
}
