/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.jade.app.engine.task.convertor.EvalInstanceConvertor;
import modelengine.jade.app.engine.task.dto.EvalInstanceCreateDto;
import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.dto.EvalInstanceUpdateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.app.engine.task.convertor.EvalInstanceConvertor;
import modelengine.jade.app.engine.task.dto.EvalInstanceCreateDto;
import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.dto.EvalInstanceUpdateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

/**
 * 表示评估任务实例管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Component
@RequestMapping(path = "/eval/task/instance", group = "评估实例管理接口")
public class EvalInstanceController {
    private final EvalInstanceService evalInstanceService;

    private final AippRunTimeService aippRunTimeService;

    /**
     * 评估任务实例管理控制器构造函数。
     *
     * @param evalInstanceService 表示评估任务实例服务的 {@link EvalInstanceService}。
     * @param aippRunTimeService 标识评估任务实例启动服务的 {@link AippRunTimeService}。
     */
    public EvalInstanceController(EvalInstanceService evalInstanceService, AippRunTimeService aippRunTimeService) {
        this.evalInstanceService = evalInstanceService;
        this.aippRunTimeService = aippRunTimeService;
    }

    /**
     * 创建评估任务实例。
     *
     * @param createDto 表示评估任务实例创建传输对象的 {@link EvalInstanceCreateDto}。
     */
    @PostMapping(description = "创建评估任务实例")
    public void createEvalInstance(@RequestBody EvalInstanceCreateDto createDto) {
        OperationContext operationContext = this.contextOf(createDto.getTenantId());
        String traceId = this.aippRunTimeService.createLatestAippInstanceByAppId(createDto.getWorkflowId(),
                createDto.getIsDebug(),
                createDto.getInitContext(),
                operationContext);
        this.evalInstanceService.createEvalInstance(createDto.getTaskId(), traceId);
    }

    /**
     * 修改评估任务实例信息。
     *
     * @param updateDto 表示评估任务实例信息传输对象的 {@link EvalInstanceUpdateDto}。
     */
    @PutMapping(description = "修改评估任务实例")
    public void updateEvalInstance(@RequestBody @Validated EvalInstanceUpdateDto updateDto) {
        EvalInstanceEntity entity = EvalInstanceConvertor.INSTANCE.convertDtoToEntity(updateDto);
        this.evalInstanceService.updateEvalInstance(entity);
    }

    /**
     * 分页查询评估任务实例。
     *
     * @param queryParam 表示分页查询评估任务实例参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例查询结果的 {@link PageVo}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    @GetMapping(description = "分页查询评估任务实例")
    public PageVo<EvalInstanceEntity> queryEvalInstance(@RequestBean @Validated EvalInstanceQueryParam queryParam) {
        return this.evalInstanceService.listEvalInstance(queryParam);
    }

    private OperationContext contextOf(String tenantId) {
        UserContext userContext = Validation.notNull(UserContextHolder.get(), "UserContext cannot be null.");
        String name = userContext.getName();
        String ip = userContext.getIp();
        String language = userContext.getLanguage();
        return new OperationContext(tenantId,
                name,
                name,
                name,
                name,
                name,
                ip,
                "",
                language);
    }
}