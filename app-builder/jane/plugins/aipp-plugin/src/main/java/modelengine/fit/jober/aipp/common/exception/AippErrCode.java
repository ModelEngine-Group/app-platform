/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import modelengine.jade.common.code.RetCode;

import lombok.AllArgsConstructor;
import modelengine.fit.jane.common.response.ErrorCode;

/**
 * Aipp 错误码。
 *
 * @author 易文渊
 * @since 2023/9/26
 */
@AllArgsConstructor
public enum AippErrCode implements ErrorCode, RetCode {
    /**
     * 请求成功
     */
    OK(0, "success"),

    /** ------------ Generic Exception. From 90000000 to 90000999 --------------------- */
    /**
     * 入参不合法
     */
    INPUT_PARAM_IS_INVALID(90000000, "非法参数: {0}。"),

    /**
     * 资源不存在
     */
    NOT_FOUND(90000001, "资源不存在: {0}。"),

    /**
     * 未知服务器内部错误
     */
    UNKNOWN(90000002, "服务器内部错误，请联系管理员或者客户。"),

    /**
     * 禁止操作
     */
    FORBIDDEN(90000003, "禁止操作。"),

    /** ------------ Configuration Exception. From 90001000 to 90001999 --------------------- */
    /**
     * 创建流程失败
     */
    CREATE_FLOW_FAILED(90001001, "系统错误，创建流程失败，请重试或联系管理员。"),

    /**
     * 不允许终止实例
     */
    TERMINATE_INSTANCE_FORBIDDEN(90001002, "没有正在运行的对话，无需终止。"),

    /**
     * 不允许删除实例
     */
    DELETE_INSTANCE_FORBIDDEN(90001003, "对话正在进行中，无法删除。"),

    /**
     * 发布流程失败
     */
    PUBLISH_FLOW_FAILED(90001004, "发布流程失败，请检查流程定义。"),

    /**
     * 流程已存在
     */
    FLOW_ALREADY_EXIST(90001005, "流程已存在，请检查流程的名称和版本是否唯一。"),

    /**
     * 不允许删除已发布的应用或工具流
     */
    DELETE_AIPP_FORBIDDEN(90001008, "应用/工具流已发布，不可以删除。"),

    /**
     * 不允许更新已发布的应用或工具流
     */
    UPDATE_AIPP_FORBIDDEN(90001009, "不允许更新已发布的应用或工具流。"),

    /**
     * 预览的临时版本已存在
     */
    PREVIEW_AIPP_FORBIDDEN(90001010, "应用预览版本已存在。"),

    /**
     * 调试失败
     */
    PREVIEW_AIPP_FAILED(90001011, "配置有误，请查看工作流编排是否正确，错误原因：{0}。"),

    /**
     * 属性key重复
     */
    AIPP_PROPS_KEY_DUPLICATE(90001012, "存在相同的应用属性。"),

    /**
     * 名称为空
     */
    AIPP_NAME_IS_EMPTY(90001013, "应用名称不能为空。"),

    /**
     * 名称已存在
     */
    AIPP_NAME_IS_DUPLICATE(90001014, "应用名称已存在。"),

    /**
     * 实例历史记录不存在
     */
    AIPP_INSTANCE_LOG_IS_NULL(90001015, "应用实例日志为空。"),

    /**
     * APP 长度超过最大值
     */
    AIPP_NAME_LENGTH_OUT_OF_BOUNDS(90001018, "应用名称长度超过最大值64。"),

    /** ------------ aipp runtime Exception. From 90002000 to 90002999 --------------------- */
    /**
     * 上传文件失败
     */
    UPLOAD_FAILED(90002000, "文件上传失败。"),

    /**
     * 上传文件失败
     */
    FILE_EXPIRED_OR_BROKEN(90002001, "文件过期或损坏。"),

    /**
     * 解析文件内容失败
     */
    EXTRACT_FILE_FAILED(90002002, "解析文件内容失败。"),

