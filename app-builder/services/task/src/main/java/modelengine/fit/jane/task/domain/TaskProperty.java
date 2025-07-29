/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;
import java.util.Map;

/**
 * 表示任务定义中定义的属性。
 *
 * @author 梁济时
 * @since 2023-09-12
 */
public interface TaskProperty extends DomainObject {
    /**
     * 获取属性的名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    String name();

    /**
     * 获取属性的数据类型。
     *
     * @return 表示属性数据类型的 {@link PropertyDataType}。
     */
    PropertyDataType dataType();

    /**
     * 获取属性保存在的数据列的序号。
     * <p>这个序号在不同的数据类型间相互独立。</p>
     *
     * @return 表示属性序号的 32 位整数。
     */
    int sequence();

    /**
     * 获取任务属性的描述信息。
     *
     * @return 表示描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取一个值，该值指示属性的值是否是必须的。
     *
     * @return 若属性的值是必须的，则为 {@code true}，否则为 {@code false}。
     */
    boolean required();

    /**
     * 获取一个值，该值指示属性是否作为唯一标识使用。
     *
     * @return 若作为唯一标识使用，则为 {@code true}，否则为 {@code false}。
     */
    boolean identifiable();

    /**
     * 获取属性的使用范围。
     *
     * @return 表示属性使用范围的 {@link PropertyScope}。
     */
    PropertyScope scope();

    /**
     * 获取属性的外观信息。
     *
     * @return 表示属性的外观信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> appearance();

    /**
     * 获取属性的类目信息配置。
     *
     * @return 表示属性类目信息配置的 {@link List}{@code <}{@link PropertyCategory}{@code >}。
     */
    List<PropertyCategory> categories();

    /**
     * 获取属性存储在的数据列的名称。
     *
     * @return 表示数据列的名称的 {@link String}。
     */
    String column();

    /**
     * 为任务属性提供构建器。
     *
     * @author 梁济时
     * @since 2023-09-12
     */
    interface Builder extends DomainObject.Builder<TaskProperty, Builder> {
        /**
         * 设置属性的名称。
         *
         * @param name 表示属性的名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置属性的数据类型。
         *
         * @param dataType 表示属性的数据类型的 {@link PropertyDataType}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder dataType(PropertyDataType dataType);

        /**
         * 获取属性保存在的数据列的序号。
         * 设置>这个序号在不同的数据类型间相互独立。</p>
         *
         * @param sequence 表示属性序号的 32 位整数。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder sequence(int sequence);

        /**
         * 设置任务属性的描述信息。
         *
         * @param description 表示任务属性的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 设置一个值，该值指示属性的值是否是必须的。
         *
         * @param isRequired 表示一个值，该值指示属性的值是否是必须的的 {@code true}，否则为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isRequired(boolean isRequired);

        /**
         * 设置一个值，该值指示属性是否作为唯一标识使用。
         *
         * @param isIdentifiable 表示一个值，该值指示属性是否作为唯一标识使用的 {@code true}，否则为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isIdentifiable(boolean isIdentifiable);

        /**
         * 设置属性的使用范围。
         *
         * @param scope 表示属性的使用范围的 {@link PropertyScope}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder scope(PropertyScope scope);

        /**
         * 设置属性的外观信息。
         *
         * @param appearance 表示属性的外观信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder appearance(Map<String, Object> appearance);

        /**
         * 设置属性的类目信息配置。
         *
         * @param categories 表示属性的类目信息配置的 {@link List}{@code <}{@link PropertyCategory}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder categories(List<PropertyCategory> categories);
    }

    /**
     * 返回一个构建器，用以构建任务属性的新实例。
     *
     * @return 表示用以构建任务属性实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTaskProperty.Builder();
    }

    /**
     * 为任务属性提供定义。
     *
     * @author 梁济时
     * @since 2023-10-23
     */
    interface Declaration {
        /**
         * 获取任务属性的名称。
         *
         * @return 表示任务属性的名称的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> name();

        /**
         * 获取任务属性的m模板Id。
         *
         * @return 表示任务属性的模板Id的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> templateId();

        /**
         * 获取任务属性的数据类型。
         *
         * @return 表示任务属性的数据类型的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> dataType();

        /**
         * 获取任务属性的描述信息。
         *
         * @return 表示任务属性的描述信息的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> description();

        /**
         * 获取用以指示任务属性是否是必填项的值。
         *
         * @return 表示任务属性的是否必填的值的 {@link UndefinableValue}{@code <}{@link Boolean}{@code >}。
         */
        UndefinableValue<Boolean> required();

