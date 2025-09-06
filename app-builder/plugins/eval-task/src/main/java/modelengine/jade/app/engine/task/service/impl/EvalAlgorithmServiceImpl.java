/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import modelengine.jade.app.engine.task.convertor.EvalAlgorithmConvertor;
import modelengine.jade.app.engine.task.entity.EvalAlgorithmEntity;
import modelengine.jade.app.engine.task.mapper.EvalAlgorithmMapper;
import modelengine.jade.app.engine.task.service.EvalAlgorithmService;

import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalAlgorithmService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Component
public class EvalAlgorithmServiceImpl implements EvalAlgorithmService {
    private final EvalAlgorithmMapper evalAlgorithmMapper;

    public EvalAlgorithmServiceImpl(EvalAlgorithmMapper evalAlgorithmMapper) {
        this.evalAlgorithmMapper = evalAlgorithmMapper;
    }

    @Override
    public Boolean exist(String nodeId) {
        return evalAlgorithmMapper.countByNodeId(nodeId) > 0;
    }

    @Override
    public void insert(List<EvalAlgorithmEntity> entity) {
        evalAlgorithmMapper.insert(entity.stream()
                .map(EvalAlgorithmConvertor.INSTANCE::entityToPo)
                .collect(Collectors.toList()));
    }
}