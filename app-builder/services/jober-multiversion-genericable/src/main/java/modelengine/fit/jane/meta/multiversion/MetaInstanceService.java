/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.multiversion;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.common.BadRequestException;
import modelengine.fit.jober.common.JoberGenericableException;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.TooManyRequestException;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 元数据实例服务
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public interface MetaInstanceService {
    /**
     * 根据给定的meta版本唯一标识和meta实例信息创建meta实例。
     *
     * @param versionId meta版本唯一标识的 {@link String}。
     * @param instanceDeclarationInfo meta实例信息的 {@link InstanceDeclarationInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @return 表示创建的meta实例的 {@link Instance}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "933aad4f2a694afa9b85dc294bb5b9d0")
    Instance createMetaInstance(String versionId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context);

    /**
     * 更新meta实例。
     *
     * @param versionId meta版本唯一标识的 {@link String}。
     * @param instanceId meta实例唯一标识的 {@link String}。
     * @param instanceDeclarationInfo meta实例信息的 {@link InstanceDeclarationInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "5f346f89a16741a2b4999b82fb37f25d")
    void patchMetaInstance(String versionId, String instanceId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context);

    /**
     * 删除meta实例。
     *
     * @param versionId meta版本唯一标识的 {@link String}。
     * @param instanceId meta实例唯一标识的 {@link String}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "be9f237d34d645ab9690fff1a344a01b")
    void deleteMetaInstance(String versionId, String instanceId, OperationContext context);

    /**
     * 查询meta实例。
     *
     * @param versionId 表示实例所属meta唯一标识的 {@link String}。
     * @param filter 表示 meta 实例过滤器的 {@link MetaInstanceFilter}。
     * @param offset 表示查询到的meta版本的结果集在全量结果集中的偏移量的 64 位整数的 {@code long}。
     * @param limit 表示查询到的meta版本的结果集中的最大数量的 32 位整数的 {@code int}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Instance}{@code >}。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b89ef4")
    RangedResultSet<Instance> list(String versionId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context);

    /**
     * 查询meta实例。
     *
     * @param versionId 表示实例所属meta唯一标识的 {@link String}。
     * @param offset 表示查询到的meta版本的结果集在全量结果集中的偏移量的 64 位整数的 {@code long}。
     * @param limit 表示查询到的meta版本的结果集中的最大数量的 32 位整数的 {@code int}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Instance}{@code >}。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b89eg4")
    RangedResultSet<Instance> list(String versionId, long offset, int limit, OperationContext context);

    /**
     * 查询meta实例。
     *
     * @param ids 表示实例id集合。
     * @param offset 表示查询到的meta版本的结果集在全量结果集中的偏移量的 64 位整数的 {@code long}。
     * @param limit 表示查询到的meta版本的结果集中的最大数量的 32 位整数的 {@code int}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Instance}{@code >}。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b89eg5")
    RangedResultSet<Instance> list(List<String> ids, long offset, int limit, OperationContext context);

    /**
     * 根据给定的实例唯一标识获取对应的 meta 唯一标识。
     *
     * @param id 表示实例唯一标识的 {@link String}。
     * @return 表示meta唯一标识的 {@link String}。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b89ef3")
    String getMetaVersionId(String id);

    /**
     * 根据给定的实例唯一标识获取对应的instance实例。
     *
     * @param instanceId 表示实例唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示instance的 {@link Instance}，没有找到时返回null。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b15e64")
    Instance retrieveById(String instanceId, OperationContext context);
}
