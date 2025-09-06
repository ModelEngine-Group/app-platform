/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_CONTEXT;

import modelengine.fit.waterflow.spi.FlowCallbackService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.app.engine.task.convertor.EvalRecordConvertor;
import modelengine.jade.app.engine.task.dto.EvalCaseCreateDto;
import modelengine.jade.app.engine.task.dto.EvalFlowContext;
import modelengine.jade.app.engine.task.dto.EvalRecordCreateDto;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.exception.EvalTaskException;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.service.EvalInstanceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评估流程结束回调节点。
 *
 * @author 何嘉斌
 * @since 2024-08-20
 */
@Component
public class EvalTaskFlowEndCallback implements FlowCallbackService {
    private static final Logger log = Logger.get(EvalTaskFlowEndCallback.class);

    private final ObjectSerializer serializer;
    private final EvalCaseService evalCaseService;
    private final EvalInstanceService evalInstanceService;

    public EvalTaskFlowEndCallback(@Fit(alias = "json") ObjectSerializer serializer,
            @Fit EvalCaseService evalCaseService, @Fit EvalInstanceService evalInstanceService) {
        this.serializer = serializer;
        this.evalCaseService = evalCaseService;
        this.evalInstanceService = evalInstanceService;
    }

    @Fitable("modelengine.jade.app.engine.task.fitable.EvalTaskFlowEndCallback")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        if (contexts.isEmpty()) {
            throw new EvalTaskException(EVAL_TASK_CONTEXT);
        }
        EvalFlowContext<EvalCaseCreateDto> evalFlowContext =
                EvalFlowContext.from(contexts.get(0), EvalCaseCreateDto.class);
        EvalCaseCreateDto evalCaseCreateDto = evalFlowContext.getData();

        if (evalCaseCreateDto.getIsDebug() != null && evalCaseCreateDto.getIsDebug().equals(true)) {
            return;
        }

        String trace = evalFlowContext.getTraceId();
        log.debug("EvalTaskFlowEndCallback businessData {}", evalFlowContext.getData());

        List<EvalRecordCreateDto> recordCreateDto = new ArrayList<>(evalCaseCreateDto.getEvalOutput().values());
        Boolean pass = recordCreateDto.stream().allMatch(EvalRecordCreateDto::getIsPass);

        EvalCaseEntity entity = new EvalCaseEntity();
        entity.setPass(pass);
        entity.setInstanceId(this.evalInstanceService.getEvalInstanceId(trace));
        List<EvalRecordEntity> recordEntities = recordCreateDto.stream().map(dto -> {
            EvalRecordEntity recordEntity = EvalRecordConvertor.INSTANCE.dtoToEntity(dto);
            recordEntity.setInput(serializer.serialize(dto.getInput()));
            return recordEntity;
        }).collect(Collectors.toList());
        this.evalCaseService.createEvalCase(entity, recordEntities);
    }
}