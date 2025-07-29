/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.utils;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Meta 操作工具类。
 * 由于该工具并未发布为 service，使用直接嵌入插件的方式引用。
 *
 * @author 高嘉乐
 * @since 2025/01/17
 */
public class MetaUtils {
    /**
     * 根据应用唯一标识获取 aippId。
     *
     * @param metaService 表示 meta 服务的 {@link MetaService}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @return 表示 aipp 唯一标识的 {@link String}。
     */
    public static String getAippIdByAppId(MetaService metaService, String appId) {
        OperationContext context = new OperationContext();
        context.setTenantId("31f20efc7e0848deab6a6bc10fc3021e");
        List<Meta> metas = MetaUtils.getAllMetasByAppId(metaService, appId, context);
        if (CollectionUtils.isEmpty(metas)) {
            return appId;
        }
        return metas.get(0).getId();
    }

    private static List<Meta> getListMetaHandle(MetaService metaService, MetaFilter metaFilter,
            OperationContext context) {
        final int limitPerQuery = 10;
        return getAllFromRangedResult(limitPerQuery,
                (offset) -> metaService.list(metaFilter,
                        false,
                        offset,
                        limitPerQuery,
                        context)).collect(Collectors.toList());
    }

    private static List<Meta> getAllMetasByAppId(MetaService metaService, String appId, OperationContext context) {
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

    private static String formatSorter(String sortKey, String direction) {
        return String.format(Locale.ROOT,
                "%s(%s)",
                DirectionEnum.getDirection(nullIf(direction, DirectionEnum.DESCEND.name())).getValue(),
                AippSortKeyEnum.getSortKey(nullIf(sortKey, AippSortKeyEnum.UPDATE_AT.name())).getKey());
    }

    private static <T> Stream<T> getAllFromRangedResult(int limitPerQuery,
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
}