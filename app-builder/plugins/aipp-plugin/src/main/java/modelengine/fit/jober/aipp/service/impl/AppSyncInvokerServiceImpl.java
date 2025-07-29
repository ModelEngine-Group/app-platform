/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AppIdentifier;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.fit.jober.aipp.service.AppSyncInvokerService;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.FlowDataConstant;
import modelengine.fit.jober.util.FlowDataUtils;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.spi.FlowCallbackService;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.exception.TimeoutException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 应用同步执行服务的实现。
 *
 * @author 宋永坦
 * @since 2025-07-23
 */
@Component
public class AppSyncInvokerServiceImpl implements FlowCallbackService, FlowExceptionService, AppSyncInvokerService {
    private static final Logger LOGGER = Logger.get(AppSyncInvokerServiceImpl.class);
    private static final String INVOKER_CALLBACK_FITABLE_ID = "modelengine.fit.aipp.sync.invoker";
    private static final String APP_REQUEST_ID = "appRequestId";

    private final AippRunTimeService aippRunTimeService;

    /**
     * 记录调用未完成的请求。其中 key 为请求标识。
     */
    private final Map<String, Request> requests = new ConcurrentHashMap<>();

    /**
     * 基于应用运行服务对象的构造方法。
     *
     * @param aippRunTimeService 表示应用运行服务的 {@link AippRunTimeService}。
     */
    public AppSyncInvokerServiceImpl(@Fit AippRunTimeService aippRunTimeService) {
        this.aippRunTimeService = aippRunTimeService;
    }

    @Override
    public Object invoke(AppIdentifier appIdentifier, Map<String, Object> initContext, long timeout,
            OperationContext operationContext) {
        String requestId = UUIDUtil.uuid();
        Request request = new Request(this.aippRunTimeService, appIdentifier,
                this.addDynamicParams(requestId, initContext),
                operationContext);
        this.requests.put(requestId, request);
        try {
            request.post();
            return request.await(timeout);
        } finally {
            this.requests.remove(requestId);
        }
    }

    @Fitable(INVOKER_CALLBACK_FITABLE_ID)
    @Override
    public void callback(List<Map<String, Object>> flowDataList) {
        Validation.isTrue(flowDataList.size() == 1, "The callback data size is not 1.");
        Map<String, Object> businessData = FlowDataUtils.getBusinessData(flowDataList.get(0));
        Map<String, Object> contextData = FlowDataUtils.getContextData(flowDataList.get(0));
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String requestId = ObjectUtils.cast(businessData.get(APP_REQUEST_ID));
        LOGGER.info("The app request is finished, callId={}, instanceId={}, parentInstanceId={}",
                requestId,
                instanceId,
                parentInstanceId);
        Request request = this.requests.get(requestId);
        if (request == null) {
            LOGGER.error("Can not find the request, requestId={}, instanceId={}, parentInstanceId={}",
                    requestId,
                    instanceId,
                    parentInstanceId);
            return;
        }
        List<Map<String, Object>> executeInfo =
                FlowDataUtils.getExecuteInfo(businessData, FlowDataUtils.getNodeId(contextData));
        if (CollectionUtils.isEmpty(executeInfo)) {
            LOGGER.error("Can not find the response. requestId={}, instanceId={}, parentInstanceId={}",
                    requestId,
                    instanceId,
                    parentInstanceId);
            FlowErrorInfo errorInfo = new FlowErrorInfo();
            errorInfo.setErrorCode(ErrorCodes.UN_EXCEPTED_ERROR.getErrorCode());
            errorInfo.setErrorMessage("No response");
            request.setError(errorInfo);
            return;
        }
        // 结束节点上的入参为最终出参
        request.setResponse(executeInfo.get(executeInfo.size() - 1).get(FlowDataConstant.EXECUTE_INPUT_KEY));
    }

