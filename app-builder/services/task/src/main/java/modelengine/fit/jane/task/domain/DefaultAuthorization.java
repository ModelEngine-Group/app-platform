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
 * 为 {@link Authorization} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-11-27
 */
class DefaultAuthorization extends AbstractDomainObject implements Authorization {
    private final String system;

    private final String user;

    private final String token;

    private final Long expiration;

    /**
     * 构造函数
     *
     * @param id id
     * @param system 系统
     * @param user 用户
     * @param token token
     * @param expiration 失效时间
     * @param creator 创建者
     * @param creationTime 创建时间
     * @param lastModifier 最后更新人
     * @param lastModificationTime 最后更新时间
     */
    DefaultAuthorization(String id, String system, String user, String token, Long expiration, String creator,
            LocalDateTime creationTime, String lastModifier, LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.system = system;
        this.user = user;
        this.token = token;
        this.expiration = expiration;
    }

    @Override
    public String system() {
        return this.system;
    }

    @Override
    public String user() {
        return this.user;
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public Long expiration() {
        return this.expiration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultAuthorization that = (DefaultAuthorization) obj;
            return Objects.equals(this.id(), that.id()) && Objects.equals(this.system(), that.system())
                    && Objects.equals(this.user(), that.user()) && Objects.equals(this.token(), that.token())
                    && Objects.equals(this.expiration(), that.expiration()) && Objects.equals(this.creator(),
                    that.creator()) && Objects.equals(this.creationTime(), that.creationTime()) && Objects.equals(
                    this.lastModifier(), that.lastModifier()) && Objects.equals(this.lastModificationTime(),
                    that.lastModificationTime());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.id(), this.system(), this.user(), this.token(), this.expiration(), this.creator(),
                this.creationTime(), this.lastModifier(), this.lastModificationTime()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "[id={0}, system={1}, user={2}, token={3}, expiration={4}, creator={5}, creationTime={6},"
                        + " lastModifier={7}, lastModificationTime={8}]", this.id(), this.system(), this.user(),
                this.token(), this.expiration(), this.creator(), this.creationTime(), this.lastModifier(),
                this.lastModificationTime());
    }

    /**
     * 为 {@link Authorization.Builder} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    static class Builder extends AbstractDomainObjectBuilder<Authorization,
        Authorization.Builder> implements Authorization.Builder {
        private String system;

        private String user;

        private String token;

        private Long expiration;

        @Override
        public Authorization.Builder system(String system) {
            this.system = system;
            return this;
        }

        @Override
        public Authorization.Builder user(String user) {
            this.user = user;
            return this;
        }

        @Override
        public Authorization.Builder token(String token) {
            this.token = token;
            return this;
        }

        @Override
        public Authorization.Builder expiration(Long expiration) {
            this.expiration = expiration;
            return this;
        }

        @Override
        public Authorization build() {
            return new DefaultAuthorization(this.id(), this.system, this.user, this.token, this.expiration,
                    this.creator(), this.creationTime(), this.lastModifier(), this.lastModificationTime());
        }
    }

    /**
     * 为 {@link Authorization.Declaration} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    static class Declaration implements Authorization.Declaration {
        private final UndefinableValue<String> system;

        private final UndefinableValue<String> user;

        private final UndefinableValue<String> token;

        private final UndefinableValue<Long> expiration;

        Declaration(UndefinableValue<String> system, UndefinableValue<String> user, UndefinableValue<String> token,
                UndefinableValue<Long> expiration) {
            this.system = system;
            this.user = user;
            this.token = token;
            this.expiration = expiration;
        }

        @Override
        public UndefinableValue<String> system() {
            return this.system;
        }

        @Override
        public UndefinableValue<String> user() {
            return this.user;
        }

        @Override
        public UndefinableValue<String> token() {
            return this.token;
        }

        @Override
        public UndefinableValue<Long> expiration() {
            return this.expiration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                Declaration that = (Declaration) obj;
                return Objects.equals(this.system(), that.system()) && Objects.equals(this.user(), that.user())
                        && Objects.equals(this.token(), that.token()) && Objects.equals(this.expiration(),
                        that.expiration());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {
                    this.getClass(), this.system(), this.user(), this.token(), this.expiration()
            });
        }

        @Override
        public String toString() {
            return StringUtils.format("[system={0}, user={1}, token={2}, expiration={3}]", this.system(), this.user(),
                    this.token(), this.expiration());
        }

        /**
         * 为 {@link Authorization.Declaration.Builder} 提供默认实现。
         *
         * @author 梁济时
         * @since 2023-11-27
         */
        static class Builder implements Authorization.Declaration.Builder {
            private UndefinableValue<String> system;

            private UndefinableValue<String> user;

            private UndefinableValue<String> token;

            private UndefinableValue<Long> expiration;

            Builder() {
                this.system = UndefinableValue.undefined();
                this.user = UndefinableValue.undefined();
                this.token = UndefinableValue.undefined();
                this.expiration = UndefinableValue.undefined();
            }

            @Override
            public Authorization.Declaration.Builder system(String system) {
                this.system = UndefinableValue.defined(system);
                return this;
            }

            @Override
            public Authorization.Declaration.Builder user(String user) {
                this.user = UndefinableValue.defined(user);
                return this;
            }

            @Override
            public Authorization.Declaration.Builder token(String token) {
                this.token = UndefinableValue.defined(token);
                return this;
            }

            @Override
            public Authorization.Declaration.Builder expiration(Long expiration) {
                this.expiration = UndefinableValue.defined(expiration);
                return this;
            }

            @Override
            public Authorization.Declaration build() {
                return new Declaration(this.system, this.user, this.token, this.expiration);
            }
        }
    }

    /**
     * 为 {@link Authorization.Filter} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    static class Filter implements Authorization.Filter {
        private final List<String> ids;

        private final List<String> systems;

        private final List<String> users;

        Filter(List<String> ids, List<String> systems, List<String> users) {
            this.ids = ids;
            this.systems = systems;
            this.users = users;
        }

        @Override
        public List<String> ids() {
            return this.ids;
        }

        @Override
        public List<String> systems() {
            return this.systems;
        }

        @Override
        public List<String> users() {
            return this.users;
        }

        /**
         * 为 {@link Authorization.Filter.Builder} 提供默认实现。
         *
         * @author 梁济时
         * @since 2023-11-27
         */
        static class Builder implements Authorization.Filter.Builder {
            private List<String> ids;

            private List<String> systems;

            private List<String> users;

            @Override
            public Authorization.Filter.Builder ids(List<String> ids) {
                this.ids = ids;
                return this;
            }

            @Override
            public Authorization.Filter.Builder systems(List<String> systems) {
                this.systems = systems;
                return this;
            }

            @Override
            public Authorization.Filter.Builder users(List<String> users) {
                this.users = users;
                return this;
            }

            @Override
            public Authorization.Filter build() {
                return new Filter(this.ids, this.systems, this.users);
            }
        }
    }
}
