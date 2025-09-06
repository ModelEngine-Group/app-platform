/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;

/**
 * DataService使用的TaskEntity结构体
 *
 * @author 陈镕希
 * @since 2023-06-12
 */
public class TaskEntity {
    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务提出人
     */
    private String applier;

    /**
     * 任务当前处理人
     */
    private String processor;

    /**
     * 三方系统任务唯一标识
     */
    private String thirdPartyId;

    /**
     * 三方系统任务对应父任务唯一标识
     */
    private String thirdPartyParentId;

    /**
     * 跳转至任务详情信息的Url
     */
    private String detailUrl;

    /**
     * 任务属性
     */
    private List<TaskProperty> props;

    /**
     * TaskEntity
     */
    public TaskEntity() {
    }

    public TaskEntity(String name, String applier, String processor, String thirdPartyId, String thirdPartyParentId,
            String detailUrl, List<TaskProperty> props) {
        this.name = name;
        this.applier = applier;
        this.processor = processor;
        this.thirdPartyId = thirdPartyId;
        this.thirdPartyParentId = thirdPartyParentId;
        this.detailUrl = detailUrl;
        this.props = props;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplier() {
        return applier;
    }

    public void setApplier(String applier) {
        this.applier = applier;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getThirdPartyParentId() {
        return thirdPartyParentId;
    }

    public void setThirdPartyParentId(String thirdPartyParentId) {
        this.thirdPartyParentId = thirdPartyParentId;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public List<TaskProperty> getProps() {
        return props;
    }

    public void setProps(List<TaskProperty> props) {
        this.props = props;
    }

    /**
     * TaskProperty
     */
    public static class TaskProperty {
        /**
         * 任务属性key值
         */
        private String key;

        /**
         * 任务属性value
         */
        private String value;

        /**
         * 关联Url(后续操作)
         */
        private String relatedUrl;

        /**
         * TaskProperty
         */
        public TaskProperty() {
        }

        public TaskProperty(String key, String value, String relatedUrl) {
            this.key = key;
            this.value = value;
            this.relatedUrl = relatedUrl;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRelatedUrl() {
            return relatedUrl;
        }

        public void setRelatedUrl(String relatedUrl) {
            this.relatedUrl = relatedUrl;
        }
    }
}
