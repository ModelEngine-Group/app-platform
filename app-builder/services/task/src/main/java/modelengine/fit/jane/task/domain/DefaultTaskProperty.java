/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link TaskProperty} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-10-09
 */
class DefaultTaskProperty extends AbstractDomainObject implements TaskProperty {
    private final String name;

    private final PropertyDataType dataType;

    private final int sequence;

    private final String description;

    private final boolean isRequired;

    private final boolean isIdentifiable;

    private final PropertyScope scope;

    private final Map<String, Object> appearance;

    private final List<PropertyCategory> categories;

    DefaultTaskProperty(String id, String name, PropertyDataType dataType, int sequence, String description,
            boolean isRequired, boolean isIdentifiable, PropertyScope scope, Map<String, Object> appearance,
            List<PropertyCategory> categories, String creator, LocalDateTime creationTime, String lastModifier,
            LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.name = name;
        this.dataType = dataType;
        this.sequence = sequence;
        this.description = description;
        this.isRequired = isRequired;
        this.isIdentifiable = isIdentifiable;
        this.scope = scope;
        this.appearance = appearance;
        this.categories = categories;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public PropertyDataType dataType() {
        return this.dataType;
    }

    @Override
    public int sequence() {
        return this.sequence;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public boolean required() {
        return this.isRequired;
    }

    @Override
    public boolean identifiable() {
        return this.isIdentifiable;
    }

    @Override
    public PropertyScope scope() {
        return this.scope;
    }

    @Override
    public Map<String, Object> appearance() {
        return this.appearance;
    }

    @Override
    public List<PropertyCategory> categories() {
        return this.categories;
    }

    @Override
    public String column() {
        String column = this.dataType().toString() + "_" + this.sequence();
        return StringUtils.toLowerCase(column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultTaskProperty) {
            DefaultTaskProperty another = (DefaultTaskProperty) obj;
            return Objects.equals(this.id(), another.id()) && Objects.equals(this.name, another.name)
                    && this.dataType == another.dataType && this.sequence == another.sequence && Objects.equals(
                    this.description, another.description) && this.isRequired == another.isRequired
                    && this.isIdentifiable == another.isIdentifiable && this.scope == another.scope && Entities.equals(
                    this.appearance, another.appearance) && Entities.equals(this.categories, another.categories);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        List<PropertyCategory> filterCategories = Optional.ofNullable(this.categories)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .sorted(Comparator.comparing(PropertyCategory::getValue).thenComparing(PropertyCategory::getCategory))
                .collect(Collectors.toList());
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.id(), this.name, this.dataType, this.sequence, this.description, this.isRequired,
                this.isIdentifiable, this.scope, this.appearance, filterCategories, this.creator(), this.creationTime(),
                this.lastModifier(), this.lastModificationTime()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, name={1}, dataType={2}, sequence={3}, description={4}, required={5}, "
                        + "identifiable={6}, scope={7}, appearance={8}, categories={9}, creator={10}, "
                        + "creationTime={11}, lastModifier={12}, lastModification={13}]",
                this.id(), this.name, this.id(),
                this.name, this.dataType, this.sequence, this.description, this.isRequired, this.isIdentifiable,
                this.scope, this.appearance, this.categories, this.creator(), this.creationTime(), this.lastModifier(),
                this.lastModificationTime());
    }

    static class Builder extends AbstractDomainObjectBuilder<TaskProperty, TaskProperty.Builder>
            implements TaskProperty.Builder {
        private String name;

        private PropertyDataType dataType;

        private int sequence;

        private String description;

        private boolean isRequired;

        private boolean isIdentifiable;

        private PropertyScope scope;

        private Map<String, Object> appearance;

        private List<PropertyCategory> categories;

        @Override
        public TaskProperty.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public TaskProperty.Builder dataType(PropertyDataType dataType) {
            this.dataType = dataType;
            return this;
        }

        @Override
        public TaskProperty.Builder sequence(int sequence) {
            this.sequence = sequence;
            return this;
        }

        @Override
        public TaskProperty.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public TaskProperty.Builder isRequired(boolean isRequired) {
            this.isRequired = isRequired;
            return this;
        }

        @Override
        public TaskProperty.Builder isIdentifiable(boolean isIdentifiable) {
            this.isIdentifiable = isIdentifiable;
            return this;
        }

        @Override
        public TaskProperty.Builder scope(PropertyScope scope) {
            this.scope = scope;
            return this;
        }

        @Override
        public TaskProperty.Builder appearance(Map<String, Object> appearance) {
            this.appearance = appearance;
            return this;
        }

        @Override
        public TaskProperty.Builder categories(List<PropertyCategory> categories) {
            this.categories = categories;
            return this;
        }

        @Override
        public TaskProperty build() {
            return new DefaultTaskProperty(this.id(), this.name, this.dataType, this.sequence, this.description,
                    this.isRequired, this.isIdentifiable, this.scope, this.appearance, this.categories, this.creator(),
                    this.creationTime(), this.lastModifier(), this.lastModificationTime());
        }
    }

    static class Declaration implements TaskProperty.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<String> dataType;

        private final UndefinableValue<String> description;

        private final UndefinableValue<Boolean> required;

        private final UndefinableValue<Boolean> identifiable;

        private final UndefinableValue<String> scope;

        private final UndefinableValue<Map<String, Object>> appearance;

        private final UndefinableValue<List<PropertyCategoryDeclaration>> categories;

        private final UndefinableValue<String> templateId;

        Declaration(UndefinableValue<String> name, UndefinableValue<String> templateId,
                UndefinableValue<String> dataType, UndefinableValue<String> description,
                UndefinableValue<Boolean> required, UndefinableValue<Boolean> identifiable,
                UndefinableValue<String> scope, UndefinableValue<Map<String, Object>> appearance,
                UndefinableValue<List<PropertyCategoryDeclaration>> categories) {
            this.name = name;
            this.dataType = dataType;
            this.description = description;
            this.required = required;
            this.identifiable = identifiable;
            this.scope = scope;
            this.appearance = appearance;
            this.categories = categories;
            this.templateId = templateId;
        }

        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        @Override
        public UndefinableValue<String> templateId() {
            return this.templateId;
        }

        @Override
        public UndefinableValue<String> dataType() {
            return this.dataType;
        }

        @Override
        public UndefinableValue<String> description() {
            return this.description;
        }

        @Override
        public UndefinableValue<Boolean> required() {
            return this.required;
        }

        @Override
        public UndefinableValue<Boolean> identifiable() {
            return this.identifiable;
        }

        @Override
        public UndefinableValue<String> scope() {
            return this.scope;
        }

        @Override
        public UndefinableValue<Map<String, Object>> appearance() {
            return this.appearance;
        }

        @Override
        public UndefinableValue<List<PropertyCategoryDeclaration>> categories() {
            return this.categories;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            this.name().ifDefined(value -> builder.append("name=").append(value).append(", "));
            this.dataType().ifDefined(value -> builder.append("dataType=").append(value).append(", "));
            this.description().ifDefined(value -> builder.append("description=").append(value).append(", "));
            this.required().ifDefined(value -> builder.append("required=").append(value).append(", "));
            this.identifiable().ifDefined(value -> builder.append("identifiable=").append(value).append(", "));
            this.scope().ifDefined(value -> builder.append("scope=").append(value).append(", "));
            this.appearance().ifDefined(value -> builder.append("appearance=").append(value).append(", "));
            this.categories().ifDefined(value -> builder.append("categories=").append(value).append(", "));
            if (builder.length() > 1) {
                builder.setLength(builder.length() - 2);
            }
            builder.append(']');
            return builder.toString();
        }

        static class Builder implements TaskProperty.Declaration.Builder {
            private UndefinableValue<String> name;

            private UndefinableValue<String> dataType;

            private UndefinableValue<String> description;

            private UndefinableValue<Boolean> isRequired;

            private UndefinableValue<Boolean> isIdentifiable;

            private UndefinableValue<String> scope;

            private UndefinableValue<Map<String, Object>> appearance;

            private UndefinableValue<List<PropertyCategoryDeclaration>> categories;

            private UndefinableValue<String> templateId;

            Builder() {
                this.name = UndefinableValue.undefined();
                this.dataType = UndefinableValue.undefined();
                this.description = UndefinableValue.undefined();
                this.isRequired = UndefinableValue.undefined();
                this.isIdentifiable = UndefinableValue.undefined();
                this.scope = UndefinableValue.undefined();
                this.appearance = UndefinableValue.undefined();
                this.categories = UndefinableValue.undefined();
                this.templateId = UndefinableValue.undefined();
            }

            @Override
            public Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            @Override
            public TaskProperty.Declaration.Builder templateId(String templateId) {
                this.templateId = UndefinableValue.defined(templateId);
                return this;
            }

            @Override
            public Builder dataType(String dataType) {
                this.dataType = UndefinableValue.defined(dataType);
                return this;
            }

            @Override
            public Builder description(String description) {
                this.description = UndefinableValue.defined(description);
                return this;
            }

            @Override
            public Builder isRequired(Boolean isRequired) {
                this.isRequired = UndefinableValue.defined(isRequired);
                return this;
            }

            @Override
            public Builder isIdentifiable(Boolean isIdentifiable) {
                this.isIdentifiable = UndefinableValue.defined(isIdentifiable);
                return this;
            }

            @Override
            public Builder scope(String scope) {
                this.scope = UndefinableValue.defined(scope);
                return this;
            }

            @Override
            public Builder appearance(Map<String, Object> appearance) {
                this.appearance = UndefinableValue.defined(appearance);
                return this;
            }

            @Override
            public Builder categories(List<PropertyCategoryDeclaration> categories) {
                this.categories = UndefinableValue.defined(categories);
                return this;
            }

            @Override
            public TaskProperty.Declaration build() {
                return new Declaration(this.name, this.templateId, this.dataType, this.description, this.isRequired,
                        this.isIdentifiable, this.scope, this.appearance, this.categories);
            }
        }
    }
}
