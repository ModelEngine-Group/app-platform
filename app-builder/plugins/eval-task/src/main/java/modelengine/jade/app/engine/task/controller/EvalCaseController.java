/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import modelengine.jade.app.engine.task.dto.EvalCaseQueryParam;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.vo.EvalCaseVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;

/**
 * 表示评估任务用例管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-09-23
 */
@Component
@RequestMapping(path = "/eval/task/case", group = "评估任务用例管理接口")
public class EvalCaseController {
    private final EvalCaseService evalCaseService;

    /**
     * 评估任务用例管理控制器构造函数。
     *
     * @param evalCaseService 表示评估任务报告服务的 {@link EvalCaseService}。
     */
    public EvalCaseController(EvalCaseService evalCaseService) {
        this.evalCaseService = evalCaseService;
    }

    /**
     * 查询评估评估任务用例元数据。
     *
     * @param queryParam 表示评估任务用例查询传输对象的 {@link EvalCaseQueryParam}。
     * @return 表示评估任务用例查询结果的 {@link PageVo}{@code <}{@link EvalCaseVo}{@code >}。
     */
    @GetMapping(description = "查询评估数据报告元数据")
    public PageVo<EvalCaseVo> queryEvalCase(@RequestBean @Validated EvalCaseQueryParam queryParam) {
        return this.evalCaseService.listEvalCase(queryParam);
    }
}