    /**
     * 非法的文件路径
     */
    INVALID_FILE_PATH(90002003, "无效文件路径。"),

    /**
     * json解析失败
     */
    JSON_DECODE_FAILED(90002900, "Json解析失败，原因：{0}。"),

    /**
     * json编码失败
     */
    JSON_ENCODE_FAILED(90002901, "Json编码失败，原因：{0}。"),

    /**
     * 获取历史记录失败
     */
    GET_HISTORY_LOG_FAILED(90002902, "获取历史记录失败。"),

    /**
     * 表单配置项类型不合法
     */
    FORM_PROPERTY_TYPE_IS_INVALID(90002903, "系统错误，应用配置项获取失败，请联系管理员。"),

    /**
     * 灵感大全fitable执行失败
     */
    EXECUTE_INSPIRATION_FITABLE_FAILED(90002904, "灵感大全提示词变量获取失败，请联系管理员。"),

    /**
     * 解析历史记录配置失败
     */
    PARSE_MEMORY_CONFIG_FAILED(90002905, "解析历史记录配置失败。"),

    /**
     * 模型节点模板解析失败
     */
    LLM_COMPONENT_TEMPLATE_RENDER_FAILED(90002906, "请检查提示词模板中的变量。"),

    /**
     * 创建调试aipp失败
     */
    CREATE_DEBUG_AIPP_FAILED(90002907, "调试失败，请检查流程配置是否正确: {0}。"),

    /**
     * 不支持的数据格式
     */
    DATA_TYPE_IS_NOT_SUPPORTED(90002908, "未支持的数据类型 [类型：{0}]。"),

    /**
     * task 不存在
     */
    TASK_NOT_FOUND(90002909, "任务不存在。"),

    /**
     * 应用已发布
     */
    APP_HAS_ALREADY(90002910, "应用已经发布，无法修改。"),

    /**
     * 应用已发布
     */
    APP_VERSION_HAS_ALREADY(90002911, "该版本名称已发布，请使用其他版本名称。"),

    /**
     * 该版本名称已发布，请使用其他版本名称
     */
    APP_HAS_PUBLISHED(90002912, "该应用已经成功发布，请不要重复发布。"),

    /**
     * 不合法的操作
     */
    INVALID_OPERATION(90002913, "系统错误，应用信息为空，请联系管理员。"),

    /**
     * 该应用已经成功发布过，无法修改应用名称
     */
    APP_NAME_HAS_PUBLISHED(90002914, "该应用已经成功发布过，无法修改应用名称。"),

    /**
     * 禁止使用更低的版本号
     */
    NEW_VERSION_IS_LOWER(90002915, "禁止使用更低的版本号。"),

    /**
     * 不合法的版本号
     */
    INVALID_VERSION_NAME(90002916, "不合法的版本名称。"),

    /**
     * 名称格式不对
     */
    APP_NAME_IS_INVALID(90002917, "名称只能包含中英文、数字、中划线（-）和下划线(_)，并且不能以中划线、下划线开头。"),

    /**
     * 会话请求结构有误
     */
    APP_CHAT_REQUEST_IS_NULL(90002918, "会话请求结构有误。"),

    /**
     * 无法使用未发布的应用对话
     */
    APP_CHAT_PUBLISHED_META_NOT_FOUND(90002919, "该应用未发布，无法进行对话。"),

    /**
     * 没有找到相关会话。应用会话出错
     */
    APP_CHAT_ERROR(90002920, "@应用会话出错，请清理缓存后重新对话。"),

    /**
     * 调试对话失败
     */
    APP_CHAT_DEBUG_META_NOT_FOUND(90002921, "调试对话失败，请重试。"),

    /**
     * 会话响应出错
     */
    APP_CHAT_WAIT_RESPONSE_ERROR(90002922, "会话响应出错，请重试。"),

