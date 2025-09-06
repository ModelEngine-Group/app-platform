/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service.impl;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATA_DELETED_ERROR;
import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.USER_CONTEXT_NOT_FOUND;

import modelengine.jade.app.engine.eval.manager.EvalDataValidator;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.eval.po.EvalDataPo;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.exception.ModelEngineException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalDataService} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Component
public class EvalDataServiceImpl implements EvalDataService {
    private final EvalDataMapper dataMapper;
    private final EvalDataValidator dataValidator;
    private final EvalDatasetVersionManager versionManager;

    /**
     * 表示评估数据服务实现的构建器。
     *
     * @param dataMapper 表示评估数据持久层接口的 {@link EvalDataMapper}。
     * @param dataValidator 评估数据校验器的 {@link EvalDataValidator}。
     * @param versionManager 表示评估数据集版本管理器的 {@link EvalDatasetVersionManager}。
     */
    public EvalDataServiceImpl(EvalDataMapper dataMapper, EvalDataValidator dataValidator,
            EvalDatasetVersionManager versionManager) {
        this.dataMapper = dataMapper;
        this.dataValidator = dataValidator;
        this.versionManager = versionManager;
    }

    @Override
    public void insertAll(Long datasetId, List<String> contents) {
        this.dataValidator.verify(datasetId, contents);
        long version = this.versionManager.applyVersion();
        insert(datasetId, contents, version);
    }

    @Override
    public void delete(List<Long> dataIds) {
        long version = this.versionManager.applyVersion();
        softDelete(dataIds, version);
    }

    @Override
    @Transactional
    public void update(Long datasetId, Long dataId, String content) {
        this.dataValidator.verify(datasetId, content);
        long version = this.versionManager.applyVersion();
        int effectRows = softDelete(Collections.singletonList(dataId), version);
        if (effectRows == 0) {
            throw new ModelEngineException(DATA_DELETED_ERROR, dataId);
        }
        insert(datasetId, Collections.singletonList(content), version);
    }

    @Override
    public void hardDelete(List<Long> datasetIds) {
        this.dataMapper.deleteAll(datasetIds);
    }

    private void insert(Long datasetId, List<String> contents, long createdVersion) {
        List<EvalDataPo> evalDataPoList = contents.stream().map(content -> {
            EvalDataPo evalDataPo = new EvalDataPo();
            evalDataPo.setContent(content);
            evalDataPo.setCreatedVersion(createdVersion);
            evalDataPo.setDatasetId(datasetId);
            return evalDataPo;
        }).collect(Collectors.toList());
        this.dataMapper.insertAll(evalDataPoList);
    }

    private int softDelete(List<Long> dataIds, long expiredVersion) {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            throw new ModelEngineException(USER_CONTEXT_NOT_FOUND);
        }
        return this.dataMapper.updateExpiredVersion(dataIds,
                expiredVersion,
                LocalDateTime.now(),
                userContext.getName());
    }
}