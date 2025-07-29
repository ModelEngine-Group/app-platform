/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据上报配置类。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
public class RecordConfig {
    /**
     * 延迟分布桶（[0-4000], [4001-8000], [8001, 12000], [12001, inf]） 当前设计只能包含四个桶。
     * <p>由于三方包原因，这里实际使用 {@link List}{@code <}{@link Double}{@code >} 进行实现。</p>
     */
    public static final List<Double> EXPLICIT_BUCKETS =
            Collections.unmodifiableList(Arrays.asList(4000.0, 8000.0, 12000.0));
}