    /**
     * 删除失败
     */
    DELETE_ERROR(90002923, "删除应用失败，请重试。"),

    /**
     * 应用找不到失败
     */
    APP_NOT_FOUND(90002924, "应用不存在，或者已经被删除。"),

    /**
     * 对话时，应用找不到失败
     */
    APP_NOT_FOUND_WHEN_CHAT(90002925, "对话失败：应用不存在，或者已经被删除。"),

    /**
     * 调试时，应用找不到失败
     */
    APP_NOT_FOUND_WHEN_DEBUG(90002926, "调试失败：应用不存在，或者已经被删除。"),

    /**
     * 请输入您的问题
     */
    APP_CHAT_QUESTION_IS_NULL(90002927, "请输入您的问题。"),

    /**
     * 实例Id无法匹配任意父实例id
     */
    PARENT_INSTANCE_ID_NOT_FOUND(90002928, "实例id“{0}”无法匹配任意父实例id。"),

    /**
     * 实例Id无法匹配任意对话
     */
    CHAT_NOT_FOUND_BY_INSTANCE_ID(90002929, "实例id“{0}”无法匹配任意对话。"),

    /**
     * 系统错误，删除失败，请联系管理员。
     */
    APP_DELETE_FAILED(90002930, "系统错误，删除失败，请联系管理员。"),

    /**
     * 系统错误，发布失败，请联系管理员。
     */
    APP_PUBLISH_FAILED(90002931, "系统错误，发布失败，请联系管理员。"),

    /**
     * 系统错误，更新失败，请联系管理员。
     */
    APP_UPDATE_FAILED(90002932, "系统错误，更新失败，请联系管理员。"),

    /**
     * 获取应用编排信息失败
     */
    OBTAIN_APP_ORCHESTRATION_INFO_FAILED(90002933, "系统错误，获取应用编排信息失败，请联系管理员。"),

    /**
     * 查询发布历史记录失败
     */
    QUERY_PUBLICATION_HISTORY_FAILED(90002934, "系统错误，查询发布历史记录失败，请联系管理员。"),

    /**
     * 文件格式校验失败
     */
    FILE_FORMAT_VERIFICATION_FAILED(90002935, "系统错误，文件格式校验失败，请联系管理员。"),

    /**
     * 模型节点解析文件失败
     */
    MODEL_NODE_FAILED_TO_PARSE_THE_FILE(90002936, "系统错误，模型节点解析文件失败，请联系管理员。"),

    /**
     * 更新应用配置失败
     */
    UPDATE_APP_CONFIGURATION_FAILED(90002937, "系统错误，更新应用配置失败，请联系管理员。"),

    /**
     * 获取历史对话失败
     */
    OBTAIN_HISTORY_CONVERSATION_FAILED(90002938, "系统错误，获取历史对话失败，请联系管理员。"),

    /**
     * 重新对话失败
     */
    RE_CHAT_FAILED(90002939, "系统错误，重新对话失败，请联系管理员。"),

    /**
     * 获取应用信息失败
     */
    OBTAIN_APP_CONFIGURATION_FAILED(90002940, "系统错误，获取应用信息失败，请联系管理员。"),

    /**
     * 抱歉，创建应用个数不能超过200。
     */
    TOO_MANY_APPS(90002941, "抱歉，创建应用个数不能超过200。"),

    /**
     * 创建调试aipp失败，流程节点个数不合法
     */
    INVALID_FLOW_NODE_SIZE(90002942, "调试失败，节点个数必须大于三个, 请检查流程配置是否正确。"),

    /**
     * 创建调试aipp失败，开始节点连接线不合法
     */
    INVALID_START_NODE_EVENT_SIZE(90002943, "调试失败，开始节点只能连接一个节点，请检查流程配置是否正确。"),

    /**
     * 连接线配置不合法
     */
    INVALID_EVENT_CONFIG(90002944, "调试失败，应用/工具流的连接线有误，请检查流程配置是否正确。"),

