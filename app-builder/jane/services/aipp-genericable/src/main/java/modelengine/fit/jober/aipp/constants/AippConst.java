/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.constants;

import modelengine.fit.dynamicform.entity.FormMetaItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * AIPP Constants
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
public class AippConst {
    /**
     * string length
     */
    public static final int STRING_LEN = 1024;

    /**
     * invalid form id string
     */
    public static final String INVALID_FORM_ID = "undefined";

    /**
     * aipp flowTraceIds instance key
     */
    public static final String INST_FLOW_TRACE_IDS = "flowTraceIds";

    /**
     * flow data
     */
    public static final String FLOW_DATA = "flowData";

    /**
     * invalid form version id string
     */
    public static final String INVALID_FORM_VERSION_ID = "undefined";

    /**
     * RETRY_INTERVAL
     */
    public static final int RETRY_INTERVAL = 1000;

    /**
     * RETRY_TIMES
     */
    public static final int RETRY_TIMES = 60;

    /**
     * 检索内容拼接符。
     */
    public static final String CONTENT_DELIMITER = "; ";

    /**
     * flow_component_data_zh
     */
    public static final String FLOW_COMPONENT_DATA_ZH_KEY = "flow_component_data_zh";

    /**
     * flow_component_data_en
     */
    public static final String FLOW_COMPONENT_DATA_EN_KEY = "flow_component_data_en";

    /**
     * form_component_data_zh
     */
    public static final String FORM_COMPONENT_DATA_ZH_KEY = "form_component_data_zh";

    /**
     * form_component_data_en
     */
    public static final String FORM_COMPONENT_DATA_EN_KEY = "form_component_data_en";

    /**
     * basic_node_component_data_zh
     */
    public static final String BASIC_NODE_COMPONENT_DATA_ZH_KEY = "basic_node_component_data_zh";

    /**
     * basic_node_component_data_en
     */
    public static final String BASIC_NODE_COMPONENT_DATA_EN_KEY = "basic_node_component_data_en";

    /**
     * evaluation_node_component_data_zh
     */
    public static final String EVALUATION_NODE_COMPONENT_DATA_ZH_KEY = "evaluation_node_component_data_zh";

    /**
     * evaluation_node_component_data_en
     */
    public static final String EVALUATION_NODE_COMPONENT_DATA_EN_KEY = "evaluation_node_component_data_en";

    /**
     * tool_context_key
     */
    public static final String TOOL_CONTEXT_KEY = "tool_context_key";

    /**
     * WaterFlowAgent
     */
    public static final String WATER_FLOW_AGENT_BEAN = "WaterFlowAgent";

    // *** business key ***
    /**
     * business data
     */
    public static final String BS_DATA_KEY = "businessData";

    /**
     * business data
     */
    public static final String CONTEXT_DATA_KEY = "contextData";

    /**
     * context nodeId
     */
    public static final String BS_NODE_ID_KEY = "nodeId";

    /**
     * context endFormId
     */
    public static final String BS_END_FORM_ID_KEY = "endFormId";

    /**
     * business initContext key
     */
    public static final String BS_INIT_CONTEXT_KEY = "initContext";

    /**
     * business llm_model_name key
     */
    public static final String BS_MODEL_NAME_KEY = "llmModelName";

    /**
     * business prompt key
     */
    public static final String BS_MODEL_PROMPT_KEY = "prompt";

    /**
     * business text generate ppt json key
     */
    public static final String BS_TEXT_GENERATE_PPT_JSON_KEY = "text";

    /**
     * business text len limit key
     */
    public static final String BS_TEXT_LIMIT_KEY = "resultTextLimit";

    /**
     * business text generate ppt json result key
     */
    public static final String BS_PPT_JSON_RESULT = "text2pptResult";

    /**
     * business image file path key
     */
    public static final String BS_IMAGE_PATH_KEY = "imagePath";

    /**
     * business pdf file path key
     */
    public static final String BS_PDF_PATH_KEY = "pdfPath";

    /**
     * business file path key
     */
    public static final String BS_FILE_PATH_KEY = "filePath";

    /**
     * business aipp_agent_id_key key
     */
    public static final String BS_AGENT_ID_KEY = "aippId";

    /**
     * business aipp_agent_param_key key
     */
    public static final String BS_AGENT_PARAM_KEY = "aipp_agent_param_key";

