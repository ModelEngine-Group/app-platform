/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.multiversion;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fitframework.annotation.Genericable;

/**
 * 提供给AIPP的元数据服务。
 *
 * @author 陈镕希
 * @since 2024-02-08
 */
public interface MetaService {
    /**
     * 创建Meta。
     *
     * @param declaration 表示meta声明的 {@link MetaDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示创建好的meta定义的 {@link Meta}。
     */
    @Genericable(id = "1c67c2c811d74c339d8e58b8d786e946")
    Meta create(MetaDeclarationInfo declaration, OperationContext context);

    /**
     * 更新Meta。
     *
     * @param versionId 表示待更新的meta版本的唯一标识的 {@link String}。
     * @param declaration 表示meta声明的 {@link MetaDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "da01320a27c448d6a96d2d5ff75e2147")
    void patch(String versionId, MetaDeclarationInfo declaration, OperationContext context);

    /**
     * 发布Meta。
     *
     * @param versionId 表示待发布的meta版本的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "71757058c3404bcfa2b4e2a78c98e5e4")
    void publish(String versionId, OperationContext context);

    /**
     * 删除Meta。
     *
     * @param versionId 表示待删除的meta版本的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "0b88c0c4a8a44c1db3a6a217da151c47")
    void delete(String versionId, OperationContext context);

    /**
     * 查询Meta。
     *
     * @param filter 表示meta过滤器的 {@link MetaFilter}。
     * @param isLatestOnly 表示每个Meta是否只显示最新版本。
     * @param offset 表示查询到的meta定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的meta定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Meta}{@code >}。
     */
    @Genericable(id = "43a83cc619e04759aa5cf00e4d1c273c")
    RangedResultSet<Meta> list(MetaFilter filter, boolean isLatestOnly, long offset, int limit,
            OperationContext context);

    /**
     * 检索Meta。
     *
     * @param versionId 表示待检索的meta定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的meta定义的 {@link Meta}。
     */
    @Genericable(id = "ed0e01ecf6484b83a7c365c0fe6d647b")
    Meta retrieve(String versionId, OperationContext context);

    /**
     * 创建Property。
     *
     * @param versionId 表示meta版本唯一标识的 {@link String}。
     * @param declaration 表示meta属性声明的 {@link MetaPropertyDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示创建好的meta属性的 {@link TaskProperty}。
     */
    @Genericable(id = "50a97c8c9cf74707a4320a4a1764b0fe")
    TaskProperty createProperty(String versionId, MetaPropertyDeclarationInfo declaration, OperationContext context);

    /**
     * 更新Property。
     *
     * @param versionId 表示meta版本唯一标识的 {@link String}。
     * @param propertyId 表示待更新的property定义的唯一标识的 {@link String}。
     * @param declaration 表示meta属性声明的 {@link MetaPropertyDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "fae301849a9f4696a6f91a0f10e69a66")
    void patchProperty(String versionId, String propertyId, MetaPropertyDeclarationInfo declaration,
            OperationContext context);

    /**
     * 删除Property。
     *
     * @param versionId 表示meta版本唯一标识的 {@link String}。
     * @param propertyId 表示待删除的property定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "865245c74f0b49688abf1b4b881d16f8")
    void deleteProperty(String versionId, String propertyId, OperationContext context);
}