    /**
     * 流程配置有误
     */
    INVALID_FLOW_CONFIG(90002945, "调试失败，流程配置有误，请检查流程配置是否正确或联系管理员。"),

    /**
     * 音频文件切分失败，请更换音频文件或重试处理
     */
    AUDIO_SEGMENTATION_FAILED(90002946, "音频文件切分失败，请更换音频文件或重试处理。"),

    /**
     * 音频文件总结内容为空，请更换音频文件或重试处理
     */
    AUDIO_SUMMARY_EMPTY(90002947, "音频文件总结内容为空，请更换音频文件或重试处理。"),

    /**
     * 音频文件内容提取失败，请更换音频文件或重试处理
     */
    AUDIO_CONTENT_EXTRACT_FAILED(90002948, "音频文件内容提取失败，请更换音频文件或重试处理。"),

    /**
     * code节点执行失败
     */
    CODE_NODE_EXECUTE_FAILED(90002949, "code节点执行失败，失败信息：{0}。"),

    /**
     * 模型服务不可用
     */
    MODEL_SERVICE_NOT_AVAILABLE(90002950, "系统错误，模型服务不可用，请检查模型状态或联系管理员。"),

    /**
     * 调用大模型参数错误
     */
    MODEL_PARAMETER_ERROR(90002951, "系统错误，调用大模型参数错误，请联系管理员。"),

    /**
     * 调用大模型通用报错
     */
    MODEL_SERVICE_GENERIC_ERROR(90002952, "系统错误，调用大模型服务失败，请联系管理员。"),

    /**
     * 对话不存在，或者已经被删除
     */
    CHAT_NOT_FOUND(90002953, "对话不存在，或者已经被删除。"),

    /**
     * 终止对话失败
     */
    TERMINATE_INSTANCE_FAILED(90002954, "系统错误，终止会话失败，请联系管理员。"),

    /**
     * 继续会话失败
     */
    RESUME_CHAT_FAILED(90002955, "系统错误，继续会话失败，请重试或联系管理员。"),

    /**
     * 系统错误，工具流编排运行失败，请联系管理员。
     */
    FLOW_ERROR(10007503, "系统错误，工具流编排运行失败，请联系管理员。"),

    /**
     * 入参不合法
     */
    FLOW_INPUT_PARAM_EXEC_FAILED(10000003, "入参不合法，不合法参数是{0}。"),

    /**
     * 条件节点执行出错。
     */
    CONDITION_CODE_EXEC_FAILED(10007511, "条件节点执行出错。"),

    /**
     * 流程执行异常，请重试。
     */
    WATER_FLOW_EXEC_FAILED(10007521, "流程执行异常，请重试。"),

    /**
     * FIT工具调用异常
     */
    FIT_TOOL_INVOCATION_EXCEPTION(90002956, "工具调用异常，请检查工具后重试。"),

    /**
     * FIT查找工具异常
     */
    FIT_TOOL_LOOKUP_ERROR(90002957, "查找工具异常，请检查工具后重试"),

    /**
     * FIT网络连接出现问题
     */
    FIT_NETWORK_EXCEPTION(90002958, "网络连接出现问题，请检查网络连接后重试。"),

    /**
     * FITable ID重复
     */
    DUPLICATE_PLUGIN_EXCEPTION(90002959, "插件重复，请更换插件后重试。"),

    /**
     * 对话工具节点报错错误信息
     */
    DIALOGUE_TOOL_NODE_ERROR_OUTPUT(90002960, "{0}节点执行出错，出错原因：{1}工具执行出错，{2}"),

    /**
     * 对话通用节点报错错误信息
     */
    DIALOGUE_GENERAL_NODE_ERROR_OUTPUT(90002961, "{0}节点执行出错，出错原因：{1}"),