    /**
     * business aipp_agent_result_link_key key
     */
    public static final String BS_AGENT_RESULT_LINK_KEY = "aipp_agent_result_link_key";

    /**
     * business aipp_agent_inst_url_key key
     */
    public static final String BS_AGENT_INST_URL_LINK_KEY = "aipp_agent_inst_url_key";

    /**
     * business image description key
     */
    public static final String BS_IMAGE_DESCRIPTION_KEY = "llmImage2TextResult";

    /**
     * business video path
     */
    public static final String BS_VIDEO_PATH = "videoPath";

    /**
     * business path for video to audio conversion
     */
    public static final String BS_VIDEO_TO_AUDIO_RESULT_DIR = "video2AudioResultDir";

    /**
     * business segSize for video to audio conversion
     */
    public static final String BS_VIDEO_TO_AUDIO_SEG_SIZE = "video2AudioSegmentSize";

    /**
     * business text result for video to text conversion
     */
    public static final String BS_VIDEO_TO_TEXT_RESULT = "llmVideo2textResult";

    /**
     * business audio path
     */
    public static final String BS_AUDIO_PATH = "audioPath";

    /**
     * business aipp_id key
     */
    public static final String BS_AIPP_ID_KEY = "aipp_id";

    /**
     * business dimension key
     */
    public static final String BS_DIMENSION_ID_KEY = "dimension_id";

    /**
     * business aipp_version key
     */
    public static final String BS_AIPP_VERSION_KEY = "aipp_version";

    /**
     * business meta_version_id key
     */
    public static final String BS_META_VERSION_ID_KEY = "meta_version_id";

    /**
     * business aipp_inst_id key
     */
    public static final String BS_AIPP_INST_ID_KEY = "aippInstanceId";

    /**
     * business http_context key
     */
    public static final String BS_HTTP_CONTEXT_KEY = "http_context";

    /**
     * business aipp_agent_input key
     */
    public static final String BS_AGENT_INPUT_KEY = "aipp_agent_input";

    /**
     * business log enable key
     */
    public static final String BS_EXTRA_CONFIG_KEY = "extraJober";

    /**
     * business log enable key
     */
    public static final String BS_LOG_ENABLE_KEY = "isLogEnabled";

    /**
     * 用于生成doc的原始文本
     */
    public static final String BS_TO_DOC_TEXT = "toDocText";

    /**
     * 使用这个url下载doc
     */
    public static final String BS_DOWNLOAD_DOC_FILE_URL = "downloadDocFileUrl";

    /**
     * 表示重新对话模式的 key。
     */
    public static final String RESTART_MODE = "restartMode";

    // *** flow config key ***

    /**
     * aipp flow config version key
     */
    public static final String FLOW_CONFIG_VERSION_KEY = "version";

    /**
     * aipp flow config name key
     */
    public static final String FLOW_CONFIG_NAME = "name";

    /**
     * aipp flow config id key
     */
    public static final String FLOW_CONFIG_ID_KEY = "id";

    // *** attribute key ***

    /**
     * aipp meta icon attribute key
     */
    public static final String ATTR_META_ICON_KEY = "meta_icon";

    /**
     * aipp meta status attribute key
     */
    public static final String ATTR_META_STATUS_KEY = "meta_status";

    /**
     * aipp type attribute key
     */
    public static final String ATTR_AIPP_TYPE_KEY = "aipp_type";

    /**
     * aipp version attribute key
     */
    public static final String ATTR_VERSION_KEY = "version";

    /**
     * aipp baseline version attribute key
     */
    public static final String ATTR_BASELINE_VERSION_KEY = "baseline_version";

    /**
     * aipp description attribute key
     */
    public static final String ATTR_DESCRIPTION_KEY = "description";

    /**
     * aipp flow_config_id attribute key
     */
    public static final String ATTR_FLOW_CONFIG_ID_KEY = "flow_config_id";

    /**
     * aipp flow_definition_id attribute key
     */
    public static final String ATTR_FLOW_DEF_ID_KEY = "flow_definition_id";

    /**
     * aipp publish_at attribute key
     */
    public static final String ATTR_PUBLISH_TIME_KEY = "publish_at";

    /**
     * aipp publish description
     */
    public static final String ATTR_PUBLISH_DESCRIPTION = "publish_description";

    /**
     * aipp publish update log
     */
    public static final String ATTR_PUBLISH_UPDATE_LOG = "publish_update_log";

