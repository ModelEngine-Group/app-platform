/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.manager.impl;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATA_INVALID_ERROR;

import modelengine.jade.app.engine.eval.manager.EvalDataValidator;
import modelengine.jade.app.engine.eval.mapper.EvalDatasetMapper;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.schema.SchemaValidator;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataValidator} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
@Component
public class EvalDataValidatorImpl implements EvalDataValidator {
    private static final Logger log = Logger.get(EvalDataValidatorImpl.class);

    private final EvalDatasetMapper datasetMapper;

    private final SchemaValidator schemaValidator;

    public EvalDataValidatorImpl(EvalDatasetMapper datasetMapper, SchemaValidator schemaValidator) {
        this.datasetMapper = datasetMapper;
        this.schemaValidator = schemaValidator;
    }

    @Override
    public void verify(Long datasetId, List<String> contents) {
        String schema = this.datasetMapper.getSchema(datasetId);
        try {
            this.schemaValidator.validate(schema, contents);
        } catch (FitException e) {
            log.error("Verify content error, message: {}", e.getMessage());
            throw new ModelEngineException(DATA_INVALID_ERROR, schema);
        }
    }

    @Override
    public void verify(Long datasetId, String content) {
        this.verify(datasetId, Collections.singletonList(content));
    }
}