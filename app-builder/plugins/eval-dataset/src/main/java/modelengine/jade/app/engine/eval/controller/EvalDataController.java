/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.controller;

import modelengine.jade.app.engine.eval.dto.EvalDataCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDataUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDataDeleteParam;
import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.jade.app.engine.eval.service.EvalListDataService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;

import javax.validation.Valid;

/**
 * 表示评估数据管理接口集。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Component
@Validated
@RequestMapping(path = "/eval/data", group = "评估数据管理接口")
public class EvalDataController {
    private final EvalDataService evalDataService;

    private final EvalListDataService evalListDataService;

    /**
     * 评估数据管理控制器构造函数。
     *
     * @param evalDataService 表示评估数据服务的 {@link EvalDataService}。
     * @param evalListDataService 表示评估数据查询服务的 {@link EvalListDataService}
     */
    public EvalDataController(EvalDataService evalDataService, EvalListDataService evalListDataService) {
        this.evalDataService = evalDataService;
        this.evalListDataService = evalListDataService;
    }

    /**
     * 批量创建评估数据。
     *
     * @param createDto 表示评估数据创建传输对象的 {@link EvalDataCreateDto}。
     */
    @PostMapping(description = "批量创建评估数据")
    public void createEvalData(@RequestBody @Valid EvalDataCreateDto createDto) {
        this.evalDataService.insertAll(createDto.getDatasetId(), createDto.getContents());
    }

    /**
     * 查询评估数据。
     *
     * @param queryParam 表示查询参数的 {@link EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@link PageVo}{@code <}{@link EvalDataEntity}{@code >}。
     */
    @GetMapping(description = "查询评估数据")
    public PageVo<EvalDataEntity> queryEvalData(@RequestBean @Valid EvalDataQueryParam queryParam) {
        return evalListDataService.listEvalData(queryParam);
    }

    /**
     * 批量删除评估数据。
     *
     * @param deleteParam 表示评估数据删除传输对象的 {@link EvalDataDeleteParam}。
     */
    @DeleteMapping(description = "批量删除评估数据")
    public void deleteEvalData(@RequestBean @Valid EvalDataDeleteParam deleteParam) {
        this.evalDataService.delete(deleteParam.getDataIds());
    }

    /**
     * 修改评估数据。
     *
     * @param updateDto 表示评估数据修改传输对象的 {@link EvalDataUpdateDto}。
     */
    @PutMapping(description = "修改评估数据")
    public void updateEvalData(@RequestBody @Valid EvalDataUpdateDto updateDto) {
        this.evalDataService.update(updateDto.getDatasetId(), updateDto.getDataId(), updateDto.getContent());
    }
}