    /**
     * 编排对话工具节点报错错误信息
     */
    ELSA_TOOL_NODE_ERROR_OUTPUT(90002962, "执行出错，出错原因：{0}工具执行出错，{1}"),

    /**
     * 编排对话通用节点报错错误信息
     */
    ELSA_GENERAL_NODE_ERROR_OUTPUT(90002963, "执行出错，出错原因：{0}"),

    /**
     * 查询灵感大全失败
     */
    QUERY_INSPIRATION_FAILED(90002964, "系统错误，查询灵感大全失败，请联系管理员。"),

    /**
     * 应用同时对话数量过多。
     */
    CHAT_QUEUE_TOO_LONG(90002965, "不好意思，当前用户排队较多，请稍后重试，谢谢。"),

    /**
     * 构造表单属性树形结构错误信息
     */
    FORM_PROPERTY_PARENT_NOT_EXIST(90002966, "表单属性父节点不存在"),

    /**
     * 应用导入配置格式错误信息
     */
    IMPORT_CONFIG_NOT_JSON(90002967, "应用配置文件格式错误，行：{0}， 列：{1}"),

    /**
     * 应用导入配置字段异常错误信息
     */
    IMPORT_CONFIG_FIELD_ERROR(90002968, "应用配置信息有误：{0}"),

    /**
     * 应用导出权限不足错误信息
     */
    EXPORT_CONFIG_UNAUTHED(90002969, "无权导出应用配置，请使用可写角色的账号进行操作"),

    /**
     * 应用导出数据库读取异常
     */
    EXPORT_CONFIG_DB_EXCEPTION(90002970, "应用导出数据库读取异常"),

    /**
     * 可用性检查不支持的节点类型
     */
    UNSUPPORTED_NODE_TYPE(90002971, "可用性检查不支持的节点类型： {0}"),

    /**
     * 应用导入配置的版本不匹配
     */
    IMPORT_CONFIG_UNMATCHED_VERSION(90002972, "应用配置版本不匹配，当前版本：{0}，应用配置版本：{1}"),

    /**
     * 应用导出流程配置不正确
     */
    EXPORT_INVALID_FLOW_EXCEPTION(90002973, "应用流程配置不正确，导出失败"),

    /**
     * 生成图片失败
     */
    GENERATE_IMAGE_FAILED(90002974, "生成图片失败，请稍后重试"),

    /**
     * 公共URL路径格式无效
     */
    INVALID_PATH_ERROR(90002980, "路径格式无效"),

    /**
     * APP 描述超过最大值
     */
    APP_DESCRIPTION_LENGTH_OUT_OF_BOUNDS(90002981, "创建失败，应用描述长度应该小于300。"),

    /**
     * APP 分类为空
     */
    APP_CATEGORY_IS_NULL(90002982, "创建失败，应用分类不能为空。"),

    /**
     * aipp 非法类型
     */
    ILLEGAL_AIPP_TYPE(90002999, "aipp 类型非法。"),

    /**
     * 上传的表单文件不能为空
     */
    NO_FILE_UPLOAD_ERROR(90002101, "上传的文件不能为空。"),

    /**
     * 表单文件上传的格式不是zip
     */
    UPLOADED_FORM_FILE_FORMAT_ERROR(90002102, "上传的表单文件格式需要是zip。"),

    /**
     * 系统错误，保存表单文件失败，请重试或联系管理员
     */
    SAVE_FORM_FILE_FAILED(90002103, "系统错误，保存表单文件失败，请重试或联系管理员。"),

    /**
     * 文件超出6M最大限制，请重新上传
     */
    FORM_FILE_MAX_SIZE_EXCEED(90002104, "文件超出6M最大限制，请重新上传。"),

    /**
     * 表单文件内容不能为空
     */
    FORM_FILE_IS_EMPTY(90002105, "表单文件内容不能为空，请重新上传。"),

    /**
     * 表单文件不完整
     */
    FORM_FILE_MISSING(90002106, "表单文件不完整，缺少{0}，请重新上传。"),

