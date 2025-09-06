/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;
import java.util.Objects;

/**
 * 表示任务实例变更消息。
 *
 * @author 梁济时
 * @since 2023-08-28
 */
public class InstanceChanged extends InstanceMessage {
    private List<ChangedPropertyValue> changes;

    /**
     * InstanceChanged
     */
    public InstanceChanged() {
    }

    public InstanceChanged(List<ChangedPropertyValue> changes) {
        this.changes = changes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InstanceChanged that = (InstanceChanged) o;
        return Objects.equals(changes, that.changes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), changes);
    }

    public List<ChangedPropertyValue> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangedPropertyValue> changes) {
        this.changes = changes;
    }

    /**
     * ChangedPropertyValue
     */
    public static class ChangedPropertyValue extends PropertyValue {
        private String originValue;

        /**
         * ChangedPropertyValue
         */
        public ChangedPropertyValue() {
        }

        public ChangedPropertyValue(String originValue) {
            this.originValue = originValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ChangedPropertyValue that = (ChangedPropertyValue) o;
            return Objects.equals(originValue, that.originValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), originValue);
        }

        public String getOriginValue() {
            return originValue;
        }

        public void setOriginValue(String originValue) {
            this.originValue = originValue;
        }
    }
}
