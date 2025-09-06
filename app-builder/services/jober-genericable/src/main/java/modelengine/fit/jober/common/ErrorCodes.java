/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common;

import java.util.Arrays;

/**
 * 异常类型枚举类
 *
 * @author 陈镕希
 * @since 2023-06-19
 */
public enum ErrorCodes {
    /** ------------ Generic Exception. From 10000000 to 10000999 --------------------- */
    /**
     * 入参为空
     */
    INPUT_PARAM_IS_EMPTY(10000000, "Input param is empty, empty param is {0}."),

    /**
     * 枚举类转换异常
     */
    ENUM_CONVERT_FAILED(10000001, "Cannot convert enum {0} by name: {1}."),

    /**
     * 实体对象未找到
     */
    ENTITY_NOT_FOUND(10000002, "Cannot find entity {0} by id: {1}."),

    /**
     * 入参不合法
     */
    INPUT_PARAM_IS_INVALID(10000003, "Input param is invalid, invalid param is {0}."),

    /**
     * 服务器内部错误
     */
    SERVER_INTERNAL_ERROR(10000004, "Server internal error, please contact administrator."),

    /**
     * 请求体不合法
     */
    REQUEST_BODY_IS_INVALID(10000005, "Request Body Is Invalid."),

    /**
     * 不符合预期
     */
    UN_EXCEPTED_ERROR(10000006, "unexpected error:{0}"),

    /**
     * 过滤器是空的
     */
    FILTER_IS_EMPTY(10000007, "Filter is empty!"),

    /**
     * 分页查询时Offset范围不正确。
     */
    PAGINATION_OFFSET_INVALID(10000008, "The range of offset is incorrect."),

    /**
     * 分页查询时Limit范围不正确。
     */
    PAGINATION_LIMIT_INVALID(10000009, "The range of limit is incorrect."),

    /**
     * 需要操作人上下文信息。
     */
    OPERATION_CONTEXT_IS_REQUIRED(10000010, "Cannot get OperationContext type parameter."),

    /**
     * 类型转换失败。
     */
    TYPE_CONVERT_FAILED(10000011, "Cannot convert type."),

    /**
     * 无操作权限。
     */
    NO_OPERATE_PERMISSION(10000012, "No operate permission."),

    /**
     * 需要用户信息。
     */
    OPERATOR_IS_REQUIRED(10000013, "Operator is required."),

    /**
     * 获取线程结果异常
     */
    FAILED_TO_GET_THREAD_RESULT(10000014, "Failed to get thread result."),

    /** ------------ DataEngine Exception 10001000-10001999 --------------------- */
    /**
     * 获取MetaData失败
     */
    FAILED_TO_GET_META_DATA(10001000, "Get metaDataList failed."),

    /**
     * 定时任务已存在
     */
    SCHEDULE_TASK_IS_EXISTED(10001001, "The scheduled task is repeatedly created."),

    /**
     * 获取TaskEntity失败
     */
    FAIL_TO_GET_TASKS_BY_FILTER(10001002, "Get tasks by filter failed."),

    /** ------------ TaskCenter Exception 10002000-10002999 --------------------- */
    /**
     * 树名称重复
     */
    DUPLICATED_TREE_NAME(10002000, "Tree name {0} is existed."),

    /**
     * 从属关系错误
     */
    TASK_NOT_IN_TENANT(10002001, "Task {0} not found in the tenant {1}."),
    TYPE_NOT_IN_TASK(10002002, "Task type {0} not found in the task {1}."),
    SOURCE_NOT_IN_TYPE(10002003, "Source {0} not found in the task type {1}."),
    CHANGE_TASK_TEMPLATE_IS_INVALID(10002004, "Cannot change template of a task."),

    /** ------------ TaskInstance Exception 10003000-10003999 --------------------- */
    /**
     * 获取field value失败
     */
    GET_FILED_VALUE_FAILED(10003000, "Get field value failed."),

    /**
     * 设置field value失败
     */
    SET_FILED_VALUE_FAILED(10003001, "Set field value failed."),

    /**
     * 当前租户无权限操作
     */
    TENANT_DO_NOT_HAVE_PERMISSION(10003002, "Tenant {0} don''t have permission to operate."),

    /**
     * 无法删除带有节点的树
     */
    CANNOT_DELETE_TREE_WITH_NODES(10003003, "Cannot delete tree with nodes."),

    /**
     * 无法删除带有子节点的节点
     */
    CANNOT_DELETE_NODE_WITH_CHILDREN(10003004, "Cannot delete node with children."),

    /**
     * 租户下已存在同样类型的节点
     */
    DUPLICATE_NODE_TYPE_IN_SAME_TENANT(10003005, "Tenant {0} already has node with type {1}."),

