/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.task_new.serializer;

/**
 * 基础序列化接口，定义了领域对象与数据对象之间的转换方法
 *
 * @param <Domain> 领域对象类型
 * @param <PO> 数据对象类型
 * @author 邬涨财
 * @since 2025-03-31
 */
public interface BaseSerializer<Domain, PO> {
    /**
     * 将领域对象序列化为数据对象
     *
     * @param domain 待序列化的领域对象
     * @return 序列化后的数据对象
     */
    PO serialize(Domain domain);

    /**
     * 将数据对象反序列化为领域对象
     *
     * @param dataObject 待反序列化的数据对象
     * @return 反序列化后的领域对象
     */
    Domain deserialize(PO dataObject);
}
