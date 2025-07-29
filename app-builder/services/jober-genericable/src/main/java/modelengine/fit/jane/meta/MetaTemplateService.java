/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.definition.MetaFilter;
import modelengine.fit.jane.meta.definition.MetaTemplate;
import modelengine.fit.jane.meta.definition.MetaTemplateDeclarationInfo;
import modelengine.fit.jane.meta.definition.MetaTemplateFilter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Genericable;

/**
 * 元数据模板服务。
 *
 * @author 陈镕希
 * @since 2024-02-04
 */
public interface MetaTemplateService {
    /**
     * 创建MetaTemplate。
     *
     * @param declaration 表示metaTemplate声明的 {@link MetaDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示创建好的metaTemplate定义的 {@link MetaTemplate}。
     */
    @Genericable(id = "c853a1d10a3b4a63b2f1c0c54289380a")
    MetaTemplate create(MetaTemplateDeclarationInfo declaration, OperationContext context);

    /**
     * 更新MetaTemplate。
     *
     * @param metaTemplateId 表示待更新的metaTemplate定义的唯一标识的 {@link String}。
     * @param declaration 表示metaTemplate声明的 {@link MetaDeclarationInfo}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "b3d826d88958472e93c25a1b1e1f626b")
    void patch(String metaTemplateId, MetaTemplateDeclarationInfo declaration, OperationContext context);

    /**
     * 删除MetaTemplate。
     *
     * @param metaTemplateId 表示待删除的metaTemplate定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "2a8c0c14616443bf9a4da781d1de197a")
    void delete(String metaTemplateId, OperationContext context);

    /**
     * 查询MetaTemplate。
     *
     * @param filter 表示metaTemplate过滤器的 {@link MetaFilter}。
     * @param offset 表示查询到的metaTemplate定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的metaTemplate定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link MetaTemplate}{@code >}。
     */
    @Genericable(id = "b4190f22ac704f398d0175a7fc6d3548")
    RangedResultSet<MetaTemplate> list(MetaTemplateFilter filter, long offset, int limit, OperationContext context);

    /**
     * 检索MetaTemplate。
     *
     * @param metaTemplateId 表示待检索的metaTemplate定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的metaTemplate定义的 {@link MetaTemplate}。
     */
    @Genericable(id = "65ea6b8e61ad496db7e54cc4dfb912bf")
    MetaTemplate retrieve(String metaTemplateId, OperationContext context);
}