    /**
     * 父节点所属树Id与子节点所属树Id不一致
     */
    INCONSISTENT_TREE_ID_OF_PARENT_AND_NODE(10003006, "Inconsistent tree id of parent and node."),

    /**
     * 无法在DefinitionProperties中找到对应的Property
     */
    FAILED_TO_FIND_SPECIFY_PROPERTY(10003007, "Failed to find specify property, key is {0}."),

    /**
     * 无法在DefinitionProperty中找到对应的Handler
     */
    FAILED_TO_FIND_SPECIFY_HANDLER(10003008, "Failed to find specify handler, SourceDefinitionId is {0}."),

    /**
     * 无法找到对应的系统字段
     */
    FAILED_TO_FIND_SPECIFY_SYSTEM_FIELD(10003009, "Failed to find specify system field, field is {0}."),

    /**
     * 无法解码参数
     */
    FAILED_ENCODE_PARAM(10003010, "Failed to encode param, param name is {0}."),

    /**
     * 不允许修改带有数据源的任务定义属性
     */
    NOT_ALLOW_MODIFY_PROPERTY_OF_TASK_DEFINITION_HAS_SOURCE(10003011,
            "Modification of task definition property with data source not allowed."),

    /**
     * 未提供所属的数据源。
     */
    INSTANCE_SOURCE_REQUIRED(10003012, "The owning source required but not supplied."),

    /**
     * 所属数据源的唯一标识的格式不正确。
     */
    INSTANCE_SOURCE_INVALID(10003013, "The format of source id is incorrect."),

    /**
     * 任务实例所属的数据源不存在。
     */
    INSTANCE_SOURCE_NOT_FOUND(10003014, "The owning source not found."),

    /**
     * 未提供任务数据。
     */
    INSTANCE_INFO_REQUIRED(10003015, "The info of a task instance is required but not supplied."),
    INSTANCE_PROPERTY_REQUIRED(10003016, "The property {0} is required but not supplied."),
    INSTANCE_NOT_FOUND(10003017, "Instance {0} not found in task {1}."),

    /**
     * 未提供所属的任务。
     */
    TASK_ID_REQUIRED(10003018, "The owning task required but not supplied."),

    /**
     * 所属数据源的唯一标识的格式不正确。
     */
    TASK_ID_INVALID(10003019, "The format of task id is incorrect."),

    /**
     * 未提供所属的租户。
     */
    TENANT_REQUIRED(10003020, "The owning tenant required but not supplied."),

    /**
     * 所属租户的格式不正确。
     */
    TENANT_INVALID(10003021, "The format of tenant id is incorrect."),

    /**
     * 未提供所属的数据源。
     */
    SOURCE_SOURCE_REQUIRED(10003022, "The owning source required but not supplied."),

    /**
     * 所属数据源的唯一标识的格式不正确。
     */
    SOURCE_SOURCE_INVALID(10003023, "The format of source id is incorrect."),

    /**
     * 未提供任务定义名称
     */
    TASK_NAME_REQUIRED(10003024, "The name of task is required but not supplied."),

    /**
     * 任务定义名称长度超过最大值
     */
    TASK_NAME_LENGTH_OUT_OF_BOUNDS(10003025, "The length of task name is out of bounds."),

    /**
     * 任务定义名称长度小于最小值
     */
    TASK_NAME_LENGTH_LESS_THAN_BOUNDS(10003026, "The length of task name is less than bounds."),

    /**
     * 未提供所属的属性。
     */
    PROPERTY_REQUIRED(10003027, "The owning property required but not supplied."),

    /**
     * 所属属性的唯一标识的格式不正确。
     */
    PROPERTY_INVALID(10003028, "The format of property id is incorrect."),

    /**
     * 未提供所属的属性触发器。
     */
    TRIGGER_REQUIRED(10003029, "The owning trigger required but not supplied."),

    /**
     * 所属属性触发器的唯一标识的格式不正确。
     */
    TRIGGER_INVALID(10003030, "The format of trigger id is incorrect."),
    SOURCE_NAME_REQUIRED(10003031, "The source name is required but not supplied."),
    SOURCE_NAME_LENGTH_OUT_OF_BOUNDS(10003032, "The length of source name is out of bounds."),
    SOURCE_APP_REQUIRED(10003033, "The source app is required but not supplied."),
    SOURCE_APP_LENGTH_OUT_OF_BOUNDS(10003034, "The length of source app is out of bounds."),
    SOURCE_TYPE_REQUIRED(10003035, "The source type is required but not supplied."),
    SOURCE_TYPE_LENGTH_OUT_OF_BOUNDS(10003036, "The length of source type is out of bounds."),

    /**
     * 未提供所属属性触发器的属性名称.
     */
    PROPERTY_NAME_REQUIRED(1003037, "The property name required but not supplied."),

