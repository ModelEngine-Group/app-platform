/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 为{@link TaskRelation}提供默认实现。
 *
 * @author 罗书强
 * @since 2023-12-29
 */
public class DefaultTaskRelation implements TaskRelation {
    private final String id;

    private final String objectId1;

    private final String objectType1;

    private final String objectId2;

    private final String objectType2;

    private final String relationType;

    private final String createdBy;

    private final LocalDateTime createdAt;

    public DefaultTaskRelation(String id, String objectId1, String objectType1, String objectId2, String objectType2,
            String relationType, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.objectId1 = objectId1;
        this.objectType1 = objectType1;
        this.objectId2 = objectId2;
        this.objectType2 = objectType2;
        this.relationType = relationType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String objectId1() {
        return this.objectId1;
    }

    @Override
    public String objectType1() {
        return this.objectType1;
    }

    @Override
    public String objectId2() {
        return this.objectId2;
    }

    @Override
    public String objectType2() {
        return this.objectType2;
    }

    @Override
    public String relationType() {
        return this.relationType;
    }

    @Override
    public String createdBy() {
        return this.createdBy;
    }

    @Override
    public LocalDateTime createdAt() {
        return this.createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultTaskRelation that = (DefaultTaskRelation) obj;
            return Objects.equals(this.id(), that.id()) && Objects.equals(this.objectId1(), that.objectId1())
                    && Objects.equals(this.objectType1(), that.objectType1()) && Objects.equals(this.objectId2(),
                    that.objectId2()) && Objects.equals(this.objectType2(), that.objectType2()) && Objects.equals(
                    this.relationType(), that.relationType()) && Objects.equals(this.createdBy(), that.createdBy())
                    && Objects.equals(this.createdAt(), that.createdAt());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.id(), this.objectId1(), this.objectType1(), this.objectId2(), this.objectType2(),
                this.relationType(), this.createdBy(), this.createdAt()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "[id={0}, objectId1={1}, objectType1={2}, objectId2={3}, objectType2={4}, relationType={5},"
                        + " createdBy={6}, createdAt={7}]", this.id(), this.objectId1(), this.objectType1(),
                this.objectId2(), this.objectType2(), this.relationType(), this.createdBy(), this.createdAt());
    }

    /**
     * 为{@link TaskRelation.Builder}提供默认实现。
     *
     * @author 罗书强
     * @since 2023-12-29
     */
    static class Builder implements TaskRelation.Builder {
        private String id;

        private String objectId1;

        private String objectType1;

        private String objectId2;

        private String objectType2;

        private String relationType;

        private String createdBy;

        private LocalDateTime createdAt;

        @Override
        public TaskRelation.Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public TaskRelation.Builder objectId1(String objectId1) {
            this.objectId1 = objectId1;
            return this;
        }

        @Override
        public TaskRelation.Builder objectType1(String objectType1) {
            this.objectType1 = objectType1;
            return this;
        }

        @Override
        public TaskRelation.Builder objectId2(String objectId2) {
            this.objectId2 = objectId2;
            return this;
        }

        @Override
        public TaskRelation.Builder objectType2(String objectType2) {
            this.objectType2 = objectType2;
            return this;
        }

        @Override
        public TaskRelation.Builder relationType(String relationType) {
            this.relationType = relationType;
            return this;
        }

        @Override
        public TaskRelation.Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        @Override
        public TaskRelation.Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Override
        public TaskRelation build() {
            return new DefaultTaskRelation(this.id, this.objectId1, this.objectType1, this.objectId2, this.objectType2,
                    this.relationType, this.createdBy, this.createdAt);
        }
    }

    /**
     * 为{@link TaskRelation.Declaration}提供默认实现。
     *
     * @author 罗书强
     * @since 2023-12-29
     */
    static class Declaration implements TaskRelation.Declaration {
        private final UndefinableValue<String> id;

        private final UndefinableValue<String> objectId1;

        private final UndefinableValue<String> objectType1;

        private final UndefinableValue<String> objectId2;

        private final UndefinableValue<String> objectType2;

        private final UndefinableValue<String> relationType;

        private final UndefinableValue<String> createdBy;

        private final UndefinableValue<LocalDateTime> createdAt;

        Declaration(UndefinableValue<String> id, UndefinableValue<String> objectId1,
                UndefinableValue<String> objectType1, UndefinableValue<String> objectId2,
                UndefinableValue<String> objectType2, UndefinableValue<String> relationType,
                UndefinableValue<String> createdBy, UndefinableValue<LocalDateTime> createdAt) {
            this.id = id;
            this.objectId1 = objectId1;
            this.objectType1 = objectType1;
            this.objectId2 = objectId2;
            this.objectType2 = objectType2;
            this.relationType = relationType;
            this.createdBy = createdBy;
            this.createdAt = createdAt;
        }

        @Override
        public UndefinableValue<String> id() {
            return this.id;
        }

        @Override
        public UndefinableValue<String> objectId1() {
            return this.objectId1;
        }

