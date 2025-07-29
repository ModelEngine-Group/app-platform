/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.definition.Meta;
import modelengine.fit.jane.meta.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.definition.MetaFilter;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fitframework.annotation.Genericable;

/**
 * 元数据服务。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public interface MetaService {
    /**
     * 创建Meta。
     *
     * @param declaration 表示meta声明的 {@link MetaDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示创建好的meta定义的 {@link Meta}。
     */
    @Genericable(id = "ab0c4d67e3d04e32b9a264ba7d49d887")
    Meta create(MetaDeclarationInfo declaration, OperationContext context);

    /**
     * 更新Meta。
     *
     * @param metaId 表示待更新的meta定义的唯一标识的 {@link String}。
     * @param declaration 表示meta声明的 {@link MetaDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "e4a87838224044cfbfc7e6ee9b3a7e80")
    void patch(String metaId, MetaDeclarationInfo declaration, OperationContext context);

    /**
     * 删除Meta。
     *
     * @param metaId 表示待删除的meta定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "0385087baad94b9c8d0412c0056a1a2e")
    void delete(String metaId, OperationContext context);

    /**
     * 查询Meta。
     *
     * @param filter 表示meta过滤器的 {@link MetaFilter}。
     * @param offset 表示查询到的meta定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的meta定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Meta}{@code >}。
     */
    @Genericable(id = "0f012742d8f94cb78f24f5a1c4326a4c")
    RangedResultSet<Meta> list(MetaFilter filter, long offset, int limit, OperationContext context);

    /**
     * 检索Meta。
     *
     * @param metaId 表示待检索的meta定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的meta定义的 {@link Meta}。
     */
    @Genericable(id = "f8df2a308c76412d9fbdefffd23e54a0")
    Meta retrieve(String metaId, OperationContext context);

    /**
     * 创建Property。
     *
     * @param metaId 表示meta定义唯一标识的 {@link String}。
     * @param declaration 表示meta属性声明的 {@link MetaPropertyDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示创建好的meta属性的 {@link TaskProperty}。
     */
    @Genericable(id = "a090aae3ad7f47f3b100bc488bc4f1fe")
    TaskProperty createProperty(String metaId, MetaPropertyDeclarationInfo declaration, OperationContext context);

    /**
     * 更新Property。
     *
     * @param metaId 表示meta定义唯一标识的 {@link String}。
     * @param propertyId 表示待更新的property定义的唯一标识的 {@link String}。
     * @param declaration 表示meta属性声明的 {@link MetaPropertyDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "1b827b84ab7f47c9a6b6b19b18e345df")
    void patchProperty(String metaId, String propertyId, MetaPropertyDeclarationInfo declaration,
            OperationContext context);

    /**
     * 删除Property。
     *
     * @param metaId 表示meta定义唯一标识的 {@link String}。
     * @param propertyId 表示待删除的property定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "4f78b13f19c540d3a98b43a738eb7167")
    void deleteProperty(String metaId, String propertyId, OperationContext context);
}
