/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import modelengine.jade.app.engine.task.convertor.EvalTaskConvertor;
import modelengine.jade.app.engine.task.dto.EvalTaskCreateDto;
import modelengine.jade.app.engine.task.dto.EvalTaskDeleteParam;
import modelengine.jade.app.engine.task.dto.EvalTaskQueryParam;
import modelengine.jade.app.engine.task.entity.EvalTaskEntity;
import modelengine.jade.app.engine.task.service.EvalTaskService;
import modelengine.jade.app.engine.task.vo.EvalTaskVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;

/**
 * 表示评估任务管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Component
@RequestMapping(path = "/eval/task", group = "评估任务管理接口")
public class EvalTaskController {
    private final EvalTaskService evalTaskService;

    /**
     * 评估任务管理控制器构造函数。
     *
     * @param evalTaskService 表示评估任务服务的 {@link EvalTaskService}。
     */
    public EvalTaskController(EvalTaskService evalTaskService) {
        this.evalTaskService = evalTaskService;
    }

    /**
     * 创建评估任务。
     *
     * @param createDto 表示评估任务创建传输对象的 {@link EvalTaskCreateDto}。
     */
    @PostMapping(description = "创建评估任务")
    public void createEvalTask(@RequestBody @Validated EvalTaskCreateDto createDto) {
        EvalTaskEntity entity = EvalTaskConvertor.INSTANCE.convertDtoToEntity(createDto);
        this.evalTaskService.createEvalTask(entity);
    }

    /**
     * 查询评估任务元数据。
     *
     * @param queryParam 表示评估任务创建传输对象的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务查询结果的 {@link PageVo}{@code <}{@link EvalTaskEntity}{@code >}。
     */
    @GetMapping(description = "查询评估任务元数据")
    public PageVo<EvalTaskVo> queryEvalTask(@RequestBean @Validated EvalTaskQueryParam queryParam) {
        return this.evalTaskService.listEvalTask(queryParam);
    }

    /**
     * 批量删除评估任务。
     *
     * @param deleteParam 表示评估任务删除参数的 {@link EvalTaskDeleteParam}。
     */
    @DeleteMapping(description = "批量删除评估任务")
    public void deleteEvalData(@RequestBean @Validated EvalTaskDeleteParam deleteParam) {
        this.evalTaskService.deleteEvalTask(deleteParam.getTaskIds());
    }
}