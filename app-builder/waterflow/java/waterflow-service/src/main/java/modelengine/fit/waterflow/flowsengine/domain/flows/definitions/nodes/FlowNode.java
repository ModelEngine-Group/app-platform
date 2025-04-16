/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static modelengine.fit.waterflow.ErrorCodes.CONDITION_NODE_EXEC_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.CONTEXT_TYPE_NOT_SUPPORT;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_EXECUTE_ASYNC_JOBER_FAILED;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_GENERAL_JOBER_INVOKE_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_STORE_JOBER_INVOKE_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_SYSTEM_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.TYPE_CONVERT_FAILED;
import static modelengine.fit.waterflow.spi.FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE;
import static modelengine.fitframework.util.ObjectUtils.cast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.execptions.OhscriptExecuteException;
import modelengine.fit.waterflow.execptions.TypeNotSupportException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.ContextErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStage;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeTriggerMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程定义节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowNode {
    private static final Logger log = Logger.get(FlowNode.class);

    /**
     * 流程节点metaId，与前端保持一致
     */
    protected String metaId;

    /**
     * 流程节点名称
     */
    protected String name;

    /**
     * 流程节点类型
     */
    protected FlowNodeType type;

    /**
     * 流程节点触发类型
     */
    protected FlowNodeTriggerMode triggerMode;

    /**
     * 流程节点属性Map，key为属性的键值，value为属性具体的值
     */
    protected Map<String, Object> properties;

    /**
     * 流程节点事件列表
     */
    protected List<FlowEvent> events;

    /**
     * 流程节点自动任务
     */
    protected FlowJober jober;

    /**
     * 流程节点自动任务数据过滤器
     */
    protected FlowFilter joberFilter;

    /**
     * 流程节点手动任务
     */
    protected FlowTask task;

    /**
     * 流程节点手动任务数据过滤器
     */
    protected FlowFilter taskFilter;

    /**
     * 流程节点回调函数
     */
    protected FlowCallback callback;

    /**
     * 流程节点对应的{@link FitStream.Processor}
     */
    protected FitStream.Processor<FlowData, FlowData> processor;

    /**
     * 所属的flow definition， 后续需要提取WaterFlow结构替换，node有归属的WaterFlow
     */
    protected FlowDefinition parentFlow;

    /**
     * 调用 fitable客户端
     */
    protected BrokerClient brokerClient;

    /**
     * 节点任务异常处理fitables集合
     */
    protected Set<String> exceptionFitables;

    public void setJober(FlowJober jober) {
        if (!Objects.isNull(jober)) {
            jober.setParentNode(this);
        }
        this.jober = jober;
    }

    /**
     * 判断节点是否属于该类型
     *
     * @param type {@link FlowNodeType} 判断类型
     * @return 节点是否属于该类型
     */
    public boolean belongTo(FlowNodeType type) {
        return this.type.equals(type);
    }

    /**
     * 获取节点内的publisher
     * 只有开始节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Processor}
     */
    public FitStream.Publisher<FlowData> getPublisher(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getPublisher");
    }

    /**
     * 获取节点内的processor
     * 只有普通节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Processor}
     */
    public FitStream.Processor<FlowData, FlowData> getProcessor(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getProcessor");
    }

    /**
     * 获取节点内的subscriber
     * 只有结束节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Subscriber}
     */
    public FitStream.Subscriber<FlowData, FlowData> getSubscriber(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getSubscriber");
    }

    /**
     * from节点subscribe下一个节点
     *
     * @param streamId stream流程Id
     * @param flowEnv flowEnv
     * @param toNode {@link FlowNode}下一个节点
     * @param event {@link FlowEvent}节点之间的连线
     */
    public void subscribe(String streamId, FlowEnv flowEnv, FlowNode toNode, FlowEvent event) {
        this.subscribe(getFrom(streamId, flowEnv.getRepo(), flowEnv.getMessenger(), flowEnv.getLocks()),
                getTo(streamId, flowEnv.getRepo(), flowEnv.getMessenger(), flowEnv.getLocks(), toNode), event);
    }

    /**
     * 获取节点事件
     *
     * @param metaId 事件metaId
     * @return {@link FlowEvent}节点事件
     */
    public Optional<FlowEvent> getEventByMetaId(String metaId) {
        return this.events.stream().filter(e -> e.getMetaId().equals(metaId)).findFirst();
    }

    /**
     * subscribe
     *
     * @param from from
     * @param to to
     * @param event event
     */
    protected void subscribe(FitStream.Publisher<FlowData> from, FitStream.Subscriber<FlowData, FlowData> to,
            FlowEvent event) {
        from.subscribe(event.getMetaId(), to);
    }

    /**
     * errorHandler
     *
     * @param streamId streamId
     * @return Processors.Error<FlowData>
     */
    protected Processors.Error<FlowData> errorHandler(String streamId) {
        return (exception, retry, flowContexts) -> {
            if (retry.isNeedRetry(exception, flowContexts)) {
                String toBatch = flowContexts.stream()
                        .filter(c -> StringUtils.isNotEmpty(c.getToBatch()))
                        .findAny()
                        .map(FlowContext::getToBatch)
                        .orElseGet(UUIDUtil::uuid);
                flowContexts.forEach(context -> context.setStatus(FlowNodeStatus.RETRYABLE).toBatch(toBatch));
            } else {
                notifyException(exception, flowContexts);
                String errorMessage = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId,
                        this.metaId, this.name, exception.getClass().getSimpleName(),
                        Optional.ofNullable(exception.getMessage()).orElse("internal error"));
                ContextErrorInfo errorInfo;
                if ((exception instanceof OhscriptExecuteException) && (exception.getCause() instanceof FitException)) {
                    errorInfo = ContextErrorInfo.builder()
                            .errorMessage(exception.getMessage())
                            .errorCode(((FitException) exception.getCause()).getCode())
                            .build();
                    errorInfo.getProperties().put("fitableId", ((OhscriptExecuteException) exception).getFitableId());
                } else if (exception instanceof TypeNotSupportException) {
                    errorInfo = ContextErrorInfo.builder()
                            .errorCode(CONTEXT_TYPE_NOT_SUPPORT.getErrorCode())
                            .errorMessage(CONTEXT_TYPE_NOT_SUPPORT.getMessage())
                            .build();
                } else {
                    errorInfo = ContextErrorInfo.builder()
                            .errorCode(FLOW_ENGINE_EXECUTOR_ERROR.getErrorCode())
                            .build();
                }
                errorInfo.setNodeName(this.getName());
                ContextErrorInfo finalErrorInfo = errorInfo;
                flowContexts.forEach(context -> {
                    context.setStatus(FlowNodeStatus.ERROR);
                    context.getData().setErrorMessage(errorMessage);
                    context.getData().setErrorInfo(finalErrorInfo);
                });
            }
            retry.process(exception, flowContexts);
        };
    }

    /**
     * 提醒异常处理.
     *
     * @param ex 异常对象.
     * @param inputs 上下文对象列表.
     */
    public void notifyException(Exception ex, List<FlowContext<FlowData>> inputs) {
        updateContextData(inputs, FlowNodeStage.AFTER);
        FlowErrorInfo errorInfo = getErrorInfo(ex);

        for (String fitableId : exceptionFitables) {
            try {
                this.brokerClient.getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE)
                        .route(new FitableIdFilter(fitableId))
                        .invoke(this.metaId, filterFlowData(inputs), errorInfo);
            } catch (FitException exception) {
                log.error("Notify exception error, exception fitableId:{}.", fitableId);
                log.error("exception: ", exception);
            }
        }
    }

    private FlowErrorInfo getErrorInfo(Exception ex) {
        FlowErrorInfo errorInfo = new FlowErrorInfo();
        errorInfo.setNodeName(this.name);
        if (!(ex instanceof WaterflowException)) {
            setErrorInfo(errorInfo, FLOW_SYSTEM_ERROR.getErrorCode(), FLOW_SYSTEM_ERROR.getMessage(), new String[0]);
            return errorInfo;
        }

        WaterflowException jobberException = (WaterflowException) ex;
        Object[] args = Optional.ofNullable(jobberException.getArgs()).orElse(new Object[0]);
        int errorCode = jobberException.getCode();
        if (Objects.equals(errorCode, FLOW_STORE_JOBER_INVOKE_ERROR.getErrorCode())) {
            setJoberErrorInfo(errorInfo, jobberException);
            errorInfo.getProperties().put("toolId", args[0].toString());
        } else if (Objects.equals(errorCode, FLOW_GENERAL_JOBER_INVOKE_ERROR.getErrorCode())) {
            setJoberErrorInfo(errorInfo, jobberException);
        } else if (isConditionNodeError(errorCode)) {
            setErrorInfo(errorInfo, CONDITION_NODE_EXEC_ERROR.getErrorCode(), CONDITION_NODE_EXEC_ERROR.getMessage(),
                    new String[0]);
        } else if (Objects.equals(errorCode, FLOW_EXECUTE_ASYNC_JOBER_FAILED.getErrorCode())) {
            Object[] exceptionArgs = jobberException.getArgs();
            boolean isErrorArgs = exceptionArgs == null || exceptionArgs.length == 0;
            if (isErrorArgs || !(exceptionArgs[0] instanceof JoberErrorInfo)) {
                setErrorInfo(errorInfo, FLOW_SYSTEM_ERROR.getErrorCode(), jobberException.getMessage(),
                        new String[0]);
                return errorInfo;
            }
            JoberErrorInfo info = (JoberErrorInfo) exceptionArgs[0];
            setErrorInfo(errorInfo, info.getCode(), info.getMessage(), info.getArgs());
        } else {
            setErrorInfo(errorInfo, jobberException.getCode(), jobberException.getMessage(),
                    Arrays.copyOf(Optional.ofNullable(jobberException.getArgs()).orElse(new Object[0]),
                            args.length, String[].class));
        }
        return errorInfo;
    }

    private void setJoberErrorInfo(FlowErrorInfo errorInfo, WaterflowException jobberException) {
        if (!(jobberException.getCause() instanceof FitException originalException)) {
            setErrorInfo(errorInfo, FLOW_SYSTEM_ERROR.getErrorCode(), jobberException.getCause().getMessage(),
                    new String[0]);
            return;
        }
        String message = this.getActualMessage(originalException);
        setErrorInfo(errorInfo, originalException.getCode(), message, new String[0]);
        errorInfo.setProperties(new HashMap<>());
        errorInfo.getProperties().put("fitableId", originalException.getProperties().get("fitableId"));
    }

    private static void setErrorInfo(FlowErrorInfo errorInfo, int code, String message, String[] args) {
        errorInfo.setErrorCode(code);
        errorInfo.setErrorMessage(message);
        errorInfo.setArgs(args);
    }

    private static boolean isConditionNodeError(int errorCode) {
        return Objects.equals(errorCode, TYPE_CONVERT_FAILED.getErrorCode())
                || Objects.equals(errorCode, FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR.getErrorCode())
                || Objects.equals(errorCode, FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR.getErrorCode());
    }

    private List<Map<String, Object>> filterFlowData(List<FlowContext<FlowData>> inputs) {
        return inputs.stream().map(cxt -> new HashMap<String, Object>() {
            {
                put(Constant.TRACE_ID_KEY, cxt.getTraceId());
                put(Constant.BUSINESS_DATA_KEY, cxt.getData().getBusinessData());
                put(Constant.CONTEXT_DATA, cxt.getData().getContextData());
                put(Constant.PASS_DATA, cxt.getData().getPassData());
                put("status", cxt.getStatus().name());
                put("createAt", cxt.getCreateAt());
                put("updateAt", cxt.getUpdateAt());
                put("archivedAt", cxt.getArchivedAt());
            }
        }).collect(Collectors.toList());
    }

    /**
     * 当用户给流程节点配置回调函数时，设置回调函数处理机制
     *
     * @param subscriber {@link FitStream.Subscriber} 表示流程节点内的subscriber
     * @param messenger {@link FlowContextMessenger} 表示stream流程事件发送器
     */
    protected void setCallback(FitStream.Subscriber<FlowData, FlowData> subscriber, FlowContextMessenger messenger) {
        subscriber.onComplete(c -> {
            List<FlowContext<FlowData>> flowContexts = c.getAll();
            try {
                flowContexts.forEach(
                    flowContext -> flowContext.getData().getContextData().put("nodeType", this.getType().getCode()));
                Optional.ofNullable(this.callback).ifPresent(callback -> callback.execute(flowContexts));
            } catch (Exception ex) {
                log.error("Exception:", ex);
                log.error("Feedback error, node id:{}", this.metaId);
            }

            Optional.ofNullable(parentFlow.getCallback())
                    .ifPresent(callback -> messenger.sendCallback(callback, flowContexts));
        });
    }

    /**
     * 跟踪回调节点执行前后的数据
     *
     * @param subscriber {@link FitStream.Subscriber} 表示流程节点内的subscriber
     * @param messenger {@link FlowContextMessenger} 表示stream流程事件发送器
     */
    protected void setGlobalTrace(FitStream.Subscriber<FlowData, FlowData> subscriber, FlowContextMessenger messenger) {
        subscriber.onGlobalBefore(c -> Optional.ofNullable(parentFlow.getCallback())
                .ifPresent(callback -> doGlobalTrace(c.getAll(), FlowNodeStage.BEFORE, messenger, callback)));

        subscriber.onGlobalAfter(c -> Optional.ofNullable(parentFlow.getCallback())
                .ifPresent(callback -> doGlobalTrace(c.getAll(), FlowNodeStage.AFTER, messenger, callback)));
    }

    private void doGlobalTrace(List<FlowContext<FlowData>> contexts, FlowNodeStage stage,
            FlowContextMessenger messenger, FlowCallback callback) {
        updateContextData(contexts, stage);
        messenger.sendCallback(callback, contexts);
    }

    private void updateContextData(List<FlowContext<FlowData>> contexts, FlowNodeStage stage) {
        contexts.forEach(context -> {
            FlowData flowData = FlowData.copyContextData(context.getData());
            Map<String, Object> contextData = flowData.getContextData();
            contextData.put(Constant.NODE_META_ID_KEY, this.getMetaId());
            contextData.put(Constant.NODE_TYPE_KEY, this.getType().getCode());
            contextData.put(Constant.NODE_PROPERTIES_KEY, this.getProperties());
            contextData.put(Constant.NODE_STAGE_KEY, stage.name());
            context.setData(flowData);
        });
    }

    /**
     * 查询所属流程的配置信息
     *
     * @param key 属性key
     * @return 属性对象
     */
    protected <T> T getFlowProperty(String key) {
        return cast(this.parentFlow.getProperties().get(key));
    }

    private FitStream.Publisher<FlowData> getFrom(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        FitStream.Publisher<FlowData> publisher;
        if (this.belongTo(FlowNodeType.START)) {
            publisher = this.getPublisher(streamId, repo, messenger, locks);
        } else {
            publisher = this.getProcessor(streamId, repo, messenger, locks);
        }
        return publisher;
    }

    private FitStream.Subscriber<FlowData, FlowData> getTo(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNode toNode) {
        FitStream.Subscriber<FlowData, FlowData> subscriber;
        if (toNode.belongTo(FlowNodeType.END)) {
            subscriber = toNode.getSubscriber(streamId, repo, messenger, locks);
        } else {
            subscriber = toNode.getProcessor(streamId, repo, messenger, locks);
        }
        return subscriber;
    }

    /**
     * 批量添加节点元数据信息
     *
     * @param inputs 上下文信息集合
     */
    protected void addContextData(List<FlowContext<FlowData>> inputs) {
        inputs.forEach(this::addContextData);
    }

    /**
     * 添加节点元数据信息
     *
     * @param flowContext 上下文信息
     */
    protected void addContextData(FlowContext<FlowData> flowContext) {
        flowContext.getData().getContextData().put("nodeMetaId", getMetaId());
        flowContext.getData().getContextData().put("nodeName", this.getName());
        flowContext.getData().getContextData().put("nodeType", getType().getCode());
        flowContext.getData().getContextData().put("flowTraceIds", new ArrayList<>(flowContext.getTraceId()));
    }

    /**
     * flow中的一些全局公共对象
     *
     * @author 宋永坦
     * @since 2024/3/16
     */
    @Getter
    public static class FlowEnv {
        private final FlowContextRepo<FlowData> repo;

        private final FlowContextMessenger messenger;

        private final FlowLocks locks;

        /**
         * FlowEnv
         *
         * @param repo {@link FlowContextRepo}stream流程上下文repo
         * @param messenger {@link FlowContextMessenger}stream流程事件发送器
         * @param locks 流程锁
         */
        public FlowEnv(FlowContextRepo<FlowData> repo, FlowContextMessenger messenger, FlowLocks locks) {
            this.repo = repo;
            this.messenger = messenger;
            this.locks = locks;
        }
    }

    private String getActualMessage(Throwable throwable) {
        Set<Throwable> visited = new HashSet<>();
        while (throwable != null && !visited.contains(throwable)) {
            visited.add(throwable);
            String message = throwable.getMessage();
            if (StringUtils.isNotBlank(message)) {
                return message;
            }
            throwable = throwable.getCause();
        }
        return null;
    }
}