    /**
     * 未提供所属属性触发器的fitableId.
     */
    FITABLE_ID_REQUIRED(1003038, "The fitable id required but not supplied."),

    /**
     * 所属属性触发器的fitableId不正确.
     */
    FITABLE_ID_INVALID(1003039, "The format of fitable id is incorrect."),

    /**
     * 任务属性名称长度超过最大值
     */
    PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS(10003040, "The length of property name is out of bounds."),

    /**
     * 任务属性名称长度小于最小值
     */
    PROPERTY_NAME_LENGTH_LESS_THAN_BOUNDS(10003041, "The length of property name is less than bounds."),
    PROPERTY_DESCRIPTION_LENGTH_OUT_OF_BOUNDS(10003042, "The length of property description is out of bounds."),
    PROPERTY_DATATYPE_LENGTH_OUT_OF_BOUNDS(10003043, "The length of property dataType is out of bounds."),
    PROPERTY_SCOPE_LENGTH_OUT_OF_BOUNDS(10003044, "The length of property scope is out of bounds."),
    CANNOT_FIND_CORRESPONDING_CONSUMER(10003045, "Cannot find corresponding consumer of event."),
    TASK_CATEGORY_TRIGGER_CATEGORY_REQUIRED(10003046, "Category to trigger is required but not supplied."),
    TASK_NOT_FOUND(10003047, "Task {0} not found."),

    /**
     * 所属数据类型的唯一标识的格式不正确。
     */
    INSTANCE_TYPE_INVALID(10003048, "The format of type id is incorrect."),

    /**
     * 未提供所属的数据类型。
     */
    INSTANCE_TYPE_REQUIRED(10003049, "The owning type required but not supplied."),

    /**
     * 任务实例所属的类型不存在。
     */
    INSTANCE_TYPE_NOT_FOUND(10003050, "The owning type not found."),

    /**
     * 未提供所属的任务类型。
     */
    TYPE_ID_REQUIRED(10003051, "The owning type required but not supplied."),

    /**
     * 所属数据类型的唯一标识的格式不正确。
     */
    TYPE_ID_INVALID(10003052, "The format of type id is incorrect."),
    INSTANCE_ID_INVALID(10003053, "The format of instance id is incorrect."),
    INSTANCE_SOURCE_UNMODIFIABLE(10003054, "Cannot modify the source of a task instance."),
    INSTANCE_PROPERTY_VALUE_INCORRECT(10003055, "The value of property '{0}' must be in {1} type."),
    INSTANCE_TYPE_UNMODIFIABLE(10003056, "The owning type of a task instance cannot be modified."),
    PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES(10003057,
            "The property cannot be modified when the task has instances."),
    PROPERTY_CANNOT_BE_DELETED_WITH_INSTANCES(10003058, "The property cannot be deleted when the task has instances."),
    TYPE_NOT_FOUND(10003059, "The type does not exist."),
    TYPE_MORE_THAN_ONE(10003060, "The specify type more than one."),
    INSTANCE_EXISTS(10003061, "A task instance with the same identify already exists."),
    INSTANCE_EVENT_TYPE_REQUIRED(10003062, "The type of an instance event is required but not supplied."),
    INSTANCE_EVENT_TYPE_INCORRECT(10003063, "The type of an instance event is incorrect."),
    INSTANCE_EVENT_FITABLE_REQUIRED(10003064, "The fitable to handle instance event is required but not supplied."),
    /**
     * 任务实例保存失败
     */
    FAILED_TO_SAVE_INSTANCE(10003065, "Failed to save instance."),

    /**
     * 不支持该手动执行类型
     */
    FAILED_TO_GET_MANUAL_OPERATOR(10003066, "Failed to get operator, the manual type: {0}, are not supported."),

    /**
     * 任务属性不存在。
     */
    TASK_PROPERTY_NOT_FOUND(10003067, "The task property does not exist."),

    /**
     * 具有相同名称的任务属性已存在。
     */
    TASK_PROPERTY_NAME_EXIST(10003068, "The task property with the same already exist."),

    /**
     * 任务属性的数据类型不正确。
     */
    TASK_PROPERTY_DATA_TYPE_INVALID(10003069, "Invalid data type of task property."),

    /**
     * 任务属性的使用范围不正确。
     */
    TASK_PROPERTY_SCOPE_INVALID(10003070, "Invalid scope of task property."),

    /**
     * 不能向包含实例的任务定义中添加一个必填的属性。
     */
    NEW_PROPERTY_REQUIRED_WITH_INSTANCES(10003071,
            "Cannot add a property which is required when the owning task has instances."),

    /**
     * 当前租户中已存在相同名称的任务定义。
     */
    TASK_EXIST_IN_CURRENT_TENANT(10003072, "A task with the same name already exists in the current tenant."),

