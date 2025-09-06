/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 默认用户对象
 *
 * @author 梁济时
 * @since 2023-11-20
 */
class DefaultUser implements User {
    private final String id;

    private final String name;

    private final String fqn;

    DefaultUser(String id, String name, String fqn) {
        this.id = id;
        this.name = name;
        this.fqn = fqn;
    }

    @Override
    public String account() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String fqn() {
        return this.fqn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultUser another = (DefaultUser) obj;
            return Objects.equals(this.account(), another.account()) && Objects.equals(this.name(), another.name())
                    && Objects.equals(this.fqn(), another.fqn());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.account(), this.name(), this.fqn()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, name={1}, fqn={2}]", this.account(), this.name(), this.fqn());
    }

    /**
     * User.Builder实现类
     */
    public static class Builder implements User.Builder {
        private String id;

        private String name;

        private String fqn;

        @Override
        public User.Builder account(String id) {
            this.id = id;
            return this;
        }

        @Override
        public User.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public User.Builder fqn(String fqn) {
            this.fqn = fqn;
            return this;
        }

        @Override
        public User build() {
            return new DefaultUser(this.id, this.name, this.fqn);
        }
    }
}
