/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import modelengine.jade.app.engine.task.dto.EvalRecordQueryParam;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.service.EvalRecordService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;

/**
 * 表示评估任务用例结果管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-08-31
 */
@Component
@RequestMapping(path = "/eval/task/record", group = "评估任务用例结果管理接口")
public class EvalRecordController {
    private final EvalRecordService evalRecordService;

    /**
     * 评估任务用例结果管理控制器构造函数。
     *
     * @param evalRecordService 表示评估任务用例结果服务的 {@link EvalRecordService}。
     */
    public EvalRecordController(EvalRecordService evalRecordService) {
        this.evalRecordService = evalRecordService;
    }

    /**
     * 分页查询评估任务用例结果。
     *
     * @param queryParam 表示分页查询评估任务用例结果参数的 {@link EvalRecordQueryParam}。
     * @return 表示评估任务用例结果查询结果的 {@link PageVo}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    @GetMapping(description = "分页查询评估任务用例结果")
    public PageVo<EvalRecordEntity> queryEvalRecord(@RequestBean @Validated EvalRecordQueryParam queryParam) {
        return this.evalRecordService.listEvalRecord(queryParam);
    }
}