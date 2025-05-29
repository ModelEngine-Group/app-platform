/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.common.RangedResultSet;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Meta操作工具类
 *
 * @author 刘信宏
 * @since 2024/02/21
 */
public class MetaUtils {
    /**
     * 从rangeResult实例中获取查询结果
     *
     * @param limitPerQuery 表示查询数量限制的{@link int}
     * @param resultGetter 表示返回查询的方法的{@link Function}
     * @param <T> 带查询数据的类型
     * @return 表示查询结果的流
     */
    public static <T> Stream<T> getAllFromRangedResult(int limitPerQuery,
            Function<Long, RangedResultSet<T>> resultGetter) {
        RangedResultSet<T> metaRes = resultGetter.apply(0L);
        if (metaRes.getResults().isEmpty() || metaRes.getRange().getTotal() == 0) {
            return Stream.empty();
        }
        List<T> firstResult = metaRes.getResults();
        if (metaRes.getRange().getTotal() <= limitPerQuery) {
            return firstResult.stream();
        }
        return Stream.concat(firstResult.stream(),
                LongStream.rangeClosed(1, (int) (metaRes.getRange().getTotal() / limitPerQuery))
                        .mapToObj(offsetCount -> CompletableFuture.supplyAsync(
                                () -> resultGetter.apply(offsetCount * limitPerQuery).getResults().stream()))
                        .flatMap(CompletableFuture::join));
    }
}