        @Override
        public UndefinableValue<String> objectType1() {
            return this.objectType1;
        }

        @Override
        public UndefinableValue<String> objectId2() {
            return this.objectId2;
        }

        @Override
        public UndefinableValue<String> objectType2() {
            return this.objectType2;
        }

        @Override
        public UndefinableValue<String> relationType() {
            return this.relationType;
        }

        @Override
        public UndefinableValue<String> createdBy() {
            return this.createdBy;
        }

        @Override
        public UndefinableValue<LocalDateTime> createdAt() {
            return this.createdAt;
        }

        /**
         * 为{@link TaskRelation.Declaration.Builder}提供默认实现。
         *
         * @author 罗书强
         * @since 2023-12-29
         */
        static class Builder implements TaskRelation.Declaration.Builder {
            private UndefinableValue<String> id;

            private UndefinableValue<String> objectId1;

            private UndefinableValue<String> objectType1;

            private UndefinableValue<String> objectId2;

            private UndefinableValue<String> objectType2;

            private UndefinableValue<String> relationType;

            private UndefinableValue<String> createdBy;

            private UndefinableValue<LocalDateTime> createdAt;

            Builder() {
                this.id = UndefinableValue.undefined();
                this.objectId1 = UndefinableValue.undefined();
                this.objectType1 = UndefinableValue.undefined();
                this.objectId2 = UndefinableValue.undefined();
                this.objectType2 = UndefinableValue.undefined();
                this.relationType = UndefinableValue.undefined();
                this.createdBy = UndefinableValue.undefined();
                this.createdAt = UndefinableValue.undefined();
            }

            @Override
            public TaskRelation.Declaration.Builder id(String id) {
                this.id = UndefinableValue.defined(id);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder objectId1(String objectId1) {
                this.objectId1 = UndefinableValue.defined(objectId1);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder objectType1(String objectType1) {
                this.objectType1 = UndefinableValue.defined(objectType1);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder objectId2(String objectId2) {
                this.objectId2 = UndefinableValue.defined(objectId2);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder objectType2(String objectType2) {
                this.objectType2 = UndefinableValue.defined(objectType2);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder relationType(String relationType) {
                this.relationType = UndefinableValue.defined(relationType);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder createdBy(String createdBy) {
                this.createdBy = UndefinableValue.defined(createdBy);
                return this;
            }

            @Override
            public TaskRelation.Declaration.Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = UndefinableValue.defined(createdAt);
                return this;
            }

            @Override
            public TaskRelation.Declaration build() {
                return new Declaration(this.id, this.objectId1, this.objectType1, this.objectId2, this.objectType2,
                        this.relationType, this.createdBy, this.createdAt);
            }
        }
    }

    /**
     * 为 {@link TaskRelation.Filter} 提供默认实现。
     *
     * @author 罗书强
     * @since 2024-01-03
     */
    static class Filter implements TaskRelation.Filter {
        private final List<String> ids;

        private final List<String> objectId1s;

        private final List<String> objectId2s;

        Filter(List<String> ids, List<String> objectId1s, List<String> objectId2s) {
            this.ids = ids;
            this.objectId1s = objectId1s;
            this.objectId2s = objectId2s;
        }

        @Override
        public List<String> ids() {
            return this.ids;
        }

        @Override
        public List<String> objectId1s() {
            return this.objectId1s;
        }

        @Override
        public List<String> objectId2s() {
            return this.objectId2s;
        }

        /**
         * 为 {@link TaskRelation.Filter.Builder} 提供默认实现。
         *
         * @author 罗书强
         * @since 2024-01-03
         */
        static class Builder implements TaskRelation.Filter.Builder {
            private List<String> ids;

            private List<String> objectId1s;

            private List<String> objectId2s;

            /**
             * 设置待查询的唯一标识的列表。
             *
             * @param ids 表示唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link TaskRelation.Filter.Builder}。
             */
            @Override
            public TaskRelation.Filter.Builder ids(List<String> ids) {
                this.ids = ids;
                return this;
            }

            /**
             * 设置待查询的关联方唯一标识的列表。
             *
             * @param objectId1s 表示关联方的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link TaskRelation.Filter.Builder}。
             */
            @Override
            public TaskRelation.Filter.Builder objectId1s(List<String> objectId1s) {
                this.objectId1s = objectId1s;
                return this;
            }

            /**
             * 设置待查询的被关联方的唯一标识的列表。
             *
             * @param objectId2s 表示被关联方的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link TaskRelation.Filter.Builder}。
             */
            @Override
            public TaskRelation.Filter.Builder objectId2s(List<String> objectId2s) {
                this.objectId2s = objectId2s;
                return this;
            }

            /**
             * 构建任务关联的过滤条件。
             *
             * @return 表示任务关联的过滤条件的 {@link Filter}。
             */
            @Override
            public TaskRelation.Filter build() {
                return new Filter(this.ids, this.objectId1s, this.objectId2s);
            }
        }
    }
}
