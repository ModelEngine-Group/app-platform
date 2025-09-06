/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service.impl;

import modelengine.jade.app.engine.eval.convertor.EvalDatasetConvertor;
import modelengine.jade.app.engine.eval.entity.EvalDatasetEntity;
import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDatasetMapper;
import modelengine.jade.app.engine.eval.po.EvalDatasetPo;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.jade.app.engine.eval.service.EvalDatasetService;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDatasetService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@Component
public class EvalDatasetServiceImpl implements EvalDatasetService {
    private final EvalDatasetMapper datasetMapper;
    private final EvalDataService dataService;
    private final EvalDatasetVersionManager versionManager;

    /**
     * 表示评估数据集服务实现的构建器。
     *
     * @param datasetMapper 表示评估数据集持久层接口的 {@link EvalDatasetMapper}。
     * @param dataService 表示评估数据集评估数据服务的 {@link EvalDataService}。
     * @param versionManager 表示评估数据集版本服务的 {@link EvalDatasetVersionManager}。
     */
    public EvalDatasetServiceImpl(EvalDatasetMapper datasetMapper, EvalDataService dataService,
            EvalDatasetVersionManager versionManager) {
        this.datasetMapper = datasetMapper;
        this.dataService = dataService;
        this.versionManager = versionManager;
    }

    @Override
    @Transactional
    public void create(EvalDatasetEntity entity) {
        EvalDatasetPo evalDatasetPo = EvalDatasetConvertor.INSTANCE.entityToPo(entity);
        this.datasetMapper.create(evalDatasetPo);
        this.dataService.insertAll(evalDatasetPo.getId(), entity.getContents());
    }

    @Override
    @Transactional
    public void delete(List<Long> datasetIds) {
        this.dataService.hardDelete(datasetIds);
        this.datasetMapper.delete(datasetIds);
    }

    @Override
    @Transactional
    public PageVo<EvalDatasetVo> listEvalDataset(EvalDatasetQueryParam queryParam) {
        List<EvalDatasetVo> evalDataset = this.datasetMapper.listEvalDataset(queryParam);
        int evalDatasetCount = this.datasetMapper.countEvalDataset(queryParam);
        return PageVo.of(evalDatasetCount, evalDataset);
    }

    @Override
    @Transactional
    public EvalDatasetVo getEvalDatasetById(Long datasetId) {
        EvalDatasetVo vo = this.datasetMapper.getEvalDatasetById(datasetId);
        vo.setVersions(Collections.singletonList(this.versionManager.getLatestVersion(datasetId)));
        return vo;
    }

    @Override
    public void updateEvalDataset(EvalDatasetEntity updateEntity) {
        EvalDatasetPo evalDatasetPo = EvalDatasetConvertor.INSTANCE.entityToPo(updateEntity);
        this.datasetMapper.updateEvaldataset(evalDatasetPo);
    }
}