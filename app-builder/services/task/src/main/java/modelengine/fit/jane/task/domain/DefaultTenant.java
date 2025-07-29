/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为 {@link Tenant} 提供默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-11
 */
public class DefaultTenant extends AbstractDomainObject implements Tenant {
    private final String name;

    private final String description;

    private final String avatarId;

    private final List<TenantMember> members;

    private List<String> tags;

    private TenantAccessLevel accessLevel;

    /**
     * 默认租户
     *
     * @param id 表示默认租户唯一标识的 {@link String}。
     * @param name 表示默认租户名称的 {@link String}。
     * @param description 表示默认租户描述的 {@link String}。
     * @param avatarId 表示默认租户avatarId的 {@link String}。
     * @param members 表示默认租户成员集合的 {@link List}{@code <}{@link String}{@code >>}。
     * @param tags 表示默认租户标签集合的 {@link List}{@code <}{@link String}{@code >>}。
     * @param creator 表示默认租户创建者的 {@link String}。
     * @param creationTime 表示默认租户创建时间的 {@link LocalDateTime}。
     * @param lastModifier 表示默认租户上次更新者的 {@link String}。
     * @param lastModificationTime 表示默认租户上次更新时间的 {@link LocalDateTime}。
     * @param accessLevel 表示默认租户级别的 {@link TenantAccessLevel}。
     */
    public DefaultTenant(String id, String name, String description, String avatarId, List<TenantMember> members,
            List<String> tags, String creator, LocalDateTime creationTime, String lastModifier,
            LocalDateTime lastModificationTime, TenantAccessLevel accessLevel) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.name = name;
        this.description = description;
        this.avatarId = avatarId;
        this.members = members;
        this.tags = tags;
        this.accessLevel = accessLevel;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String avatarId() {
        return this.avatarId;
    }

    @Override
    public List<TenantMember> members() {
        return this.members;
    }

    @Override
    public List<String> tags() {
        return this.tags;
    }

    @Override
    public TenantAccessLevel accessLevel() {
        return this.accessLevel;
    }

    @Override
    public boolean isPermitted(Repo repo, String userName, OperationContext context) {
        return repo.listMember(
                TenantMember.Filter.custom().tenantId(this.id()).userIds(Collections.singletonList(userName)).build(),
                0, 1, context).getRange().getTotal() > 0;
    }

    static class Declaration implements Tenant.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<String> description;

        private final UndefinableValue<String> avatarId;

        private final UndefinableValue<List<String>> members;

        private final UndefinableValue<List<String>> tags;

        private final UndefinableValue<TenantAccessLevel> accessLevel;

        Declaration(UndefinableValue<String> name, UndefinableValue<String> description,
                UndefinableValue<String> avatarId, UndefinableValue<List<String>> members,
                UndefinableValue<List<String>> tags, UndefinableValue<TenantAccessLevel> accessLevel) {
            this.name = name;
            this.description = description;
            this.avatarId = avatarId;
            this.members = members;
            this.tags = tags;
            this.accessLevel = accessLevel;
        }

        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        @Override
        public UndefinableValue<String> description() {
            return this.description;
        }

        @Override
        public UndefinableValue<String> avatarId() {
            return this.avatarId;
        }

        @Override
        public UndefinableValue<List<String>> members() {
            return this.members;
        }

        @Override
        public UndefinableValue<List<String>> tags() {
            return this.tags;
        }

