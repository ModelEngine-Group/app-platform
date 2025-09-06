/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.controller;

import modelengine.jade.app.engine.eval.entity.JsonEntity;
import modelengine.jade.app.engine.eval.service.EvalFileService;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fitframework.annotation.Component;

/**
 * 表示评估数据文件解析接口集。
 *
 * @author 兰宇晨
 * @since 2024-8-31
 */
@Component
@RequestMapping(path = "/eval/file", group = "评估数据文件解析接口")
public class EvalFileController {
    private final EvalFileService evalFileService;

    /**
     * 表示评估数据文件解析接口集构造函数。
     *
     * @param evalFileService 表示文件解析服务的 {@link EvalFileService}。
     */
    public EvalFileController(EvalFileService evalFileService) {
        this.evalFileService = evalFileService;
    }

    /**
     * 表示评估数据文件解析接口。
     *
     * @param file 表示评估数据文件实体的 {@link ReadableBinaryEntity}。
     * @return 表示评估数据文件解析结果的 {@link JsonEntity}。
     */
    @PostMapping(description = "上传文件并解析为字符串")
    public JsonEntity parseJsonFile(ReadableBinaryEntity file) {
        return evalFileService.parseJsonFileToEvalData(file);
    }
}
