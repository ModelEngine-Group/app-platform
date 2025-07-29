/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import com.opencsv.CSVWriter;

import modelengine.fitframework.annotation.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * csv 文件写入助手。
 *
 * @author 杨祥宇
 * @since 2025-04-18
 */
@Component
public class CsvWriterHelper {
    /**
     * 生成 CsvWriter 对象。
     *
     * @param path 表示文件路径的 {@link Path}。
     * @param isAppend 表示是否追加的 {@link Boolean}。
     * @return 表示生成 CSVWriter类的 {@link CSVWriter}。
     * @throws IOException 表示可能抛出异常的 {@link IOException}。
     */
    public CSVWriter createCsvWriter(Path path, boolean isAppend) throws IOException {
        return new CSVWriter(new FileWriter(path.toFile(), isAppend));
    }

    /**
     * 生成 File 对象。
     *
     * @param path 表示文件路径的 {@link Path}。
     * @return 表示生成 File 对象的 {@link File}。
     */
    public File getFile(String path) {
        return new File(path);
    }
}
