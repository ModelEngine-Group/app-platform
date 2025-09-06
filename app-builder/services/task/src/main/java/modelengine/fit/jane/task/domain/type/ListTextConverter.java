/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain.type;

/**
 * 为文本列表提供数据转换器。
 *
 * @author 梁济时
 * @since 2024-01-24
 */
public class ListTextConverter extends AbstractListDataConverter {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final ListTextConverter INSTANCE = new ListTextConverter();

    private ListTextConverter() {
        super(TextConverter.INSTANCE);
    }
}
