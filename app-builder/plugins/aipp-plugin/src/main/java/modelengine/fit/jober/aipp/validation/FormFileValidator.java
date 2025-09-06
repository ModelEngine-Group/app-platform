/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.validation;

import java.io.File;
import java.util.Map;

/**
 * 表单文件校验器
 *
 * @author 陈潇文
 * @since 2024/11/18
 */
public interface FormFileValidator {
    /**
     * 校验表单schema是否有问题
     *
     * @param config 表单schema
     */
    void validateSchema(Map<String, Object> config);

    /**
     * 校验表单预览图是否有问题
     *
     * @param file 表单预览图
     */
    void validateImg(File file);

    /**
     * 校验表单组件文件是否有问题
     *
     * @param directory 表单组件文件
     */
    void validateComponent(File directory);
}