    /**
     * aipp unique name
     */
    public static final String ATTR_UNIQUE_NAME = "unique_name";

    /**
     * aipp start_form_id attribute key
     */
    public static final String ATTR_START_FORM_ID_KEY = "start_form_id";

    /**
     * aipp start_form_version attribute key
     */
    public static final String ATTR_START_FORM_VERSION_KEY = "start_form_version";

    /**
     * aipp end_form_id attribute key
     */
    public static final String ATTR_END_FORM_ID_KEY = "end_form_id";

    /**
     * aipp end_form_version attribute key
     */
    public static final String ATTR_END_FORM_VERSION_KEY = "end_form_version";

    // *** instance key ***

    /**
     * aipp name instance key
     */
    public static final String INST_NAME_KEY = "aipp_instance_name";

    /**
     * task id key
     */
    public static final String TASK_ID_KEY = "task_id";

    /**
     * aipp create_by instance key
     */
    public static final String INST_CREATOR_KEY = "create_by";

    /**
     * aipp created_at instance key
     */
    public static final String INST_CREATE_TIME_KEY = "created_at";

    /**
     * aipp modified_by instance key
     */
    public static final String INST_MODIFY_BY_KEY = "modified_by";

    /**
     * aipp modified_at instance key
     */
    public static final String INST_MODIFY_TIME_KEY = "modified_at";

    /**
     * aipp finish_time instance key
     */
    public static final String INST_FINISH_TIME_KEY = "finish_at";

    /**
     * aipp flow_trans_id instance key
     */
    public static final String INST_FLOW_INST_ID_KEY = "flow_trans_id";

    /**
     * aipp curr_form_id instance key
     */
    public static final String INST_CURR_FORM_ID_KEY = "curr_form_id";

    /**
     * aipp curr_form_version instance key
     */
    public static final String INST_CURR_FORM_VERSION_KEY = "curr_form_version";

    /**
     * aipp curr_form_data instance key
     */
    public static final String INST_CURR_FORM_DATA_KEY = "curr_form_data";

    /**
     * aipp smart form time instance key
     */
    public static final String INST_SMART_FORM_TIME_KEY = "inst_smart_form_time";

    /**
     * aipp resume duration instance key
     */
    public static final String INST_RESUME_DURATION_KEY = "inst_resume_duration";

    /**
     * aipp inst_status instance key
     */
    public static final String INST_STATUS_KEY = "inst_status";

    /**
     * aipp inst_progress instance key
     */
    public static final String INST_PROGRESS_KEY = "progress";

    /**
     * aipp curr_node_id instance key
     */
    public static final String INST_CURR_NODE_ID_KEY = "curr_node_id";

    /**
     * aipp llmText2TextResult instance key (dynamic_key)
     */
    public static final String INST_TEXT2TEXT_KEY = "llmText2TextResult";

    /**
     * aipp llmPdf2TextResult instance key (dynamic_key)
     */
    public static final String INST_PDF2TEXT_KEY = "llmPdf2TextResult";

    /**
     * aipp llmFile2TextResult instance key (dynamic_key)
     */
    public static final String INST_FILE2TEXT_KEY = "llmFile2TextResult";

    /**
     * aipp llmWord2MindResult data instance key (dynamic_key)
     */
    public static final String INST_WORD2MIND_KEY = "llmWord2MindResult";

    /**
     * aipp inst_result instance key
     */
    public static final String INST_AGENT_RESULT_KEY = "agentResult";

    /**
     * aipp child instance key
     */
    public static final String INST_CHILD_INSTANCE_ID = "childInstanceId";

    /**
     * aipp mind agent data instance key (dynamic_key)
     */
    public static final String INST_MIND_DATA_KEY = "llmJson2MindResult";

    /**
     * aipp mind agent url instance key (dynamic_key)
     */
    public static final String INST_MIND_URL_KEY = "mindUrl";

    /**
     * aipp llmRecommendDocResult instance key (dynamic_key)
     */
    public static final String INST_RECOMMEND_DOC_KEY = "llmRecommendDocResult";

    /**
     * aipp elsePptUrl instance key (dynamic_key)
     */
    public static final String INST_ELSA_PPT_RESULT_KEY = "elsaPptResult";

    /**
     * chat history instance key (dynamic_key)
     */
    public static final String INST_CHAT_HISTORY_KEY = "aipp$QAlist";