    /**
     * 其他租户中已存在相同名称的任务定义。
     */
    TASK_EXIST_IN_OTHER_TENANT(10003073, "A task with the same name already exists in another tenant."),

    /**
     * 任务实例已被删除。
     */
    INSTANCE_DELETED(10003074, "Task instance has been deleted."),

    /**
     * 数据源需要配置用以检索任务实例的服务实现。
     */
    REFRESH_IN_TIME_REQUIRE_RETRIEVE_FITABLE(10003075, "The fitable to retrieve source required but not found."),

    /**
     * 数据源需要配置用以查询任务实例的服务实现。
     */
    REFRESH_IN_TIME_REQUIRE_LIST_FITABLE(10003076, "The fitable to list sources required but not found."),

    /**
     * 数据源不存在。
     */
    SOURCE_NOT_FOUND(10003077, "The task source does not exist."),

    /**
     * 数据源不支持此操作。
     */
    SOURCE_NOT_SUPPORT(10003078, "The task source does not support this action."),

    /**
     * 未提供待授权的三方系统。
     */
    AUTHORIZATION_SYSTEM_REQUIRED(10003079, "The system to authorize is required but not supplied."),

    /**
     * 未提供三方系统授权的用户。
     */
    AUTHORIZATION_USER_REQUIRED(10003080, "The user to authorize is required but not supplied."),

    /**
     * 未提供三方系统授权的令牌。
     */
    AUTHORIZATION_TOKEN_REQUIRED(10003081, "The token to authorize is required but not supplied."),

    /**
     * 三方系统授权不存在。
     */
    AUTHORIZATION_NOT_FOUND(10003082, "The authorization does not exist."),

    /**
     * 三方系统授权的唯一标识格式不正确。
     */
    AUTHORIZATION_ID_INVALID(10003083, "Invalid id of authorization."),

    /**
     * 三方系统授权的系统名称的长度超出限制。
     */
    AUTHORIZATION_SYSTEM_TOO_LONG(10003084, "The system of authorization is too long."),

    /**
     * 三方系统授权的用户唯一标识的长度超出限制。
     */
    AUTHORIZATION_USER_TOO_LONG(10003085, "The user id of authorization is too long."),

    /**
     * 三方系统授权的令牌有效期不能为负数。
     */
    AUTHORIZATION_EXPIRATION_NEGATIVE(10003086, "The token expiration of authorization cannot be negative."),

    /**
     * 未指定索引的名称。
     */
    INDEX_NAME_REQUIRED(10003087, "The name of index is required but not supplied."),

    /**
     * 索引中未指定属性。
     */
    INDEX_PROPERTY_REQUIRED(10003088, "No property specified in index."),

    /**
     * 索引的属性在任务定义中不存在。
     */
    INDEX_UNKNOWN_PROPERTY(10003089, "The property to index does not exist in task."),

    /**
     * 索引唯一标识的格式不正确。
     */
    INDEX_ID_INVALID(10003090, "The id of index is incorrect."),

    /**
     * 索引不存在。
     */
    INDEX_NOT_FOUND(10003091, "The index does not exist."),

    /**
     * 索引名称的长度超出限制。
     */
    INDEX_NAME_LENGTH_OUT_OF_BOUNDS(10003092, "The length of index name is out of bounds."),

    /**
     * 已存在同名的索引。
     */
    INDEX_NAME_DUPLICATE(10003093, "An index with the same name already exists."),

    /**
     * 暂不支持非info中字段进行排序
     */
    ORDER_BY_PROPERTY_NAME_NOT_SUPPORT(10003094, "Not support order by property name without info. prefix."),

    /**
     * OrderBy的属性不存在
     */
    PROPERTY_NOT_EXIST_IN_ORDER_BY_PARAM(10003095, "Property not exist in order by param."),

    /**
     * 超出Statistic统计后结果最大值
     */
    EXCEEDED_STATISTICAL_LIMIT_RESULT_SIZE(10003096, "Exceeded statistical limit result size."),