    /**
     * 表单缩略图文件只能上传一个，请重新上传
     */
    FORM_IMG_FILE_COUNT_ERROR(90002107, "表单缩略图文件只能上传一个，请重新上传。"),

    /**
     * 表单缩略图超出1M最大限制，请重新上传
     */
    FORM_IMG_FILE_MAX_SIZE_EXCEED(90002108, "表单缩略图超出1M最大限制，请重新上传。"),

    /**
     * 表单schema缺少必填字段
     */
    FORM_SCHEMA_MISSING_KEY(90002109, "表单schema缺少必填字段：{0}， 请重新上传。"),

    /**
     * 表单schema的parameters需要包含字段：type， 值：object， 请重新上传
     */
    FORM_SCHEMA_PARAMETERS_TYPE_ERROR(90002110, "表单schema的parameters需要包含字段：type， 值：object， 请重新上传。"),

    /**
     * 表单schema的parameters缺少必填字段：{0}，请重新上传
     */
    FORM_SCHEMA_PARAMETERS_MISSING_KEY(90002111, "表单schema的parameters缺少必填字段：{0}，请重新上传。"),

    /**
     * 表单schema的参数{0}缺少{1}定义，请重新上传
     */
    FORM_SCHEMA_PROPERTY_MISSING_KEY(90002112, "表单schema的字段{0}缺少{1}定义，请重新上传。"),

    /**
     * 表单schema的参数{0}的type值类型不是String, 请重新上传
     */
    FORM_SCHEMA_PROPERTY_TYPE_NOT_STRING_ERROR(90002113, "表单schema的字段{0}的type值类型必须是String, 请重新上传。"),

    /**
     * 表单schema的参数{0}的type值类型只能是[string, number, integer, array, boolean, null, object], 请重新上传
     */
    FORM_SCHEMA_PROPERTY_TYPE_ERROR(90002114,
            "表单schema的字段{0}的type的值只能是[string, number, integer, array, boolean, null, object], 请重新上传。"),

    /**
     * 表单schema的list类型参数{0}的type值类型不是String, 请重新上传。
     */
    FORM_SCHEMA_LIST_PROPERTY_TYPE_NOT_STRING_ERROR(90002115,
            "表单schema的list类型字段{0}的type值类型必须是String, 请重新上传。"),

    /**
     * 表单schema的list类型参数{0}的enum类型item的值类型必须是list, 请重新上传
     */
    FORM_SCHEMA_LIST_PROPERTY_ENUM_NOT_LIST_ERROR(90002116,
            "表单schema的list类型字段{0}的enum类型item的值类型必须是list, 请重新上传。"),

    /**
     * 表单schema的{0}的参数个数不能大于入参个数，请重新上传
     */
    FORM_SCHEMA_FIELD_SIZE_ERROR(90002117, "表单schema的{0}的参数个数不能大于入参个数，请重新上传。"),

    /**
     * 表单schema的字段{0}不能包含除properties下的参数之外的参数名，请重新上传
     */
    FORM_SCHEMA_FIELD_NOT_IN_PROPERTIES(90002118,
            "表单schema的字段{0}不能包含除properties下的参数之外的参数名，请重新上传。"),

    /**
     * 表单build文件夹内容为空，请重新上传
     */
    FORM_BUILD_EMPTY_ERROR(90002119, "表单build文件夹内容为空，请重新上传。"),

    /**
     * 表单build文件夹缺少{0}文件，请重新上传
     */
    FORM_BUILD_MISSING_FILE(90002120, "表单build文件夹缺少{0}文件，请重新上传。"),

    /**
     * 表单schema的json格式有误，请检查后重新上传
     */
    FORM_SCHEMA_JSON_FORMAT_ERROR(90002121, "表单schema的json格式有误，请检查后重新上传。"),

