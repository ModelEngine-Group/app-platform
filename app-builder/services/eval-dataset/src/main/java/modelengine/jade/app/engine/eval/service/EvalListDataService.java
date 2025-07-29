/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.common.vo.PageVo;

/**
 * 查询评估数据的服务。
 *
 * @author 兰宇晨
 * @since 2024-8-26
 */
public interface EvalListDataService {
    /**
     * 分页查询数据。
     *
     * @param queryParam 表示查询相关参数的 {@link EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@link PageVo}{@code <}{@link EvalDataEntity}{@code >}。
     */
    @Genericable(id = "modelengine.jade.app.engine.eval.service.EvalListDataService.listEvalData")
    PageVo<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam);
}
