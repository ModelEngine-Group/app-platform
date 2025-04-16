/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static modelengine.fit.waterflow.common.Constant.CONTEXT_DATA;
import static modelengine.fit.waterflow.common.Constant.PASS_DATA;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties.ENTITY;
import static modelengine.fitframework.util.ObjectUtils.cast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;
import modelengine.fit.waterflow.flowsengine.domain.flows.utils.FlowExecuteInfoUtil;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流程定义节点任务关键类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowJober {
    private static final Logger log = Logger.get(FlowJober.class);

    private static final String EXTRA_JOBER = "extraJober";

    private static final String FLOWABLE_HANDLE_TASK_GENERICABLE = "b735c87f5e7e408d852d8440d0b2ecdf";

    private static final String TIMEOUT = "timeout";

    private static final String IS_ASYNC = "isAsync";

    private static final String JOBER_EXECUTE_INFO_TYPE = "jober";

    /**
     * 所在节点的metaId
     */
    protected String nodeMetaId;

    /**
     * jober所在的节点位置
     */
    protected String nodeId;

    /**
     * 节点任务名称
     */
    protected String name;

    /**
     * 节点任务类型
     */
    protected FlowJoberType type;

    /**
     * 节点任务属性，所有任务中定义的变量作为该属性的key
     */
    protected Map<String, String> properties;

    /**
     * 节点任务的fitables集合，不同的jober内置的fitables数量不一致
     */
    protected Set<String> fitables;

    /**
     * 节点任务异常处理fitables集合
     */
    protected Set<String> exceptionFitables;

    /**
     * 调用 fitable客户端
     */
    protected BrokerClient brokerClient;

    /**
     * fitable对应的系统配置和业务参数配置
     */
    protected Map<String, Object> fitablesConfig;

    /**
     * 任务数据转换器
     */
    protected FlowDataConverter converter;

    /**
     * jober归属的流程节点
     */
    protected FlowNode parentNode;

    /**
     * context repo
     */
    protected FlowContextRepo<FlowData> contextRepo;

    /**
     * 是否异步job
     *
     * @return true:异步
     */
    public Boolean isAsync() {
        return Optional.ofNullable(this.properties.get(IS_ASYNC)).map(Boolean::parseBoolean).orElse(false);
    }

    /**
     * 执行任务核心方法
     *
     * @param inputs 流程实现执行时的入参
     * @return 任务执行结果
     */
    public List<FlowData> execute(List<FlowData> inputs) {
        List<FlowData> flowData;
        Map<String, Object> oldValues = new HashMap<>();
        List<FlowData> convertedInputs = this.convertFlowData(inputs);
        if (this.isAsync()) {
            // 异步情况需要提前落库更新入参信息，后续可以将这部分信息修改为无需落库，通过回调实时通知，由上层业务自行处理
            this.contextRepo.updateFlowData(
                    convertedInputs.stream().collect(Collectors.toMap(FlowData::getContextId, Function.identity())));
        }
        try {
            oldValues = this.modifyJoberConfig(convertedInputs.get(0));
            flowData = this.executeJober(convertedInputs);
            if (this.parentNode.getParentFlow().isEnableOutputScope()) {
                flowData.forEach(data -> FlowUtil.cacheResultToNode(data.getBusinessData(), this.nodeMetaId));
            }
            if (this.converter != null) {
                flowData.forEach(data -> {
                    String outputName = this.converter.getOutputName();
                    FlowExecuteInfoUtil.addOutputMap2ExecuteInfoMap(data, MapBuilder.<String, Object>get()
                            .put(outputName, data.getBusinessData().get(outputName))
                            .build(), this.nodeMetaId, JOBER_EXECUTE_INFO_TYPE);
                });
            }
        } finally {
            this.restoreJoberConfig(oldValues);
        }
        return flowData;
    }

    /**
     * modifyJoberConfig
     *
     * @param flowData flowData
     * @return Map<String, Object>
     */
    protected Map<String, Object> modifyJoberConfig(FlowData flowData) {
        Set<String> oldFitables = new LinkedHashSet<>(this.fitables);
        String oldEntity = this.getProperties().get(ENTITY.getValue());

        this.doModifyJoberConfig(flowData);

        Map<String, Object> oldJober = new HashMap<>();
        oldJober.put(FlowGraphData.FITABLES, oldFitables);
        oldJober.put(ENTITY.getValue(), oldEntity);
        return oldJober;
    }

    /**
     * 从FitException获取fitable id
     *
     * @param ex 异常
     * @return fitable id
     */
    protected String getFitableId(FitException ex) {
        return Optional.ofNullable(ex.getProperties())
                .orElse(new HashMap<>()).get("fitableId");
    }

    private void doModifyJoberConfig(FlowData flowData) {
        Optional<JSONObject> jober = Optional.ofNullable(
                        ObjectUtils.<JSONObject>cast(flowData.getBusinessData().get(this.nodeMetaId)))
                .map(json -> cast(json.get(FlowGraphData.JOBER)));
        if (jober.isPresent()) {
            jober.map(joberObject -> ObjectUtils.<JSONArray>cast(joberObject.get(FlowGraphData.FITABLES)))
                    .map(jsonArray -> JSON.parseObject(jsonArray.toJSONString(), new TypeReference<List<String>>() {}))
                    .filter(stringList -> !stringList.isEmpty())
                    .ifPresent(stringList -> this.fitables = new LinkedHashSet<>(stringList));

            jober.map(joberObject -> joberObject.getString(ENTITY.getValue()))
                    .ifPresent(s -> this.properties.put(ENTITY.getValue(), s));
        }
    }

    /**
     * executeJober
     *
     * @param inputs inputs
     * @return List<FlowData>
     */
    protected abstract List<FlowData> executeJober(List<FlowData> inputs);

    /**
     * restoreJoberConfig
     *
     * @param oldValues oldValues
     */
    protected void restoreJoberConfig(Map<String, Object> oldValues) {
        this.setFitables(cast(oldValues.get(FlowGraphData.FITABLES)));
        Optional.ofNullable(oldValues.get(ENTITY.getValue()))
                .ifPresent(value -> this.getProperties().put(ENTITY.getValue(), cast(value)));
    }

    /**
     * getInputs
     *
     * @param inputs inputs
     * @return List<Map < String, Object>>
     */
    protected List<Map<String, Object>> getInputs(List<FlowData> inputs) {
        List<Map<String, Object>> contextData = new ArrayList<>();
        inputs.forEach(input -> {
            Optional.ofNullable(this.properties.get(ENTITY.getValue()))
                    .ifPresent(e -> input.getBusinessData().put(ENTITY.getValue(), JSON.parseObject(e)));

            Map<String, String> extraJober = new HashMap<>();
            Set<String> knownProperties = Arrays.stream(FlowJoberProperties.values())
                    .map(property -> property.getValue())
                    .collect(Collectors.toSet());
            this.properties.keySet()
                    .stream()
                    .filter(k -> !knownProperties.contains(k))
                    .forEach(k -> extraJober.put(k, this.properties.get(k)));

            Map<String, Object> data = new HashMap<>();
            data.put(BUSINESS_DATA_KEY, input.getBusinessData());
            data.put(CONTEXT_DATA, input.getContextData());
            data.put(PASS_DATA, input.getPassData());
            contextData.add(data);
        });
        return contextData;
    }

    /**
     * convertToFlowData
     *
     * @param outputEntities outputEntities
     * @param input input
     * @return List<FlowData>
     */
    protected List<FlowData> convertToFlowData(List<Map<String, Object>> outputEntities, FlowData input) {
        return outputEntities.stream()
                .map(output -> FlowData.builder()
                        .operator(input.getOperator())
                        .startTime(input.getStartTime())
                        .businessData(cast(output.get("businessData")))
                        .contextData(new HashMap<>(input.getContextData()))
                        .passData(cast(output.get("passData")))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * fitableInvoke
     *
     * @param contextData contextData
     * @param fitableId fitableId
     * @return List<Map < String, Object>>
     */
    protected List<Map<String, Object>> fitableInvoke(List<Map<String, Object>> contextData, String fitableId) {
        List<Map<String, Object>> outputEntities;
        Map<String, Object> fitableConf = cast(this.fitablesConfig.get(fitableId));
        if (fitableConf != null && fitableConf.containsKey(TIMEOUT)) {
            outputEntities = this.brokerClient.getRouter(FlowableService.class, FLOWABLE_HANDLE_TASK_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .timeout(cast(fitableConf.get(TIMEOUT)), TimeUnit.MILLISECONDS)
                    .invoke(contextData);
        } else {
            outputEntities = this.brokerClient.getRouter(FlowableService.class, FLOWABLE_HANDLE_TASK_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .communicationType(CommunicationType.ASYNC)
                    .invoke(contextData);
        }
        return outputEntities;
    }

    private List<FlowData> convertFlowData(List<FlowData> inputs) {
        if (Objects.isNull(this.converter)) {
            return inputs;
        }
        return inputs.stream().peek(flowData -> {
            Map<String, Object> newInputMap = this.converter.convertInput(flowData.getBusinessData());
            if (this.nodeMetaId != null) {
                FlowExecuteInfoUtil.addInputMap2ExecuteInfoMap(flowData, newInputMap, this.nodeMetaId,
                        JOBER_EXECUTE_INFO_TYPE);
            }
            flowData.setBusinessData(FlowUtil.mergeMaps(flowData.getBusinessData(), newInputMap));
        }).collect(Collectors.toList());
    }

    /**
     * 将结果加入到FlowData的businessData中
     *
     * @param result 任务执行结果
     * @param businessData 业务数据存放处
     * @param contextInfo 调用的上下文信息
     * @return FlowData
     */
    protected FlowData addResultToFlowData(Object result, Map<String, Object> businessData, FlowData contextInfo) {
        if (Objects.isNull(this.converter)) {
            log.error("There is no converter for adding result, nodeMetaId={}.", this.nodeMetaId);
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flowConverter");
        }
        FlowData flowData = FlowData.builder()
                .operator(contextInfo.getOperator())
                .startTime(contextInfo.getStartTime())
                .businessData(businessData)
                .contextData(new HashMap<>(contextInfo.getContextData()))
                .build();

        Map<String, Object> newOutputMap = this.converter.convertOutput(result);
        if (!newOutputMap.isEmpty()) {
            flowData.getBusinessData().putAll(newOutputMap);
        }
        return flowData;
    }
}
