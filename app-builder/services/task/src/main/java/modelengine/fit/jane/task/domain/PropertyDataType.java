/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.domain.type.BooleanConverter;
import modelengine.fit.jane.task.domain.type.DateTimeConverter;
import modelengine.fit.jane.task.domain.type.IntegerConverter;
import modelengine.fit.jane.task.domain.type.ListTextConverter;
import modelengine.fit.jane.task.domain.type.TextConverter;
import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示任务属性的类型。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
public enum PropertyDataType implements DataConverter {
    /**
     * 表示文本类型。
     */
    TEXT(TextConverter.INSTANCE, "index_text"),

    /**
     * 表示整数。
     */
    INTEGER(IntegerConverter.INSTANCE, "index_integer"),

    /**
     * 表示日期时间。
     */
    DATETIME(DateTimeConverter.INSTANCE, "index_datetime"),

    /**
     * 表示布尔值。
     */
    BOOLEAN(BooleanConverter.INSTANCE, null),

    /**
     * 表示文本列表。
     */
    LIST_TEXT(ListTextConverter.INSTANCE, "index_text", "list_text", "TEXT");

    /**
     * 默认文本。
     */
    public static final PropertyDataType DEFAULT = TEXT;

    private final boolean isIndexable;
    private final String tableOfIndex;
    private final boolean isListable;
    private final String tableOfList;

    private final DataConverter converter;

    private final String elementTypeName;

    private PropertyDataType elementType;

    PropertyDataType(DataConverter converter, String tableOfIndex) {
        this(converter, tableOfIndex, null, null);
    }

    PropertyDataType(DataConverter converter, String tableOfIndex, String tableOfList, String elementTypeName) {
        this.tableOfIndex = StringUtils.trim(tableOfIndex);
        this.isIndexable = StringUtils.isNotEmpty(this.tableOfIndex);
        this.tableOfList = StringUtils.trim(tableOfList);
        this.isListable = StringUtils.isNotEmpty(this.tableOfList);
        this.elementTypeName = elementTypeName;
        this.converter = converter;
    }

    /**
     * 获取索引存储在的数据表的名称。
     *
     * @return 表示数据表的名称的 {@link String}。若为 {@code null}，则表示该类型不支持索引。
     */
    public String tableOfIndex() {
        return this.tableOfIndex;
    }

    /**
     * 获取一个值，该值指示当前数据类型是否支持索引。
     *
     * @return 若支持索引，则为 {@code true}，否则为 {@code false}。
     */
    public boolean indexable() {
        return this.isIndexable;
    }

    /**
     * 获取列表项的值存储在的数据表的名称。
     *
     * @return 表示用以存储列表项的数据表的名称的 {@link String}。
     */
    public String tableOfList() {
        return this.tableOfList;
    }

    /**
     * 获取一个值，该值指示当前类型的数据是否支持列举。
     *
     * @return 若支持列举，则为 {@code true}，否则为 {@code false}。
     */
    public boolean listable() {
        return this.isListable;
    }

    /**
     * 获取元素的类型。
     * <p>当 {@link #listable()} 为 {@code true} 时，表示列表中元素的类型，否则为 {@code null}。</p>
     *
     * @return 表示元素类型的 {@link PropertyDataType}。
     */
    public PropertyDataType elementType() {
        if (this.elementType == null && StringUtils.isNotBlank(this.elementTypeName)) {
            this.elementType = Enum.valueOf(PropertyDataType.class, this.elementTypeName);
        }
        return this.elementType;
    }

    @Override
    public Object fromExternal(Object value) {
        return this.converter.fromExternal(value);
    }

    @Override
    public Object toExternal(Object value) {
        return this.converter.toExternal(value);
    }

    @Override
    public Object fromPersistence(Object value) {
        return this.converter.fromPersistence(value);
    }

    @Override
    public Object toPersistence(Object value) {
        return this.converter.toPersistence(value);
    }

    @Override
    public ParsingResult<Object> parse(String text) {
        return this.converter.parse(text);
    }

    @Override
    public String toString(Object value) {
        return this.converter.toString(value);
    }
}