    /**
     * 属性的数据类型不支持建立索引。
     */
    INDEX_DATA_TYPE_UNSUPPORTED(10003097, "The property in the data type cannot be indexed."),
    LAST_VERSION_NOT_PUBLISH(10003098, "The meta version {0}:{1} not publish, can't create new version!"),
    /**
     * ------------ TaskTemplate Exception 10003200-10003299 ---------------------
     */
    TASK_TEMPLATE_NAME_REQUIRED(10003200, "The task template name required but not supplied."),
    TASK_TEMPLATE_NAME_EXIST(10003201, "The task template with the same already exist in same tenant."),
    TASK_TEMPLATE_EMPTY_DECLARATION(10003202, "The task template declaration is empty."),
    TASK_TEMPLATE_ID_INVALID(10003203, "Invalid id of task template."),
    TASK_TEMPLATE_NOT_FOUND(10003204, "The task template does not exist."),
    TASK_TEMPLATE_FOUND_MORE_THAN_ONE(10003205, "Found the task template more than 1 by id."),
    TASK_TEMPLATE_NAME_NO_MODIFY(10003206, "The task template name is the same as the original one."),
    TASK_TEMPLATE_USED(10003207, "The task template cannot be deleted when the template is used."),
    TASK_TEMPLATE_DECLARATION_NOT_NULL(10003208, "The task template declaration is null."),
    TASK_TEMPLATE_IS_PARENT(10003209, "The task template cannot be deleted when the template is parent template."),
    /**
     * ------------ TaskTemplateProperty Exception 10003300-10003399 ---------------------
     */
    TEMPLATE_PROPERTY_NAME_REQUIRED(10003300, "The template property name required but not supplied."),
    TEMPLATE_PROPERTY_NAME_EXIST(10003301, "The task template property with the same already exist."),
    TEMPLATE_PROPERTY_DATA_TYPE_INVALID(10003302, "Invalid data type of task template property."),
    TEMPLATE_PROPERTY_NAME_NO_MODIFY(10003303, "The old property name is equals new name."),
    TEMPLATE_PROPERTY_DATA_TYPE_NO_MODIFY(10003304, "The old property dataType is equals new dataType."),
    TEMPLATE_PROPERTY_USED(10003305, "The property cannot be modified or deleted when the property is used."),
    TEMPLATE_PROPERTY_NOT_FOUND(10003306, "The task template property does not exist."),
    TEMPLATE_PROPERTY_ID_INVALID(10003307, "Invalid id of task template property."),
    TEMPLATE_ID_INVALID_IN_PROPERTY(10003308, "Invalid task template id of task template property."),
    PROPERTY_HAS_USED_TEMPLATE(10003309, "The task property has used a template and cannot be modified."),
    PROPERTY_TEMPLATE_EXCEPT_NOT_EQUALS_ACTUAL(10003310,
            "The task property template except to used is not equals actual."),
    PROPERTY_DATA_TYPE_NOT_EQUALS_TEMPLATE(10003311,
            "The task property uses data_type that is different from that in the template."),
    PROPERTY_NAME_NOT_EQUALS_TEMPLATE(10003312,
            "The task property uses name that is different from that in the template."),
    /*
     * 标签错误码：10004xxx
     */
    TAG_REQUIRED(10004001, "The tag is required but not supplied."),
    TAG_LENGTH_OUT_OF_BOUNDS(10004002, "The length of tag is out of bounds."),
    TAG_DESCRIPTION_LENGTH_OUT_OF_BOUNDS(10004003, "The length of tag description is out of bounds."),
    TAG_OBJECT_TYPE_REQUIRED(10004004, "The object type is required but not supplied."),
    TAG_OBJECT_TYPE_OUT_OF_BOUNDS(10004005, "The length of object type is out of bounds."),
    TAG_OBJECT_ID_REQUIRED(10004006, "The object id is required but not supplied."),
    TAG_OBJECT_ID_FORMAT_INCORRECT(10004007, "The format of object id is incorrect."),
    /*
     * 任务树错误码：10005xxx
     */
    TREE_NAME_REQUIRED(10005001, "The name of a task tree is required but not supplied."),
    TREE_NAME_LENGTH_OUT_OF_BOUNDS(10005002, "The length of task tree name is out of bounds."),
    TREE_NOT_FOUND(10005003, "The task tree does not exist."),
    TYPE_NAME_REQUIRED(10005004, "The name of task type is required but not supplied."),
    TYPE_NAME_LENGTH_OUT_OF_BOUNDS(10005005, "The length of task type name is out of bounds."),
    NODE_ID_INVALID(10005006, "The format of node id is incorrect."),
    TREE_ID_INVALID(10005007, "The format of tree id is incorrect."),
    NODE_NOT_FOUND(10005008, "The tree node does not exist."),
    NODE_DELETING_HAS_SOURCES(10005009, "The tree node to delete has bound sources."),
    TREE_DELETING_HAS_NODES(10005010, "The tree to delete has bound nodes."),
    NODE_DELETING_HAS_NODES(10005011, "The node to delete has bound nodes."),
    TYPE_NAME_ALREADY_EXISTS(10005012, "A task type with the same name already exists."),
    TYPE_PARENT_ID_INVALID(10005013, "The format of parent task type id is incorrect."),
    UNKNOWN_ORDER(10005014, "Unknown order to sort: {0}"),
    PROPERTY_REQUIRED_TO_SORT(10005015, "No property specified to sort."),
    TYPE_PARENT_ID_NOT_EXISTS(10005016, "The parent task type id is not exits."),
    /*
     * 类目错误码：10006xxx
     */
    UNKNOWN_CATEGORY(10006001, "Unknown categories: {0}."),
    PROPERTY_CATEGORY_VALUE_REQUIRED(10006002,
            "The value of property for specific category is required but not supplied."),
    PROPERTY_CATEGORY_REQUIRED(10006003, "The category for specific value of property is required but not supplied."),

