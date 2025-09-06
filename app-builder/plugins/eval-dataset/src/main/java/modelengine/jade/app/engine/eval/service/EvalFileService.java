/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service;

import modelengine.jade.app.engine.eval.entity.JsonEntity;

import modelengine.fit.http.entity.ReadableBinaryEntity;

/**
 * 表示评估数据文件解析服务。
 *
 * @author 兰宇晨
 * @since 2024-08-31
 */
public interface EvalFileService {
    /**
     * 表示解析评估数据文件的服务接口。
     *
     * @param file 表示上传的评估数据 Json 文件。
     * @return 表示文件解析内容的 {@link JsonEntity}.
     * @throws modelengine.jade.app.engine.eval.exception.AppEvalDatasetException 当解析评估数据失败时。
     */
    JsonEntity parseJsonFileToEvalData(ReadableBinaryEntity file);
}
