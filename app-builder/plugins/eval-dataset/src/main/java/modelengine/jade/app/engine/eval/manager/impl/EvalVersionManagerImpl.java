/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.manager.impl;

import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.uid.UidGenerator;

import modelengine.fitframework.annotation.Component;

/**
 * 表示 {@link EvalDatasetVersionManager} 的默认实现。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-07-20
 */
@Component
public class EvalVersionManagerImpl implements EvalDatasetVersionManager {
    private final UidGenerator versionGenerator;
    private final EvalDataMapper dataMapper;

    /**
     * 表示评估数据集版本管理器实现的构建器。
     *
     * @param dataMapper 表示评估数据持久层接口的 {@link EvalDataMapper}。
     * @param generator 表示版本生成器持久层接口的 {@link UidGenerator}。
     */
    public EvalVersionManagerImpl(EvalDataMapper dataMapper, UidGenerator generator) {
        this.dataMapper = dataMapper;
        this.versionGenerator = generator;
    }

    @Override
    public long applyVersion() {
        return this.versionGenerator.getUid();
    }

    @Override
    public EvalVersionEntity getLatestVersion(Long datasetId) {
        return this.dataMapper.getLatestVersion(datasetId);
    }
}