    @Fitable(INVOKER_CALLBACK_FITABLE_ID)
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> flowDataList, FlowErrorInfo errorInfo) {
        Validation.isTrue(flowDataList.size() == 1, "The exception data size is not 1.");
        Map<String, Object> businessData = FlowDataUtils.getBusinessData(flowDataList.get(0));
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String requestId = ObjectUtils.cast(businessData.get(APP_REQUEST_ID));
        LOGGER.info("The app request has error, requestId={}, instanceId={}, parentInstanceId={}, error={}",
                requestId,
                instanceId,
                parentInstanceId,
                errorInfo.getErrorMessage());
        Request request = this.requests.get(requestId);
        if (request == null) {
            LOGGER.error("Can not find the request, requestId={}, instanceId={}, parentInstanceId={}",
                    requestId,
                    instanceId,
                    parentInstanceId);
            return;
        }
        request.setError(errorInfo);
    }

    private Map<String, Object> addDynamicParams(String requestId, Map<String, Object> initContext) {
        Map<String, Object> inputParams = ObjectUtils.cast(initContext.get(AippConst.BS_INIT_CONTEXT_KEY));
        inputParams.put(APP_REQUEST_ID, requestId);
        inputParams.put(AippConst.PARENT_CALLBACK_ID, INVOKER_CALLBACK_FITABLE_ID);
        inputParams.put(AippConst.PARENT_EXCEPTION_FITABLE_ID, INVOKER_CALLBACK_FITABLE_ID);
        return initContext;
    }

    /**
     * 调用应用的请求的封装。
     *
     * @author 宋永坦
     * @since 2025-07-23
     */
    private class Request {
        private final AippRunTimeService aippRunTimeService;
        private final AppIdentifier appIdentifier;
        private final Map<String, Object> initContext;
        private final OperationContext operationContext;
        private final CountDownLatch countDownLatch = new CountDownLatch(1);

        private Object response = null;
        private FlowErrorInfo error = null;

        /**
         * 绑定调用信息的构造方法。
         *
         * @param aippRunTimeService 表示应用运行服务的 {@link AippRunTimeService}。
         * @param appIdentifier 表表示应用标识的 {@link AppIdentifier}。
         * @param initContext 表示应用启动上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @param operationContext 表示操作上下文的 {@link OperationContext}。
         */
        public Request(AippRunTimeService aippRunTimeService, AppIdentifier appIdentifier,
                Map<String, Object> initContext, OperationContext operationContext) {
            this.aippRunTimeService = aippRunTimeService;
            this.appIdentifier = appIdentifier;
            this.initContext = initContext;
            this.operationContext = operationContext;
        }

        /**
         * 发起请求。
         */
        public void post() {
            this.aippRunTimeService.createAippInstance(this.appIdentifier.getAippId(),
                    this.appIdentifier.getVersion(),
                    initContext,
                    this.operationContext);
        }

        /**
         * 等待结果。
         *
         * @param timeoutMs 表示最大等待时间的 {@code long}。
         * @return 执行结果。
         */
        public Object await(long timeoutMs) {
            boolean isWaited;
            try {
                isWaited = this.countDownLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
            if (!isWaited) {
                throw new TimeoutException(StringUtils.format("Invocation timeout. [aippId={0}, version={1}]",
                        this.appIdentifier.getAippId(),
                        this.appIdentifier.getVersion()));
            }
            if (this.error != null) {
                throw new FitException(this.error.getErrorCode(), this.error.getErrorMessage());
            }
            return this.response;
        }

        /**
         * 设置执行结果。如果应用执行完成，通过该接口完成请求，表示请求获取到结果。
         *
         * @param response 表示应用执行结果。
         */
        public void setResponse(Object response) {
            this.response = response;
            this.countDownLatch.countDown();
        }

        /**
         * 设置异常信息。如果应用执行异常，通过该接口完成请求，表示请求异常。
         *
         * @param errorInfo 表示应用执行异常的异常对象。
         */
        public void setError(FlowErrorInfo errorInfo) {
            this.error = errorInfo;
            this.countDownLatch.countDown();
        }
    }
}
