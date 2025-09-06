/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.controller;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATASET_DELETION_ERROR;

import modelengine.jade.app.engine.eval.convertor.EvalDatasetConvertor;
import modelengine.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDatasetDeleteParam;
import modelengine.jade.app.engine.eval.entity.EvalDatasetEntity;
import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.service.EvalDatasetService;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.vo.PageVo;

import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * 表示评估数据集管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@Component
@Validated
@RequestMapping(path = "/eval/dataset", group = "评估数据集管理接口")
public class EvalDatasetController {
    private final EvalDatasetService evaldatasetService;

    /**
     * 评估数据集管理控制器构造函数。
     *
     * @param evalDatasetService 表示评估数据服务的 {@link EvalDatasetService}。
     */
    public EvalDatasetController(EvalDatasetService evalDatasetService) {
        this.evaldatasetService = evalDatasetService;
    }

    /**
     * 创建评估数据集。
     *
     * @param createDto 表示评估数据集创建传输对象的 {@link EvalDatasetCreateDto}。
     */
    @PostMapping(description = "创建评估数据集")
    public void createEvalDataset(@RequestBody @Valid EvalDatasetCreateDto createDto) {
        EvalDatasetEntity entity = EvalDatasetConvertor.INSTANCE.convertDtoToEntity(createDto);
        this.evaldatasetService.create(entity);
    }

    /**
     * 批量删除评估数据集。
     *
     * @param deleteParam 表示评估数据集的批量删除条件的 {@link EvalDatasetDeleteParam}。
     */
    @DeleteMapping(description = "批量删除评估数据集")
    public void deleteEvalDataset(@RequestBean @Valid EvalDatasetDeleteParam deleteParam) {
        try {
            this.evaldatasetService.delete(deleteParam.getDatasetIds());
        } catch (DataAccessException exception) {
            throw new ModelEngineException(DATASET_DELETION_ERROR,
                    exception,
                    deleteParam.getDatasetIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
    }

    /**
     * 查询评估数据集元数据。
     *
     * @param queryParam 表示评估数据集查询传输对象的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集查询结果的 {@link PageVo}{@code <}{@link EvalDatasetEntity}{@code >}。
     */
    @GetMapping(description = "查询评估数据集元数据")
    public PageVo<EvalDatasetVo> queryEvalDataset(@RequestBean @Valid EvalDatasetQueryParam queryParam) {
        return this.evaldatasetService.listEvalDataset(queryParam);
    }

    /**
     * 通过唯一标识查询评估数据集元数据。
     *
     * @param datasetId 表示评估数据集传输对象的 {@link Long}。
     * @return 表示评估数集据查询结果的 {@link EvalDatasetEntity}。
     */
    @GetMapping(path = "/{id}", description = "通过唯一标识查询评估数据集元数据")
    public EvalDatasetVo queryEvalDatasetById(
            @Valid @PathVariable("id") @Positive(message = "Min dataset ID is 1") Long datasetId) {
        return this.evaldatasetService.getEvalDatasetById(datasetId);
    }

    /**
     * 修改评估数据集信息。
     *
     * @param updateDto 表示评估数据集信息传输对象的 {@link EvalDatasetUpdateDto}。
     */
    @PutMapping(description = "修改评估数据集信息")
    public void updateEvalDataset(@RequestBody @Valid EvalDatasetUpdateDto updateDto) {
        EvalDatasetEntity entity = EvalDatasetConvertor.INSTANCE.convertDtoToEntity(updateDto);
        this.evaldatasetService.updateEvalDataset(entity);
    }
}