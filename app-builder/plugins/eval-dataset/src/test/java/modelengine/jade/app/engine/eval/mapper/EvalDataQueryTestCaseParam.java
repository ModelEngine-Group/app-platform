/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.mapper;

import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;

import lombok.Data;

/**
 * 表示数据集查询测试用例参数。
 *
 * @author 兰宇晨
 * @since 2024-07-24
 */
@Data
public class EvalDataQueryTestCaseParam {
    private EvalDataQueryParam queryParam;
    private int expectedSize;
    private String expectedContent;
}