    /**
     * report result instance key (dynamic_key)
     */
    public static final String INST_OPERATION_REPORT_KEY = "reportResult";

    /**
     * answer type
     */
    public static final String ANSWER_TYPE = "ELSA";

    /**
     * memories: 根据配置获取的历史记录，最后放在 business。
     */
    public static final String BS_AIPP_MEMORIES_KEY = "memories";

    /**
     * memory: 历史记录在 config 的 key。
     */
    public static final String MEMORY_CONFIG_KEY = "memory";

    /**
     * use memory
     */
    public static final String BS_AIPP_USE_MEMORY_KEY = "useMemory";

    /**
     * 表示允许使用的历史记录轮次。
     */
    public static final String BS_MAX_MEMORY_ROUNDS = "maxMemoryRounds";

    /**
     * 多轮对话开关在 config 中的 key。
     */
    public static final String MEMORY_SWITCH_KEY = "memorySwitch";

    /**
     * aipp app attribute key
     */
    public static final String ATTR_APP_ID_KEY = "app_id";

    /**
     * business中的question key
     */
    public static final String BS_AIPP_QUESTION_KEY = "Question";

    /**
     * business中的file description key
     */
    public static final String BS_AIPP_FILE_DESC_KEY = "$[FileDescription]$";

    /**
     * business中的file download key
     */
    public static final String BS_AIPP_FILE_DOWNLOAD_KEY = "fileUrl";

    /**
     * business 中的多文件下载的键。
     */
    public static final String BS_AIPP_FILES_DOWNLOAD_KEY = "fileUrls";

    /**
     * business中的用户选择的历史记录的key
     */
    public static final String BS_AIPP_SELECTED_LOGS = "selected_logs";

    /**
     * 表明结束节点的结果
     */
    public static final String BS_AIPP_FINAL_OUTPUT = "finalOutput";

    /**
     * 表明结果是否需要经过模型再加工
     */
    public static final String BS_AIPP_OUTPUT_IS_NEEDED_LLM = "isNeededLLM";

    /**
     * 表明模型返回结果是否需要输出
     */
    public static final String BS_LLM_ENABLE_LOG = "enableLog";

    /**
     * parent flow trace id
     */
    public static final String PARENT_INSTANCE_ID = "parentInstanceId";

    /**
     * parent callback id
     */
    public static final String PARENT_CALLBACK_ID = "parentCallbackId";

    /**
     * parent exception fitable id
     */
    public static final String PARENT_EXCEPTION_FITABLE_ID = "parentExceptionFitableId";

    /**
     * 对应启动节点应用的数据id，在节点应用执行完成后可使用该数据恢复该节点job执行
     */
    public static final String PARENT_FLOW_DATA_ID = "parentFlowDataId";

    /**
     * 表明结果是否来自子流程
     */
    public static final String OUTPUT_IS_FROM_CHILD = "outputIsFromChild";

    /**
     * 表单渲染数据的key
     */
    public static final String FORM_APPEARANCE_KEY = "formAppearance";

    /**
     * 表单填充数据的key
     */
    public static final String FORM_DATA_KEY = "formData";

    /**
     * 标识是否是评估模块调用接口
     */
    public static final String IS_EVAL_INVOCATION = "_isEvalInvocation";

    /**
     * 记录实例启动时间
     */
    public static final String INSTANCE_START_TIME = "_instanceStartTime";

    /**
     * 系统上下文的应用Id
     */
    public static final String CONTEXT_APP_ID = "appId";

    /**
     * 系统上下文的对话实例Id
     */
    public static final String CONTEXT_INSTANCE_ID = "instanceId";

    /**
     * 表示当前的语言类型
     */
    public static final String BS_APP_LANGUAGE = "app_language";

    /**
     * 系统上下文的用户Id
     */
    public static final String CONTEXT_USER_ID = "userId";

    /**
     * 表明当前会话是否被删除
     */
    public static final Integer CHAT_STATUS = 0;

    /**
     * 表明最长chat_name为20
     */
    public static final Integer CHAT_NAME_LENGTH = 20;

    /**
     * 用于获取终止信息
     */
    public static final String TERMINATE_MESSAGE_KEY = "content";

    /**
     * chat的attributes的key：state
     */
    public static final String ATTR_CHAT_STATE_KEY = "state";

    /**
     * chat的attributes的key：originApp
     */
    public static final String ATTR_CHAT_ORIGIN_APP_KEY = "originApp";

