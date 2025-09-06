/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.utils;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
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
     * 查询指定appId所有已发布的 id 集合
     *
     * @param metaService 使用的{@link MetaService}
     * @param appId 指定app的id
     * @param context 操作人上下文
     * @return 最近更新的未发布草稿{@link Meta}
     */
    public static List<String> getAllPublishedAppId(MetaService metaService, String appId, OperationContext context) {
        OperationContext operationContext = context;
        if (operationContext == null) {
            operationContext = new OperationContext();
            operationContext.setTenantId("31f20efc7e0848deab6a6bc10fc3021e");
        }
        List<Meta> metaList = getAllMetasByAppId(metaService, appId, operationContext);
        if (CollectionUtils.isEmpty(metaList)) {
            return Collections.singletonList(appId);
        }
        MetaFilter metaFilter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("meta_status", Collections.singletonList("active"));
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        metaFilter.setAttributes(attributes);
        metaFilter.setMetaIds(Collections.singletonList(metaList.get(0).getId()));
        List<Meta> publishedMetas = getListMetaHandle(metaService, metaFilter, operationContext);
        return publishedMetas.stream()
                .map(meta -> meta.getAttributes().getOrDefault("app_id", null))
                .filter(Objects::nonNull)
                .map(ObjectUtils::<String>cast)
                .collect(Collectors.toList());
    }

    /**
     * getAllMetasByAppId
     *
     * @param metaService metaService
     * @param appId appId
     * @param context context
     * @return List<Meta>
     */
    public static List<Meta> getAllMetasByAppId(MetaService metaService, String appId, OperationContext context) {
        MetaFilter metaFilter = getMetaFilter(appId);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    private static MetaFilter getMetaFilter(String appId) {
        MetaFilter metaFilter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("app_id", Collections.singletonList(appId));
        metaFilter.setAttributes(attributes);
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        return metaFilter;
    }

    /**
     * 获取MetaList
     *
     * @param metaService 处理Meta请求的service
     * @param metaFilter 过滤器
     * @param context 上下文
     * @return 结果
     */
    public static List<Meta> getListMetaHandle(MetaService metaService, MetaFilter metaFilter,
            OperationContext context) {
        final int limitPerQuery = 10;
        return getAllFromRangedResult(limitPerQuery,
                (offset) -> metaService.list(metaFilter,
                        false,
                        offset,
                        limitPerQuery,
                        context)).collect(Collectors.toList());
    }

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
                        .mapToObj(offsetCount -> CompletableFuture.supplyAsync(() -> resultGetter.apply(
                                offsetCount * limitPerQuery).getResults().stream()))
                        .flatMap(CompletableFuture::join));
    }

    /**
     * 查询指定aippId所有预览{@link Meta}, 按更新时间倒序
     *
     * @param sortKey 见{@link AippSortKeyEnum}, 默认update_at
     * @param direction 排序方向, 见{@link DirectionEnum}, 默认desc
     * @return {@link MetaFilter}中setOrderBys中的字符串
     */
    public static String formatSorter(String sortKey, String direction) {
        return String.format(Locale.ROOT,
                "%s(%s)",
                DirectionEnum.getDirection(nullIf(direction, DirectionEnum.DESCEND.name())).getValue(),
                AippSortKeyEnum.getSortKey(nullIf(sortKey, AippSortKeyEnum.UPDATE_AT.name())).getKey());
    }
}