        /**
         * 获取用以指示任务属性是否是作为唯一标识使用。
         *
         * @return 表示任务属性的是否作为唯一标识使用的值的 {@link UndefinableValue}{@code <}{@link Boolean}{@code >}。
         */
        UndefinableValue<Boolean> identifiable();

        /**
         * 获取任务属性的使用范围。
         *
         * @return 表示任务属性的使用信息的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> scope();

        /**
         * 获取任务属性的外观信息。
         *
         * @return 表示任务属性的外观信息的 {@link UndefinableValue}{@code <}{@link Map}{@code <}
         * {@link String}{@code , }{@link Object}{@code >>}。
         */
        UndefinableValue<Map<String, Object>> appearance();

        /**
         * 获取属性的分类信息。
         *
         * @return 表示属性分类信息的 {@link UndefinableValue}{@code <}{@link List}{@code <}
         * {@link PropertyCategoryDeclaration}{@code >>}。
         */
        UndefinableValue<List<PropertyCategoryDeclaration>> categories();

        /**
         * 为属性的声明提供构建器。
         *
         * @author 梁济时
         * @since 2023-10-23
         */
        interface Builder {
            /**
             * 设置任务属性的名称。
             *
             * @param name 表示任务属性名称的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder name(String name);

            /**
             * 设置任务属性的模板Id。
             *
             * @param templateId 表示任务属性模板Id的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder templateId(String templateId);

            /**
             * 设置任务属性的数据类型。
             *
             * @param dataType 表示任务属性数据类型的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder dataType(String dataType);

            /**
             * 设置任务属性的描述信息。
             *
             * @param description 表示任务属性描述信息的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder description(String description);

            /**
             * 设置一个值，该值指示属性是否是必填项。
             *
             * @param isRequired 若为 {@code true}，则任务属性的必填的，否则为非必填。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder isRequired(Boolean isRequired);

            /**
             * 设置一个值，该值指示任务属性是否作为唯一标识使用。
             *
             * @param isIdentifiable 若为 {@code true}，则作为唯一标识是否，否则不作为唯一标识使用。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder isIdentifiable(Boolean isIdentifiable);

            /**
             * 设置任务属性的使用范围。
             *
             * @param scope 表示任务属性使用范围的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder scope(String scope);

            /**
             * 设置任务属性的外观信息。
             *
             * @param appearance 表示任务属性外观信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder appearance(Map<String, Object> appearance);

            /**
             * 设置任务属性的分类信息。
             *
             * @param categories 表示任务属性分类信息的 {@link List}{@code <}{@link PropertyCategoryDeclaration}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder categories(List<PropertyCategoryDeclaration> categories);

            /**
             * 构建任务属性声明的新实例。
             *
             * @return 表示新构建的任务属性声明的 {@link Declaration}。
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建任务属性声明的新实例。
         *
         * @return 表示用以构建任务属性声明新实例的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTaskProperty.Declaration.Builder();
        }
    }

    /**
     * 为任务属性提供存储能力。
     *
     * @author 梁济时
     * @since 2023-10-25
     */
    interface Repo {
        /**
         * 创建任务属性。
         *
         * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
         * @param declaration 表示待修补的内容的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的任务属性的 {@link TaskProperty}。
         */
        TaskProperty create(String taskId, Declaration declaration, OperationContext context);

        /**
         * 为指定的任务属性打一个补丁。
         *
         * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
         * @param propertyId 表示任务属性的唯一标识的 {@link String}。
         * @param declaration 表示待修补的内容的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void patch(String taskId, String propertyId, Declaration declaration, OperationContext context);

        /**
         * 删除指定任务属性。
         *
         * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
         * @param propertyId 表示任务属性的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(String taskId, String propertyId, OperationContext context);

        /**
         * 删除指定任务定义的属性。
         *
         * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void deleteByTask(String taskId, OperationContext context);

        /**
         * 检索指定任务属性。
         *
         * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
         * @param propertyId 表示任务属性的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示任务属性的 {@link TaskProperty}。
         */
        TaskProperty retrieve(String taskId, String propertyId, OperationContext context);

        /**
         * 列出指定任务定义的属性。
         *
         * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示任务属性的列表的 {@link List}{@code <}{@link TaskProperty}{@code >}。
         */
        List<TaskProperty> list(String taskId, OperationContext context);

        /**
         * 列出指定任务定义的属性。
         *
         * @param taskIds 表示任务属性所属任务定义的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示任务属性的列表的 {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}
         * {@link TaskProperty}{@code >>}。
         */
        Map<String, List<TaskProperty>> list(List<String> taskIds, OperationContext context);
    }
}