        @Override
        public UndefinableValue<TenantAccessLevel> accessLevel() {
            return this.accessLevel;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Declaration) {
                Declaration another = (Declaration) obj;
                return Objects.equals(this.name, another.name) && Objects.equals(this.description, another.description)
                        && Objects.equals(this.avatarId, another.avatarId) && Objects.equals(this.members,
                        another.members) && Objects.equals(this.tags, another.tags);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {
                    this.getClass(), this.name, this.description, this.avatarId, this.members, this.tags
            });
        }

        @Override
        public String toString() {
            Map<String, Object> values = new HashMap<>(1);
            this.name.ifDefined(value -> values.put("name", value));
            this.description.ifDefined(value -> values.put("description", value));
            this.avatarId.ifDefined(value -> values.put("avatarId", value));
            this.members.ifDefined(value -> values.put("members", value));
            this.tags.ifDefined(value -> values.put("tags", value));
            this.accessLevel.ifDefined(value -> values.put("accessLevel", value));
            return values.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        static class Builder implements Tenant.Declaration.Builder {
            private UndefinableValue<String> name;

            private UndefinableValue<String> description;

            private UndefinableValue<String> avatarId;

            private UndefinableValue<List<String>> members;

            private UndefinableValue<List<String>> tags;

            /**
             * 访问级别
             */
            private UndefinableValue<TenantAccessLevel> accessLevel;

            Builder() {
                this.name = UndefinableValue.undefined();
                this.description = UndefinableValue.undefined();
                this.avatarId = UndefinableValue.undefined();
                this.members = UndefinableValue.undefined();
                this.tags = UndefinableValue.undefined();
                this.accessLevel = UndefinableValue.undefined();
            }

            @Override
            public Tenant.Declaration.Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            @Override
            public Tenant.Declaration.Builder description(String description) {
                this.description = UndefinableValue.defined(description);
                return this;
            }

            @Override
            public Tenant.Declaration.Builder avatarId(String avatarId) {
                this.avatarId = UndefinableValue.defined(avatarId);
                return this;
            }

            @Override
            public Tenant.Declaration.Builder members(List<String> members) {
                this.members = UndefinableValue.defined(members);
                return this;
            }

            @Override
            public Tenant.Declaration.Builder tags(List<String> tags) {
                this.tags = UndefinableValue.defined(tags);
                return this;
            }

            @Override
            public Tenant.Declaration.Builder accessLevel(TenantAccessLevel accessLevel) {
                this.accessLevel = UndefinableValue.defined(accessLevel);
                return this;
            }

            @Override
            public Tenant.Declaration build() {
                return new Declaration(this.name, this.description, this.avatarId, this.members, this.tags,
                        this.accessLevel);
            }
        }
    }

    static class Builder extends AbstractDomainObjectBuilder<Tenant, Tenant.Builder> implements Tenant.Builder {
        private String name;

        private String description;

        private String avatarId;

        private List<TenantMember> members;

        private List<String> tags;

        private TenantAccessLevel accessLevel;

        @Override
        public Tenant.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Tenant.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Tenant.Builder avatarId(String avatarId) {
            this.avatarId = avatarId;
            return this;
        }

        @Override
        public Tenant.Builder members(List<TenantMember> members) {
            this.members = members;
            return this;
        }

        @Override
        public Tenant.Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        @Override
        public Tenant.Builder accessLevel(TenantAccessLevel accessLevel) {
            this.accessLevel = accessLevel;
            return this;
        }

        @Override
        public Tenant build() {
            return new DefaultTenant(this.id(), this.name, this.description, this.avatarId, this.members, this.tags,
                    this.creator(), this.creationTime(), this.lastModifier(), this.lastModificationTime(),
                    this.accessLevel);
        }
    }

    /**
     * 为租户提供过滤配置。
     */
    public static class Filter implements Tenant.Filter {
        private final UndefinableValue<List<String>> ids;

        private final UndefinableValue<List<String>> names;

        private final UndefinableValue<List<String>> tags;

        private final UndefinableValue<List<TenantAccessLevel>> accessLevels;

        public Filter(UndefinableValue<List<String>> ids, UndefinableValue<List<String>> names,
                UndefinableValue<List<String>> tags, UndefinableValue<List<TenantAccessLevel>> accessLevels) {
            this.ids = ids;
            this.names = names;
            this.tags = tags;
            this.accessLevels = accessLevels;
        }

        @Override
        public UndefinableValue<List<String>> ids() {
            return this.ids;
        }

        @Override
        public UndefinableValue<List<String>> names() {
            return this.names;
        }

        @Override
        public UndefinableValue<List<String>> tags() {
            return this.tags;
        }

        @Override
        public UndefinableValue<List<TenantAccessLevel>> accessLevels() {
            return this.accessLevels;
        }

        /**
         * 为租户的过滤配置提供构建器。
         */
        public static class Builder implements Tenant.Filter.Builder {
            private UndefinableValue<List<String>> ids;

            private UndefinableValue<List<String>> names;

            private UndefinableValue<List<String>> tags;

            private UndefinableValue<List<TenantAccessLevel>> accessLevels;

            public Builder() {
                this.ids = UndefinableValue.undefined();
                this.names = UndefinableValue.undefined();
                this.tags = UndefinableValue.undefined();
                this.accessLevels = UndefinableValue.undefined();
            }

            @Override
            public Tenant.Filter.Builder ids(List<String> ids) {
                this.ids = UndefinableValue.defined(ids);
                return this;
            }

            @Override
            public Tenant.Filter.Builder names(List<String> names) {
                this.names = UndefinableValue.defined(names);
                return this;
            }

            @Override
            public Tenant.Filter.Builder tags(List<String> tags) {
                this.tags = UndefinableValue.defined(tags);
                return this;
            }

            @Override
            public Tenant.Filter.Builder accessLevels(List<TenantAccessLevel> accessLevels) {
                this.accessLevels = UndefinableValue.defined(accessLevels);
                return this;
            }

            @Override
            public Tenant.Filter build() {
                return new Filter(this.ids, this.names, this.tags, this.accessLevels);
            }
        }
    }
}
