/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common;

import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 用于加载resource目录下的资源
 *
 * @author 夏斐
 * @since 2024/1/12
 */
public class ResourceLoader {
    private static final Logger log = Logger.get(ResourceLoader.class);

    /**
     * 加载指定path的文件内容
     *
     * @param plugin 资源加载器
     * @param path 文件路径
     * @return 文件内容
     * @throws IOException 文件的io异常
     */
    public static String loadFileData(Plugin plugin, String path) throws IOException {
        List<Resource> resources = Arrays.asList(plugin.resolverOfResources().resolve(path));
        if (resources.isEmpty()) {
            throw new IOException("component resource not found.");
        }
        String file = readInputStream(resources.get(0).read());
        if (StringUtils.isBlank(file)) {
            throw new IOException(path + " component file is empty.");
        }

        // 校验json
        if (!JsonUtils.isValidJson(file)) {
            throw new IOException(path + " component file is invalid json.");
        }
        return file;
    }

    /**
     * 读取输入流
     *
     * @param inputStream 输入流
     * @return 内容
     */
    private static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("read error {}", e.getMessage());
        }
        return "";
    }
}
