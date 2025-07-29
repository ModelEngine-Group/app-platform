/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.instance;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.Undefinable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meta实例声明信息结构体。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public class InstanceDeclarationInfo {
    private Undefinable<Map<String, Object>> info;

    private Undefinable<List<String>> tags;

    public InstanceDeclarationInfo() {
        this(null, null);
    }

    public InstanceDeclarationInfo(Undefinable<Map<String, Object>> info, Undefinable<List<String>> tags) {
        this.info = nullIf(info, Undefinable.undefined());
        this.tags = nullIf(tags, Undefinable.undefined());
    }

    public Undefinable<Map<String, Object>> getInfo() {
        return info;
    }

    public void setInfo(Undefinable<Map<String, Object>> info) {
        this.info = nullIf(info, Undefinable.undefined());
    }

    public Undefinable<List<String>> getTags() {
        return tags;
    }

    public void setTags(Undefinable<List<String>> tags) {
        this.tags = nullIf(tags, Undefinable.undefined());
    }

    /**
     * Builder类
     */
    public static class Builder {
        private Undefinable<Map<String, Object>> info = Undefinable.undefined();

        private Undefinable<List<String>> tags = Undefinable.undefined();

        /**
         * putInfo
         *
         * @param key key
         * @param value value
         * @return Builder
         */
        public Builder putInfo(String key, Object value) {
            if (!info.getDefined()) {
                this.info = Undefinable.defined(new HashMap<>());
            }
            this.info.getValue().put(key, value);
            return this;
        }

        /**
         * putTags
         *
         * @param key key
         * @return Builder
         */
        public Builder putTags(String key) {
            if (!tags.getDefined()) {
                this.tags = Undefinable.defined(new ArrayList<>());
            }
            this.tags.getValue().add(key);
            return this;
        }

        /**
         * info
         *
         * @param info info
         * @return Builder
         */
        public Builder info(Map<String, Object> info) {
            this.info = Undefinable.defined(info);
            return this;
        }

        /**
         * tags
         *
         * @param tags tags
         * @return Builder
         */
        public Builder tags(List<String> tags) {
            this.tags = Undefinable.defined(tags);
            return this;
        }

        /**
         * build
         *
         * @return InstanceDeclarationInfo
         */
        public InstanceDeclarationInfo build() {
            return new InstanceDeclarationInfo(this.info, this.tags);
        }
    }

    /**
     * custom
     *
     * @return InstanceDeclarationInfo.Builder
     */
    public static Builder custom() {
        return new Builder();
    }
}