    /** ------------ FlowEngines Exception 10007000-10007999 --------------------- */
    /**
     * 流程节点转换不支持操作
     */
    FLOW_NODE_CREATE_ERROR(10007000, "Processor can not be null during create flowable node."),

    /**
     * 流程节点不支持执行操作
     */
    FLOW_NODE_OPERATOR_NOT_SUPPORT(10007001, "Flow node with id: {0}, type: {1}, for operator [{2}] not supported."),

    /**
     * 流程没有开始节点
     */
    FLOW_HAS_NO_START_NODE(10007002, "Flow definition with id: {0} has no start node."),

    /**
     * 流程任务不支持执行操作
     */
    FLOW_TASK_OPERATOR_NOT_SUPPORT(10007003, "Flow task with name: {0}, type: {1}, are not supported."),

    /**
     * 流程执行错误，没有手动执行任务
     */
    FLOW_ENGINE_INVALID_MANUAL_TASK(10007004, "Flow engine executor error for invalid manual task."),

    /**
     * 流程执行错误，非法节点Id
     */
    FLOW_ENGINE_INVALID_NODE_ID(10007005, "Flow engine executor error for invalid node id: {0}."),

    /**
     * 流程定义解析失败
     */
    FLOW_ENGINE_PARSER_NOT_SUPPORT(10007010, "Flow engine parser not support {0} operator."),

    /**
     * 流程启动失败
     */
    FLOW_START_ERROR(10007011, "Flow status is invalid"),
    FLOW_EXECUTE_FITABLE_TASK_FAILED(10007012,
            "execute jober failed, jober name: {0}, jober type: {1}, fitables: {2}, errors: {3}"),

    /**
     * 流程执行不支持发送事件
     */
    FLOW_SEND_EVENT_NOT_SUPPORT(100070013, "Flow send event are not supported."),

    /**
     * 流程引擎数据库不支持该操作
     */
    FLOW_ENGINE_DATABASE_NOT_SUPPORT(100070014, "Operation :{0} is not supported."),

    /**
     * 流程定义更新失败
     */
    FLOW_DEFINITION_UPDATE_NOT_SUPPORT(100070015, "Flow status :{0} update not supported."),

    /**
     * 通过eventMetaId查询to节点失败
     */
    FLOW_FIND_TO_NODE_BY_EVENT_FAILED(100070016, "Find to node by event metaId :{0} failed."),
    FLOW_GRAPH_NOT_FOUND(100070017, "Flow graph id: {0} version: {1} not found."),
    FLOW_MODIFY_PUBLISHED_GRAPH(100070018,
            "graph data with id: {0} version: {1} has been published, can not be modified"),
    FLOW_ID_NOT_MATCH(100070019, "Flow id {0} does not match id {1} in data."),
    FLOW_GRAPH_DATA_PARSE_FAILED(100070020, "Parse graph data failed."),
    FLOW_HANDLE_SMART_FORM_FAILED(100070021, "Failed to handle the smart form task."),
    FLOW_TERMINATE_FAILED(100070022,
            "Failed to terminate flows by trace id {0}, when the flow status is error, archived or terminate."),

    /**
     * ElsaFlowsGraphRepo不支持该操作
     */
    ELSA_FLOW_GRAPH_NOT_SUPPORT(100070023, "Operation :{0} is not supported."),

    /**
     * DbFlowsGraphRepo 不支持该操作
     */
    NOT_SUPPORT(100070024, "Operation :{0} is not supported."),

    /**
     * 流程已存在
     */
    FLOW_ALREADY_EXIST(100070025, "flow already exist, {0}."),

    /**
     * 流程回调函数执行fitables失败
     */
    FLOW_EXECUTE_CALLBACK_FITABLES_FAILED(10007026,
            "Failed to execute callback, callback name: {0}, callback type: {1}, fitables: {2}, errors: {3}"),

    /**
     * 流程引擎OhScript语法错误
     */
    FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR(100070024, "OhScript grammar error. Source Code: {0}"),

    /**
     * 流程引擎条件规则变量未找到
     */
    FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR(100070025, "Condition rule parse error. Condition Rule: {0}"),

