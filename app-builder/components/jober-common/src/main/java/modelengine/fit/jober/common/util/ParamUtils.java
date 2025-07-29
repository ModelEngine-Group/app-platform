/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.util;

import modelengine.fit.jane.task.domain.File;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.entity.FileDeclaration;

import java.util.Objects;

/**
 * 工具类，用于处理参数转换。该工具类提供了一些静态方法，用于处理操作上下文信息。
 *
 * @author 陈镕希
 * @since 2023-09-25
 */
public class ParamUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ParamUtils() {
    }

    /**
     * 操作人相关信息转换。
     *
     * @param context 表示待转换的操作人信息的 {@link OperationContext}。
     * @return 表示转换后操作人信息的 {@link modelengine.fit.jane.task.util.OperationContext}。
     */
    public static modelengine.fit.jane.task.util.OperationContext convertToInternalOperationContext(
            OperationContext context) {
        modelengine.fit.jane.task.util.OperationContext operationContext;
        if (Objects.nonNull(context)) {
            operationContext = modelengine.fit.jane.task.util.OperationContext.custom()
                    .tenantId(context.getTenantId())
                    .operator(context.getOperator())
                    .operatorIp(context.getOperatorIp())
                    .sourcePlatform(context.getSourcePlatform())
                    .langage(context.getLanguage())
                    .build();
        } else {
            operationContext = modelengine.fit.jane.task.util.OperationContext.custom().build();
        }
        return operationContext;
    }

    /**
     * 操作人相关信息转换。
     *
     * @param context 表示待转换的操作人信息的 {@link OperationContext}。
     * @return 表示转换后操作人信息的 {@link modelengine.fit.jane.task.util.OperationContext}。
     */
    public static modelengine.fit.jane.task.util.OperationContext convertOperationContext(
            modelengine.fit.jober.entity.OperationContext context) {
        modelengine.fit.jane.task.util.OperationContext operationContext;
        if (Objects.nonNull(context)) {
            operationContext = modelengine.fit.jane.task.util.OperationContext.custom()
                    .tenantId(context.getTenantId())
                    .operator(context.getOperator())
                    .operatorIp(context.getOperatorIp())
                    .sourcePlatform(context.getSourcePlatform())
                    .langage(context.getLanguage())
                    .build();
        } else {
            operationContext = modelengine.fit.jane.task.util.OperationContext.custom().build();
        }
        return operationContext;
    }

    /**
     * 将任务模块的操作人信息转换为jober模块的操作人信息。
     *
     * @param context 表示待转换的操作人信息的 {@link modelengine.fit.jane.task.util.OperationContext}。
     * @return jober模块的操作人信息
     */
    public static modelengine.fit.jober.entity.OperationContext convertOperationContext(
            modelengine.fit.jane.task.util.OperationContext context) {
        modelengine.fit.jober.entity.OperationContext operationContext;
        operationContext = new modelengine.fit.jober.entity.OperationContext();
        if (Objects.nonNull(context)) {
            operationContext.setOperatorIp(context.operatorIp());
            operationContext.setOperator(context.operator());
            operationContext.setTenantId(context.tenantId());
            operationContext.setSourcePlatform(context.sourcePlatform());
            operationContext.setLanguage(context.language());
        }
        return operationContext;
    }

    /**
     * 将任务模块的操作人信息转换为jober模块的操作人信息。
     *
     * @param context 表示待转换的操作人信息的 {@link modelengine.fit.jane.task.util.OperationContext}。
     * @return jober模块的操作人信息
     */
    public static OperationContext convertToInternalOperationContext(
            modelengine.fit.jane.task.util.OperationContext context) {
        OperationContext operationContext = new OperationContext();
        if (Objects.nonNull(context)) {
            operationContext.setOperator(context.operator());
            operationContext.setOperatorIp(context.operatorIp());
            operationContext.setSourcePlatform(context.sourcePlatform());
            operationContext.setTenantId(context.tenantId());
            operationContext.setLanguage(context.language());
        }
        return operationContext;
    }

    /**
     * 将jober模块的文件信息转换为任务模块的文件信息。
     *
     * @param file 表示待转换的文件信息的 {@link modelengine.fit.jober.entity.File}。
     * @return 表示转换后文件信息的 {@link modelengine.fit.jane.task.domain.File}。
     */
    public static modelengine.fit.jane.task.domain.File convertFile(modelengine.fit.jober.entity.File file) {
        return modelengine.fit.jane.task.domain.File.custom().name(file.getName()).content(file.getContent()).build();
    }

    /**
     * 将任务模块的文件声明信息转换为jober模块的文件声明信息。
     *
     * @param fileDeclaration 表示待转换的文件声明信息的 {@link File.Declaration}。
     * @return 表示转换后文件声明信息的 {@link FileDeclaration}。
     */
    public static FileDeclaration convertDeclaration(File.Declaration fileDeclaration) {
        FileDeclaration declaration = new FileDeclaration();
        if (Objects.nonNull(fileDeclaration)) {
            declaration.setName(fileDeclaration.name().get());
            declaration.setContent(fileDeclaration.content().get());
        }
        return declaration;
    }

    /**
     * 将jober模块的文件声明信息转换为任务模块的文件声明信息。
     *
     * @param fileDeclaration 表示待转换的文件声明信息的 {@link FileDeclaration}。
     * @return 表示转换后文件声明信息的 {@link File.Declaration}。
     */
    public static File.Declaration convertDeclaration(FileDeclaration fileDeclaration) {
        return File.Declaration.custom().name(fileDeclaration.getName()).content(fileDeclaration.getContent()).build();
    }
}
