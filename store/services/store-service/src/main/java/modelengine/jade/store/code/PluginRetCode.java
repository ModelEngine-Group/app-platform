/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.code;

import modelengine.jade.common.code.RetCode;

/**
 * 插件部署错误码
 *
 * @author 张雪彬
 * @since 2024-08-07
 */
public enum PluginRetCode implements RetCode {
    /**
     * 插件上传校验失败，未上传文件。
     */
    NO_FILE_UPLOADED_ERROR(130901001, "The plugin package to upload cannot be empty."),

    /**
     * 插件上传校验失败，非zip压缩包。
     */
    UPLOADED_FILE_FORMAT_ERROR(130901002, "The plugin package to upload must be a ZIP package."),

    /**
     * 上传文件中没有插件。
     */
    NO_PLUGIN_FOUND_ERROR(130901003,
            "The plugin package to upload does not contain the plugin source file."),

    /**
     * 上传文件中缺少文件，占位符代表缺少的文件名称。
     */
    FILE_MISSING_ERROR(130901004, "The file to upload does not contain the configuration file. [file={0}]"),

    /**
     * 插件唯一性校验失败。
     */
    PLUGIN_UNIQUE_CHECK_ERROR(130901005, "Failed to verify the file uniqueness."),

    /**
     * json 文件解析失败。
     */
    JSON_PARSE_ERROR(130901006, "Failed to parse the configuration file. [reason={0}]"),

    /**
     * 插件不存在。
     */
    PLUGIN_NOT_EXISTS(130901007, "The plugin does not exist. Select another one."),

    /**
     * 插件完整性校验失败。
     */
    PLUGIN_COMPLETENESS_CHECK_ERROR(130901008, "Integrity check failed because the plugin has been modified."),

    /**
     * schema 解析失败。
     */
    FIELD_ERROR_IN_SCHEMA(130901009, "Failed to obtain the data in schema. [field={0}]"),

    /**
     * Plugin 唯一性校验格式错误，可以包含点、下划线和短横线，但不能以这些符号开头或结尾。
     */
    PLUGIN_VALIDATION_FIELD(130901010, "Plugin validation pattern error. [field={0}]"),

    /**
     * 名称格式不对，名称只能包含中英文、数字、中划线（-）和下划线(_)，并且不能以中划线、下划线开头。
     */
    NAME_IS_INVALID(130901011, "The name format is incorrect. [name={0}, path={1}]"),

    /**
     * 插件上传超出限制错误。
     */
    UPLOAD_EXCEEDED_LIMIT_FIELD(130901012, "The plugin upload exceeds the limit. [cause={0}]"),

    /**
     * 参数长度超出限制错误。
     */
    LENGTH_EXCEEDED_LIMIT_FIELD(130901013, "The field length exceeds the limit. [field={0}]"),

    /**
     * 插件部署失败。
     */
    PLUGIN_DEPLOY_FAILED(130901014, "Failed to deploy the plugin. [cause={0}]"),

    /**
     * 解压 zip 文件失败。
     */
    UNZIP_FILE_ERROR(130901015, "Failed to unzip plugin file. [file={0}]"),

    /**
     * 没有权限操作该应用。
     */
    NO_PERMISSION_OPERATE_PLUGIN(130901016, "No permission to operate this plugin."),

    /**
     * 没有权限部署插件。
     */
    NO_PERMISSION_DEPLOY_PLUGIN(130901017, "No permission to deploy plugins."),

    /**
     * 查询插件工具个数超过限制。
     */
    PLUGIN_TOOL_COUNT_EXCEEDED_LIMIT(130901020, "Query plugin tools amount exceeds limit.");

    private final int code;

    private final String msg;

    PluginRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
