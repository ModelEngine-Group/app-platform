/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.WaterFlowService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AppCoordinate;
import modelengine.fit.jober.aipp.service.AppSyncInvokerService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 注册到 store 中应用调用的入口实现.
 *
 * @author 李鑫
 * @since 2024/4/24
 */
@Component
public class WaterFlowInvoke implements WaterFlowService {
    private final AppSyncInvokerService appSyncInvokerService;
    private final ObjectSerializer objectSerializer;
    private final long timeout;

    /**
     * 基于应用同步执行服务的构造方法。
     *
     * @param appSyncInvokerService 表示应用同步执行服务的 {@link AppSyncInvokerService}。
     * @param objectSerializer 表示序列化器的 {@link ObjectSerializer}。
     * @param timeout 表示执行超时时间的 {@code long}，单位秒。
     */
    public WaterFlowInvoke(@Fit AppSyncInvokerService appSyncInvokerService,
            @Fit(alias = "json") ObjectSerializer objectSerializer,
            @Value("${app-engine.app.store.timeout:300}") long timeout) {
        this.appSyncInvokerService = appSyncInvokerService;
        this.objectSerializer = objectSerializer;
        this.timeout = TimeUnit.SECONDS.toMillis(Validation.greaterThan(timeout, 0, "The timeout should > 0."));
    }

    @Override
    @Fitable(id = "water.flow.invoke")
    public String invoke(String tenantId, String aippId, String version, Map<String, Object> inputParams) {
        return this.objectSerializer.serialize(this.appSyncInvokerService.invoke(new AppCoordinate(tenantId,
                        aippId,
                        version),
                this.buildInitContext(inputParams),
                this.timeout,
                this.buildOperationContext(tenantId, inputParams)));
    }

    private OperationContext buildOperationContext(String tenantId, Map<String, Object> inputParams) {
        String userId = ObjectUtils.cast(inputParams.getOrDefault(AippConst.CONTEXT_USER_ID, StringUtils.EMPTY));
        OperationContext context = new OperationContext();
        context.setTenantId(tenantId);
        context.setOperator(userId);
        context.setAccount(userId);
        context.setName(userId);
        context.setOperatorIp(StringUtils.EMPTY);
        context.setSourcePlatform(StringUtils.EMPTY);
        context.setLanguage(StringUtils.EMPTY);
        return context;
    }

    private Map<String, Object> buildInitContext(Map<String, Object> inputParams) {
        return MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, inputParams).build();
    }
}
