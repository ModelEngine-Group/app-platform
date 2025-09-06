/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.UndefinableValue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link TenantMember}的默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-23
 */
public class DefaultTenantMember extends AbstractDomainObject implements TenantMember {
    private final String tenantId;

    private final String userId;

    /**
     * 构造一个新的{@link DefaultTenantMember}实例。
     *
     * @param id 租户成员的ID
     * @param tenantId 租户的ID
     * @param userId 用户的ID
     * @param creator 创建者
     * @param creationTime 创建时间
     * @param lastModifier 最后修改者
     * @param lastModificationTime 最后修改时间
     */
    public DefaultTenantMember(String id, String tenantId, String userId, String creator, LocalDateTime creationTime,
            String lastModifier, LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.tenantId = tenantId;
        this.userId = userId;
    }

    @Override
    public String tenantId() {
        return this.tenantId;
    }

    @Override
    public String userId() {
        return this.userId;
    }

    static class Declaration implements TenantMember.Declaration {
        private final UndefinableValue<String> tenantId;

        private final UndefinableValue<String> userId;

        Declaration(UndefinableValue<String> tenantId, UndefinableValue<String> userId) {
            this.tenantId = tenantId;
            this.userId = userId;
        }

        @Override
        public UndefinableValue<String> tenantId() {
            return this.tenantId;
        }

        @Override
        public UndefinableValue<String> userId() {
            return this.userId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Declaration) {
                Declaration another = (Declaration) obj;
                return Objects.equals(this.tenantId, another.tenantId) && Objects.equals(this.userId, another.userId);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.tenantId, this.userId});
        }

        @Override
        public String toString() {
            Map<String, Object> values = new HashMap<>(1);
            this.tenantId.ifDefined(value -> values.put("tenantId", value));
            this.userId.ifDefined(value -> values.put("userId", value));
            return values.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        static class Builder implements TenantMember.Declaration.Builder {
            private UndefinableValue<String> tenantId;

            private UndefinableValue<String> userId;

            Builder() {
                this.tenantId = UndefinableValue.undefined();
                this.userId = UndefinableValue.undefined();
            }

            @Override
            public TenantMember.Declaration.Builder tenantId(String tenantId) {
                this.tenantId = UndefinableValue.defined(tenantId);
                return this;
            }

            @Override
            public TenantMember.Declaration.Builder userId(String userId) {
                this.userId = UndefinableValue.defined(userId);
                return this;
            }

            @Override
            public TenantMember.Declaration build() {
                return new Declaration(this.tenantId, this.userId);
            }
        }
    }

    static class Builder extends AbstractDomainObjectBuilder<TenantMember,
        TenantMember.Builder> implements TenantMember.Builder {
        private String tenantId;

        private String userId;

        @Override
        public TenantMember.Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        @Override
        public TenantMember.Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public TenantMember build() {
            return new DefaultTenantMember(this.id(), this.tenantId, this.userId, this.creator(), this.creationTime(),
                    this.lastModifier(), this.lastModificationTime());
        }
    }

    /**
     * 为租户成员提供过滤配置。
     */
    public static class Filter implements TenantMember.Filter {
        private final UndefinableValue<List<String>> ids;

        private final UndefinableValue<String> tenantId;

        private final UndefinableValue<List<String>> userIds;

        public Filter(UndefinableValue<List<String>> ids, UndefinableValue<String> tenantId,
                UndefinableValue<List<String>> userIds) {
            this.ids = ids;
            this.tenantId = tenantId;
            this.userIds = userIds;
        }

        @Override
        public UndefinableValue<List<String>> ids() {
            return this.ids;
        }

        @Override
        public UndefinableValue<String> tenantId() {
            return this.tenantId;
        }

        @Override
        public UndefinableValue<List<String>> userIds() {
            return this.userIds;
        }

        /**
         * 为租户成员的过滤配置提供构建器。
         */
        public static class Builder implements TenantMember.Filter.Builder {
            private UndefinableValue<List<String>> ids;

            private UndefinableValue<String> tenantId;

            private UndefinableValue<List<String>> userIds;

            public Builder() {
                this.ids = UndefinableValue.undefined();
                this.tenantId = UndefinableValue.undefined();
                this.userIds = UndefinableValue.undefined();
            }

            @Override
            public TenantMember.Filter.Builder ids(List<String> ids) {
                this.ids = UndefinableValue.defined(ids);
                return this;
            }

            @Override
            public TenantMember.Filter.Builder tenantId(String tenantId) {
                this.tenantId = UndefinableValue.defined(tenantId);
                return this;
            }

            @Override
            public TenantMember.Filter.Builder userIds(List<String> userIds) {
                this.userIds = UndefinableValue.defined(userIds);
                return this;
            }

            @Override
            public TenantMember.Filter build() {
                return new Filter(this.ids, this.tenantId, this.userIds);
            }
        }
    }
}
