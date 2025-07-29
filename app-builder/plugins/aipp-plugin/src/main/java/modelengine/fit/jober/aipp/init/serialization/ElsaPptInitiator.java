/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.init.serialization;

import modelengine.fit.jober.aipp.common.ResourceLoader;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Initialize;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * elsa ppt所需的配置初始化
 *
 * @author 夏斐
 * @since 2024-01-12
 */
@Component
public class ElsaPptInitiator {
    /**
     * 流程/表单组件数据
     *
     * @author 孙怡菲
     * @since 2024/05/10
     */
    public static final Map<String, String> ELSA_PPT_DATA = new HashMap<>();

    /**
     * 图表
     */
    public static final String GRAPH_KEY = "graph";

    /**
     * 封面
     */
    public static final String COVER_PAGE_KEY = "cover_page";

    /**
     * 内容页
     */
    public static final String CONTENT_PAGE_KEY = "content_page";

    /**
     * 基本封面页
     */
    public static final String BASE_COVER_PAGE_KEY = "base_cover_page";

    /**
     * 基本内容页
     */
    public static final String BASE_CONTENT_PAGE_KEY = "base_content_page";

    private static final Logger log = Logger.get(ElsaPptInitiator.class);

    private static final String RESOURCE_PATH = "elsa_ppt";

    private static final String GRAPH_PATH = "/graph.json";

    private static final String COVER_PAGE_PATH = "/cover_page.json";

    private static final String CONTENT_PAGE_PATH = "/content_page.json";

    private static final String BASE_COVER_PAGE_PATH = "/base_cover_page.json";

    private static final String BASE_CONTENT_PAGE_PATH = "/base_content_page.json";

    private final Plugin plugin;

    public ElsaPptInitiator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Initialize
    private void loadElsaPptData() throws IOException {
        log.info("load elsa ppt data.");
        ELSA_PPT_DATA.put(GRAPH_KEY, ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + GRAPH_PATH));
        ELSA_PPT_DATA.put(COVER_PAGE_KEY, ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + COVER_PAGE_PATH));
        ELSA_PPT_DATA.put(CONTENT_PAGE_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + CONTENT_PAGE_PATH));
        ELSA_PPT_DATA.put(BASE_COVER_PAGE_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASE_COVER_PAGE_PATH));
        ELSA_PPT_DATA.put(BASE_CONTENT_PAGE_KEY,
                ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASE_CONTENT_PAGE_PATH));
    }
}
