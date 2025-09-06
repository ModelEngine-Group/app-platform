/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.ResumeAippDto;
import modelengine.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.aipp.vo.MetaVo;
import modelengine.fit.runtime.entity.RuntimeData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.flowable.Choir;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import java.util.Map;

/**
 * Aipp运行时管理接口
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "aipp运行时管理接口")
public class AppRunTimeController extends AbstractController {
    private final AippRunTimeService aippRunTimeService;
    private final AippFlowRuntimeInfoService aippFlowRuntimeInfoService;

    /**
     * 构造函数。
     *
     * @param authenticator 认证器。
     * @param aippRunTimeService AIPP运行时服务。
     * @param aippFlowRuntimeInfoService AIPP流程运行时信息服务。
     */
    public AppRunTimeController(Authenticator authenticator, AippRunTimeService aippRunTimeService,
            AippFlowRuntimeInfoService aippFlowRuntimeInfoService) {
        super(authenticator);
        this.aippRunTimeService = aippRunTimeService;
        this.aippFlowRuntimeInfoService = aippFlowRuntimeInfoService;
    }

    /**
     * 用户选择历史后启动流程
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param metaInstId 实例id
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param isDebug 表示是否为调试状态
     * @return 实例id
     */
    @CarverSpan(value = "operation.appRuntime.history")
    @PostMapping(path = "/start/instances/{instance_id}", description = "用户选择历史后启动流程")
    public Choir<Object> startFlowByUserSelectMemory(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("instance_id") @SpanAttr("instance_id") String metaInstId,
            @RequestParam(value = "is_debug") boolean isDebug,
            @Property(description = "initContext表示start表单填充的内容，作为流程初始化的businessData",
                    example = "图片url, 文本输入, prompt") @RequestBody Map<String, Object> initContext) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        return this.aippRunTimeService.startFlowWithUserSelectMemory(metaInstId, initContext, context, isDebug);
    }

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param httpRequest 操作上下文
     * @param resumeAippDto 恢复实例运行的启动参数
     * @param formArgs 用于填充表单的数据
     * @return 返回空回复的 {@link Rsp}{@code <}{@link Void}{@code >}
     */
    @CarverSpan(value = "operation.appRuntime.updateResume")
    @PutMapping(path = "/app/instances/{instance_id}/log/{log_id}", description = "更新表单数据，并恢复实例任务执行")
    public Choir<Object> resumeAndUpdateAippInstance(HttpClassicServerRequest httpRequest,
            @RequestBean ResumeAippDto resumeAippDto,
            @Property(description = "用户填写的表单信息", example = "用户选择的大模型信息") @RequestBody
            Map<String, Object> formArgs) {
        return this.aippRunTimeService.resumeAndUpdateAippInstance(resumeAippDto.getInstanceId(),
                formArgs,
                resumeAippDto.getLogId(),
                this.contextOf(httpRequest, resumeAippDto.getTenantId()),
                resumeAippDto.isDebug());
    }

    /**
     * 终止实例任务
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @return 返回终止信息的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @CarverSpan(value = "operation.appRuntime.terminate")
    @PutMapping(path = "/instances/{instance_id}/terminate", description = "终止实例任务")
    public Rsp<String> terminateAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("instance_id") @SpanAttr("instance_id") String instanceId,
            @RequestBody Map<String, Object> msgArgs) {
        return Rsp.ok(this.aippRunTimeService.terminateInstance(instanceId,
                msgArgs,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 用于表单的终止实例任务
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @param logId logId
     * @return 返回终止信息的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @CarverSpan(value = "operation.appRuntime.terminate.form")
    @PutMapping(path = "/instances/{instance_id}/terminate/log/{log_id}", description = "用于表单的终止实例任务")
    public Rsp<String> terminateAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("instance_id") @SpanAttr("instance_id") String instanceId,
            @RequestBody Map<String, Object> msgArgs, @PathVariable("log_id") Long logId) {
        return Rsp.ok(this.aippRunTimeService.terminateInstance(instanceId, msgArgs, logId,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询流程运行时数据.
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param instanceId 实例id
     * @param version aipp版本
     * @return 返回流程运行时数据的 {@link Rsp}{@code <}{@link RuntimeData}{@code >}
     */
    @GetMapping(value = "/aipp/{aipp_id}/instances/{instance_id}/runtime", description = "查询流程运行时信息")
    public Rsp<RuntimeData> getRuntimeInfo(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @PathVariable("instance_id") String instanceId, @RequestParam(value = "version") String version) {
        OperationContext ctx = this.contextOf(httpRequest, tenantId);
        RuntimeData runtimeData =
                this.aippFlowRuntimeInfoService.getRuntimeData(aippId, version, instanceId, ctx).orElse(null);
        return Rsp.ok(runtimeData);
    }

    /**
     * 根据appid查询对应的aippid和版本号
     *
     * @param httpRequest 请求上下文
     * @param tenantId 租户id
     * @param appId app的id
     * @param isDebug 是否查询debug阶段的aipp信息
     * @return 返回表示meta信息的 {@link Rsp}{@code <}{@link MetaVo}{@code >}
     */
    @GetMapping(value = "/app/{app_id}/aipp", description = "查询app对应的aipp信息")
    public Rsp<MetaVo> queryLatestMetaVoByAppId(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam(value = "isDebug", defaultValue = "false", required = false) Boolean isDebug) {
        OperationContext ctx = this.contextOf(httpRequest, tenantId);
        return Rsp.ok(this.aippRunTimeService.queryLatestMetaVoByAppId(appId, isDebug, ctx));
    }
}
