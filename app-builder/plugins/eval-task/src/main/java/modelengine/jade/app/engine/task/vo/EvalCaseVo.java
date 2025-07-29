/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.vo;

import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;

import lombok.Data;

import java.util.List;

/**
 * 表示评估用例展示对象。
 *
 * @author 何嘉斌
 * @since 2024-09-23
 */
@Data
public class EvalCaseVo {
    /**
     * 评估用例记录实体。
     */
    EvalCaseEntity evalCaseEntity;

    /**
     * 评估算法评估结果实体。
     */
    List<EvalRecordEntity> evalRecordEntities;
}