    /**
     * 当前的存储空间利用率达到{0}%，请清除部分文件
     */
    STORAGE_RATIO_UP_TO_MAXIMUM(90002122, "当前的存储空间利用率达到{0}%，请清除部分文件。"),

    /**
     * 系统错误，校验系统物理存储限制失败，请联系管理员
     */
    VALIDATE_FORM_CONSTRAINT_FAILED(90002123, "系统错误，校验系统物理存储限制失败，请联系管理员。"),

    /**
     * 组件文件包含不允许的文件类型，文件名:{0}
     */
    CONTAIN_DISALLOWED_FILE(90002124, "组件文件包含不允许的文件类型，文件名:{0}。"),

    /**
     * 组件文件包含除build文件夹、config.json、表单预览图之外的文件，请重新上传
     */
    CONTAIN_EXTRA_FILE(90002125, "组件文件包含除build文件夹、config.json、表单预览图之外的文件，请重新上传。"),

    /**
     * 表单目录创建失败
     */
    ENSURE_FORM_DIRECTORY_FAILED(90002126, "表单目录创建失败，请重新上传。"),

    /**
     * 表单文件写入失败
     */
    WRITE_FORM_FILE_FAILED(90002127, "表单文件写入失败，请重新上传。"),

    /**
     * 创建表单信息为空，请重试
     */
    CREATE_FORM_NULL(90002128, "操作失败，创建表单信息为空，请重试。"),

    /**
     * FORM 名称超过最大值
     */
    FORM_NAME_LENGTH_OUT_OF_BOUNDS(90002129, "操作失败，表单名称长度应该小于64。"),

    /**
     * FORM 描述超过最大值
     */
    FORM_DESCRIPTION_LENGTH_OUT_OF_BOUNDS(90002130, "操作失败，表单描述长度应该小于300。"),

    /**
     * FORM 缺少{0}信息
     */
    CREATE_FORM_MISSING_INFO(90002131, "操作失败，缺少{0}信息。"),

    /**
     * 更新表单信息为空，请重试
     */
    UPDATE_FORM_NULL(90002132, "更新失败，更新表单信息为空，请重试。"),

    /**
     * 更新失败，表单不存在，请重试或联系管理员
     */
    UPDATE_FORM_NOT_EXIST(90002133, "更新失败，表单不存在，请重试或联系管理员。"),

    /**
     * 创建失败，表单个数不能超过{0}个
     */
    FORM_UP_TO_MAXIMUM(90002134, "创建失败，表单个数不能超过{0}个。"),

    /**
     * 操作失败，表单名称已存在，请使用其他名称
     */
    FORM_NAME_IS_EXISTED(90002135, "操作失败，表单名称已存在，请使用其他名称。"),

    /**
     * 删除失败，表单不存在或已经被删除
     */
    FORM_DELETED_FAILED_CAUSE_NOT_EXISTED(90002136, "删除失败，表单不存在或已经被删除。"),

    /**
     * 获取表单配置数据失败
     */
    GET_FORM_CONFIG_ERROR(90002137, "系统错误，获取表单配置信息失败，请联系管理员。"),

    /**
     * 运行失败，表单不存在或已经被删除。
     */
    FORM_RUNNING_FAILED_CAUSE_NOT_EXISTED(90002138, "运行失败，表单不存在或已经被删除。"),

    /**
     * 调用大模型报错，并且透传具体报错信息和错误码。
     */
    MODEL_SERVICE_INVOKE_ERROR(90002139, "调用大模型服务失败，{0}。"),

    /**
     * 应用模板不存在。
     */
    TEMPLATE_NOT_FOUND(90003000, "应用模板不存在"),

    /**
     * 错误码截止值
     */
    ERROR_CODE_BUTT(99999999, "Error code butt.");

    private final int code;

    private final String msg;

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    @Override
    public int getErrorCode() {
        return this.getCode();
    }

    /**
     * 获取错误提示信息
     *
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return this.getMsg();
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