    /**
     * chat的attributes的key：originAppVersion
     */
    public static final String ATTR_CHAT_ORIGIN_APP_VERSION_KEY = "originAppVersion";

    /**
     * chat的attributes的key：instId
     */
    public static final String ATTR_CHAT_INST_ID_KEY = "instId";

    /**
     * business中的会话的sessionId的key
     */
    public static final String BS_CHAT_SESSION_ID_KEY = "chat_session_id";

    /**
     * business中的会话的@其它应用的id的key
     */
    public static final String BS_AT_APP_ID = "at_app_id";

    /**
     * business中的会话的@其它应用的会话id的key
     */
    public static final String BS_AT_CHAT_ID = "at_chat_id";

    /**
     * business中的会话的id的key
     */
    public static final String BS_CHAT_ID = "chatId";

    /**
     * business中的原始会话的id的key，用于应用之间@的场景.
     */
    public static final String BS_ORIGIN_CHAT_ID = "originChatId";

    /**
     * business中的原始应用版本的id的key，用于应用之间@的场景.
     */
    public static final String BS_ORIGIN_APP_ID = "originAppId";

    /**
     * aippId
     */
    public static final String AIPP_ID = "aippId";

    /**
     * app的attributes的key：is_update
     */
    public static final String ATTR_APP_IS_UPDATE = "is_update";

    /** 表示默认的日期时间的格式。 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 表示筛选对话模型的类型名字
     */
    public static final String CHAT_MODEL_TYPE = "chat_completions";

    /**
     * 节点的执行开始时间
     */
    public static final String NODE_START_TIME_KEY = "startTime";

    /**
     * 内置数据key
     */
    public static final String BUSINESS_DATA_INTERNAL_KEY = "_internal";

    /**
     * 内置数据key
     */
    public static final String BUSINESS_INPUT_KEY = "input";

    /**
     * 内置数据key
     */
    public static final String BUSINESS_INFOS_KEY = "infos";

    /**
     * mcp server key
     */
    public static final String MCP_SERVER_KEY = "mcpServer";

    /**
     * mcp servers key
     */
    public static final String MCP_SERVERS_KEY = "mcpServers";

    /**
     * mcp server url key
     */
    public static final String MCP_SERVER_URL_KEY = "url";


    /**
     * mcp server type
     */
    public static final String MCP_SERVER_TYPE = "mcp";

    /**
     * store server type
     */
    public static final String STORE_SERVER_TYPE = "store";

    /**
     * store server name
     */
    public static final String STORE_SERVER_NAME = "store";

    /**
     * tool real name
     */
    public static final String TOOL_REAL_NAME = "toolRealName";

    /**
     * tools key
     */
    public static final String TOOLS_KEY = "tools";

    // *** aipp initial static meta items ***
    /**
     * aipp initial static meta items
     */
    public static final List<FormMetaItem> STATIC_META_ITEMS = Collections.unmodifiableList(Arrays.asList(
                    new FormMetaItem(INST_NAME_KEY, "meta实例名称", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CREATOR_KEY, "创建人", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CREATE_TIME_KEY, "创建时间", "DATETIME", null, null),
                    new FormMetaItem(INST_MODIFY_BY_KEY, "更新人", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_MODIFY_TIME_KEY, "更新时间", "DATETIME", null, null),
                    new FormMetaItem(INST_FINISH_TIME_KEY, "完成时间", "DATETIME", null, null),
                    new FormMetaItem(INST_FLOW_INST_ID_KEY, "flow实例id", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CURR_FORM_ID_KEY, "当前表单id", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CURR_FORM_VERSION_KEY, "当前表单版本", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CURR_FORM_DATA_KEY, "当前表单数据", "TEXT", STRING_LEN * 8, null),
                    new FormMetaItem(INST_SMART_FORM_TIME_KEY, "elsa 表单渲染时间戳", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_RESUME_DURATION_KEY, "人工节点时间耗时", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_STATUS_KEY, "实例状态", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_PROGRESS_KEY, "实例进度", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_AGENT_RESULT_KEY, "aipp agent结果", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CHILD_INSTANCE_ID, "aipp子流程instanceId", "TEXT", STRING_LEN, null),
                    new FormMetaItem(INST_CURR_NODE_ID_KEY, "当前节点id", "TEXT", STRING_LEN, null)));
}
