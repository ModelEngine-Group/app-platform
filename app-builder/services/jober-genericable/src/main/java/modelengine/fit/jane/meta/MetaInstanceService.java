/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.instance.Instance;
import modelengine.fit.jane.meta.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.instance.MetaInstanceFilter;
import modelengine.fit.jober.common.BadRequestException;
import modelengine.fit.jober.common.JoberGenericableException;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.TooManyRequestException;
import modelengine.fitframework.annotation.Genericable;

/**
 * 元数据实例服务
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public interface MetaInstanceService {
    /**
     * 创建meta实例。
     *
     * @param metaId meta定义唯一标识的 {@link String}。
     * @param instanceDeclarationInfo meta实例信息的 {@link InstanceDeclarationInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @return Instance
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "c0ddc4a5295e4a83a5a2b080d9eb10e3")
    Instance createMetaInstance(String metaId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context);

    /**
     * 更新meta实例。
     *
     * @param metaId meta定义唯一标识的 {@link String}。
     * @param instanceId meta实例唯一标识的 {@link String}。
     * @param instanceDeclarationInfo meta实例信息的 {@link InstanceDeclarationInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "bcf06f6f8c584967a63000fe7ad3cb76")
    void patchMetaInstance(String metaId, String instanceId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context);

    /**
     * 删除meta实例。
     *
     * @param metaId meta定义唯一标识的 {@link String}。
     * @param instanceId meta实例唯一标识的 {@link String}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "c906d0e6471749f2b41c9f2e9b7d9a0c")
    void deleteMetaInstance(String metaId, String instanceId, OperationContext context);

    /**
     * 查询meta实例。
     *
     * @param metaId 表示实例所属meta唯一标识的 {@link String}。
     * @param filter 表示meta实例过滤器的 {@link MetaInstanceFilter}。
     * @param offset 表示查询到的meta定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的meta定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Instance}{@code >}。
     */
    @Genericable(id = "0e5fbcac7d1c46e9812d3d0aaec88a43")
    RangedResultSet<Instance> list(String metaId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context);
}