    /**
     * 找不到对应流程节点
     */
    FLOW_NODE_NOT_FOUND(100070024, "Flow node id {0} not found, flow meta id {1}, version {2}."),

    /**
     * flow节点任务数达到最大值
     */
    FLOW_NODE_MAX_TASK(100070024, "Flow node id {0} tasks over the limit."),

    /**
     * 流程自动任务特定异常重试失败
     */
    FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED(10007024, "Failed to update the retry record for retryJober, toBatch: {0}"),

    /**
     * 异步jober执行失败
     */
    FLOW_EXECUTE_ASYNC_JOBER_FAILED(10007027, "execute async jober failed."),

    /**
     * 流程执行过程出现异常
     */
    FLOW_ENGINE_EXECUTOR_ERROR(10007500,
            "Flow engine executor errors " + "stream id: {0}, node id: {1}, name: {2}, exception: {3}, errors: {4}."),

    /**
     * 流程执行过程通过ohscript调用fitable出现异常
     */
    FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR(10007501,
            "Error code: 10007501, Flow engine executor ohscript code error when invoke fitable."),

    /**
     * 流程定义删除失败
     */
    FLOW_DEFINITION_DELETE_ERROR(10007502, "Error code: 10007502, Flow definition delete error"),

    /**
     * 流程出现系统错误
     */
    FLOW_SYSTEM_ERROR(10007503, "SYSTEM ERROR"),

    /**
     * 流程调用过程出现网络错误
     */
    FLOW_NETWORK_ERROR(10007504, "Error code: 10007504, Network error when Invoke fitable"),

    /**
     * 流程执行过程中不支持处理该类型
     */
    CONTEXT_TYPE_NOT_SUPPORT(10007505, "Not support this type."),

    /**
     * 中间节点连接线不合法
     */
    INVALID_STATE_NODE_EVENT_SIZE(10007518, "State node event size must be 1, please check config"),

    /**
     * 节点对应的event个数不合法
     */
    INVALID_EVENT_SIZE(10007506, "Error code: 10007506, Invalid event size."),

    /**
     * 流程storeJober调用过程执行出错
     */
    FLOW_STORE_JOBER_INVOKE_ERROR(10007507, "Flow store jober invoke error, tool id:{0}."),

    /**
     * 流程httpJober调用过程执行出错
     */
    FLOW_HTTP_JOBER_INVOKE_ERROR(10007508, "Flow http jober invoke error."),

    /**
     * 流程genericableJober调用过程执行出错
     */
    FLOW_GENERICALBE_JOBER_INVOKE_ERROR(10007509, "Flow genericable jober invoke error."),

    /**
     * 流程generalJober调用过程执行出错
     */
    FLOW_GENERAL_JOBER_INVOKE_ERROR(100075010, "Flow general jober invoke error."),

    /**
     * 条件节点执行出错
     */
    CONDITION_NODE_EXEC_ERROR(10007511, "Condition node executor error."),

    /**
     * 流程图保存失败
     */
    FLOW_GRAPH_SAVE_ERROR(10007512, "Flow graph save error, flow id: {0}, version: {1}."),

    /**
     * 流程图升级失败
     */
    FLOW_GRAPH_UPGRADE_ERROR(10007513, "Flow graph upgrade error, flow id: {0}, version: {1}."),

    /**
     * 流程校验失败
     */
    FLOW_VALIDATE_ERROR(10007514, "Flow graph validate error, detail: {0}"),

    /**
     * 流程节点个数不合法
     */
    INVALID_FLOW_NODE_SIZE(10007515, "Node size must more than 3, please check config"),

    /**
     * 开始节点连接线不合法
     */
    INVALID_START_NODE_EVENT_SIZE(10007516, "Start node event size must be 1, please check config"),

    /**
     * 连接线配置不合法
     */
    INVALID_EVENT_CONFIG(10007517, "Event config is invalid, event id: {0}"),
    /*
     * 租户错误码：10008xxx
     */
    TENANT_NAME_REQUIRED(10008001, "The name of a tenant is required but not supplied."),
    TENANT_NAME_LENGTH_OUT_OF_BOUNDS(10008002, "The length of tenant name is out of bounds."),
    TENANT_NOT_FOUND(10008003, "The tenant does not exist."),
    TENANT_ID_INVALID(10008004, "The format of tenant id is incorrect."),
    TENANT_DELETING_HAS_TASKS(10008005, "The tenant to delete has bound tasks."),
    TENANT_MEMBERS_REQUIRED(10008006, "The tenant members required but not supplied."),
    TENANT_IS_EXISTS(10008007, "The tenant with same name already exists."),
    MEMBER_NOT_FOUND(10008008, "The tenant member does not exist."),
    MEMBER_IS_EXISTS(10008009, "The tenant member already exists."),
    MEMBER_ID_INVALID(10008010, "The format of tenant member id is incorrect."),
    /*
     * 文件错误码：10009xxx
     */
    FILE_CONTENT_LENGTH_OUT_OF_BOUNDS(10009001, "The content length of file is out of bounds."),
    FILE_CONTENT_REQUIRED(10009002, "The content of file is required but not supplied."),

