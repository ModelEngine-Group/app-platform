/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.util;

import static modelengine.fitframework.inspection.Validation.notBlank;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

/**
 * {@link Urn}的默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class DefaultUrn implements Urn {
    private final Urn parent;

    private final String type;

    private final String id;

    DefaultUrn(Urn parent, String type, String id) {
        this.parent = parent;
        this.type = notBlank(type, "The type of an object cannot be blank.").trim();
        this.id = notBlank(id, "The id of an object cannot be blank.").trim();
    }

    @Override
    public Urn parent() {
        return this.parent;
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultUrn) {
            DefaultUrn another = (DefaultUrn) obj;
            return Objects.equals(this.parent, another.parent) && this.type.equals(another.type) && this.id.equals(
                    another.id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.parent, this.type, this.id});
    }

    @Override
    public String toString() {
        Stack<Urn> stack = new Stack<>();
        Urn current = this;
        while (current != null) {
            stack.push(current);
            current = current.parent();
        }
        StringBuilder builder = new StringBuilder(128);
        builder.append("urn");
        while (!stack.empty()) {
            current = stack.pop();
            builder.append(':').append(current.type()).append(':').append(current.id());
        }
        return builder.toString();
    }
}