    /**
     * 所属文件的唯一标识的格式不正确。
     */
    FILE_ID_INVALID(10009003, "The format of file id is incorrect."),
    FILE_NOT_FOUND(10009004, "The file does not exist."),
    FILE_CONTENT_LENGTH_NOT_ANNOUNCE(10009005, "The content length of file not announce."),
    FILE_NAME_NOT_ANNOUNCE(10009006, "The name of file not announce."),
    FILE_NAME_REQUIRED(10009007, "The name of file is required but not supplied."),
    FILE_TYPE_NOT_DEFINE(10009008, "File type not define."),
    FILE_TYPE_NOT_SUPPORT_MULTIPART(10009009, "File not support Multipart type."),
    FILE_NAME_NOT_ESCAPE(10009010, "File name escape error."),

    /**
     * ------------ OperationRecord Exception. From 10010000 to 10010999 ---------------------
     */
    OPERATION_RECORD_DECLARATION_FIELD_IS_NULL(10010000, "Operation record declaration field {0} is null."),
    OPERATION_RECORD_DECLARATION_IS_NULL(10010001, "Operation record declaration is null."),
    OPERATION_RECORD_LIST_FILTER_FIELD_IS_EMPTY(10010002, "Operation record list filter field {0} is empty."),

    /**
     * ------------ TaskRelation Exception. From 10011000 to 10011999 ---------------------
     */
    TASK_RELATION_OBJECT_ID1_REQUIRED(10011001, "The objectId1 of task relation is required, but not provided."),
    TASK_RELATION_OBJECT_TYPE1_REQUIRED(10011002, "The objectType1 of task relation is required, but not provided."),
    TASK_RELATION_OBJECT_ID2_REQUIRED(10011003, "The objectId2 of task relation is required, but not provided."),
    TASK_RELATION_OBJECT_TYPE2_REQUIRED(10011004, "The objectType2 of task relation is required, but not provided."),
    TASK_RELATION_RELATION_TYPE_REQUIRED(10011005, "The relationType of task relation is required, but not provided."),

    /**
     * 任务关联的关联方的唯一标识的长度超出限制。
     */
    TASK_RELATION_OBJECT_ID1_TOO_LONG(10011006, "The objectId1 of task relation is too long."),
    TASK_RELATION_OBJECT_TYPE1_TOO_LONG(10011007, "The objectType1 of task relation is too long."),
    TASK_RELATION_OBJECT_ID2_TOO_LONG(10011008, "The objectId2 of task relation is too long."),
    TASK_RELATION_OBJECT_TYPE2_TOO_LONG(10011009, "The objectType2 of task relation is too long."),
    TASK_RELATION_RELATION_TYPE_TOO_LONG(10011010, "The relationType of task relation is too long."),

    /**
     * 任务关联的唯一标识格式不正确。
     */
    TASK_RELATION_RELATION_ID_INVALID(10011011, "Invalid id of task relation."),

    /**
     * 任务关联关系不存在。
     */
    TASK_RELATION_RELATION_NOT_FOUND(10011012, "The task relation does not exist."),

    /**
     * 两个任务关联关系已存在
     */
    TASK_RELATION_EXIST_RELATION(10011013, "Two task relations already exist"),

    /**
     * ------------ Task Agenda Exception. From 10013000 to 10013999 ---------------------
     */

    TASK_AGENDA_NO_TEMPLATE_ID(10013001, "The templateId required, but not provided."),
    /**
     * filter对应的属性不存在
     */
    TASK_AGENDA_NOT_EXIST_IN_FILTER_PARAM(10013002,
            "The corresponding property for the parameter in the filter does not exist."),
    /**
     * 模板id没有对应的属性
     */
    TASK_AGENDA_NOT_EXIST_IN_TEMPLATE_PARAM(10013003, "property to the template ID does not exist."),
    TASK_AGENDA_NO_TASK(10013004, "The task ID corresponding to the template does not exist. templateId:{0}"),
    ;

    private final Integer errorCode;

    private final String message;

    ErrorCodes(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * 通过code获取枚举
     *
     * @param code code
     * @return 枚举对象
     */
    public static ErrorCodes getErrorCodes(int code) {
        return Arrays.stream(ErrorCodes.values())
                .filter(o -> code == o.errorCode)
                .findFirst()
                .orElse(ErrorCodes.SERVER_INTERNAL_ERROR);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "err " + this.errorCode + ": " + this.message;
    }
}
