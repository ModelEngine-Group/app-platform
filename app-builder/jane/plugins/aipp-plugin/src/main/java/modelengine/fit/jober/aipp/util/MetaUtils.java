/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippNotFoundException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.enums.AippSortKeyEnum;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
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
    private static final Logger log = Logger.get(MetaUtils.class);

    /**
     * 查询最近更新的任意{@link Meta}
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param version 指定aipp的版本
     * @param context 操作人上下文
     * @return 最近更新的{@link Meta}
     * @throws AippNotFoundException 没有找到
     */
    public static Meta getAnyMeta(MetaService metaService, String metaId, String version, OperationContext context)
            throws AippNotFoundException {
        MetaFilter metaFilter = getAnyMetaFilter(metaId, version);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId最新的非预览{@link Meta}(包括发布和未发布的)
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 最近更新的非预览{@link Meta}
     * @throws AippException 没有找到
     */
    public static Meta getLastNormalMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.DEFAULT);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId最新的已发布{@link Meta}
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 最近更新的已发布{@link Meta}
     * @throws AippException 没有找到
     */
    public static Meta getLastPublishedMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.PUBLISHED);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId最新的未发布草稿{@link Meta}
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 最近更新的未发布草稿{@link Meta}
     * @throws AippException 没有找到
     */
    public static Meta getLastDraftMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.DRAFT);
        return getSingleMetaHandle(metaService, metaId, metaFilter, context);
    }

    /**
     * 查询指定aippId所有发布的 {@link Meta}, 按更新时间倒序
     *
     * @param metaService 使用的{@link MetaService}
     * @param metaId 指定aipp的id
     * @param context 操作人上下文
     * @return 所有非预览{@link Meta}
     * @throws AippException 没有找到
     */
    public static List<Meta> getAllPublishedMeta(MetaService metaService, String metaId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.PUBLISHED);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    /**
     * 分页查询指定应用的已发布meta列表，按更新时间倒序。
     *
     * @param metaService 表示使用的 {@link MetaService}。
     * @param metaId 表示指定应用id的 {@link String}。
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示单页最大数量的 {@code int}。
     * @param context 表示操作人上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Meta}{@code >}。
     */
    public static RangedResultSet<Meta> getPublishedMetaByPage(MetaService metaService, String metaId, long offset,
            int limit, OperationContext context) {
        MetaFilter metaFilter = getNormalMetaFilter(metaId, NormalFilterEnum.PUBLISHED);
        metaFilter.setOrderBys(Collections.singletonList(formatSorter(AippSortKeyEnum.UPDATE_AT.name(),
                DirectionEnum.DESCEND.name())));
        return metaService.list(metaFilter, false, offset, limit, context);
    }

    /**
     * 查询指定aippId所有预览{@link Meta}, 按更新时间倒序
     *
     * @param metaService 使用的{@link MetaService}
     * @param baselineAippId 指定aipp的id
     * @param context 操作人上下文
     * @return 所有预览{@link Meta}
     * @throws AippException 没有找到
     */
    public static List<Meta> getAllPreviewMeta(MetaService metaService, String baselineAippId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getPreviewMetaFilter(baselineAippId);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    private static Meta getSingleMetaHandle(MetaService metaService, String metaId, MetaFilter metaFilter,
            OperationContext context) throws AippException {
        Validation.notBlank(metaId, () -> new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, metaId));
        RangedResultSet<Meta> metaRes = metaService.list(metaFilter, true, 0, 1, context);
        if (metaRes.getResults().isEmpty()) {
            log.warn("meta {} version {} meta status {} not found",
                    metaId,
                    metaFilter.getVersions(),
                    metaFilter.getAttributes().get(AippConst.ATTR_META_STATUS_KEY));
            return null;
        }
        return metaRes.getResults().get(0);
    }

    /**
     * 获取MetaList
     *
     * @param metaService 处理Meta请求的service
     * @param metaFilter 过滤器
     * @param context 上下文
     * @return 结果
     * @throws AippException 抛出aipp的异常
     */
    public static List<Meta> getListMetaHandle(MetaService metaService, MetaFilter metaFilter, OperationContext context)
            throws AippException {
        final int limitPerQuery = 10;
        return getAllFromRangedResult(limitPerQuery,
                (offset) -> metaService.list(metaFilter,
                        false,
                        offset,
                        limitPerQuery,
                        context)).collect(Collectors.toList());
    }

    private static Map<String, List<String>> deepCopy(Map<String, List<String>> originalMap) {
        Map<String, List<String>> copiedMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            List<String> originalList = entry.getValue();
            List<String> copiedList = new ArrayList<>(originalList);
            copiedMap.put(key, copiedList);
        }
        return copiedMap;
    }

    private static MetaFilter getAnyMetaFilter(String metaId, String version) {
        MetaFilter metaFilter = new MetaFilter();
        metaFilter.setCategories(Collections.singletonList(JaneCategory.AIPP.name()));
        metaFilter.setMetaIds(Collections.singletonList(metaId));
        if (version != null) {
            metaFilter.setVersions(Collections.singletonList(version));
        }
        metaFilter.setOrderBys(Collections.singletonList(formatSorter(null, null)));
        return metaFilter;
    }

    private static MetaFilter getNormalMetaFilter(String metaId, NormalFilterEnum normalFilter) {
        Map<String, List<String>> attributes = new HashMap<String, List<String>>() {{
            // 仅查找普通aipp，不包含预览aipp
            put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.NORMAL.name()));
        }};
        if (normalFilter == NormalFilterEnum.PUBLISHED) {
            attributes.put(AippConst.ATTR_META_STATUS_KEY,
                    Collections.singletonList(AippMetaStatusEnum.ACTIVE.getCode()));
        } else {
            if (normalFilter == NormalFilterEnum.DRAFT) {
                attributes.put(AippConst.ATTR_META_STATUS_KEY,
                        Collections.singletonList(AippMetaStatusEnum.INACTIVE.getCode()));
            }
        }
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        MetaFilter metaFilter = getAnyMetaFilter(metaId, null);
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        metaFilter.setAttributes(attributes);
        return metaFilter;
    }

    /**
     * 判断一个Meta是否被发布
     *
     * @param meta 待验证的Meta
     * @return 该meta是否发布
     */
    public static boolean isPublished(Meta meta) {
        Map<String, Object> attributes = meta.getAttributes();
        if (!attributes.containsKey(AippConst.ATTR_AIPP_TYPE_KEY)
                || !attributes.containsKey(AippConst.ATTR_META_STATUS_KEY)) {
            return false;
        }
        String aippType = String.valueOf(attributes.get(AippConst.ATTR_AIPP_TYPE_KEY));
        String metaStatus = String.valueOf(attributes.get(AippConst.ATTR_META_STATUS_KEY));
        return StringUtils.equals(aippType, AippTypeEnum.NORMAL.name()) && Objects.equals(metaStatus,
                AippMetaStatusEnum.ACTIVE.getCode());
    }

    /**
     * 通过appID获取所有的Meta
     *
     * @param metaService 处理Meta请求的service
     * @param appId app的Id
     * @param aippType aipp的类型
     * @param context 上下文
     * @return 结果
     * @throws AippException 抛出aipp的异常
     */
    public static List<Meta> getAllMetasByAppId(MetaService metaService, String appId, String aippType,
            OperationContext context) throws AippException {
        MetaFilter metaFilter = getMetaFilter(appId, aippType);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    /**
     * 通过appId获取所有的元数据
     *
     * @param metaService 元数据服务
     * @param appId appId
     * @param context 上下文
     * @return 元数据列表
     * @throws AippException aipp通用异常
     */
    public static List<Meta> getAllMetasByAppId(MetaService metaService, String appId, OperationContext context)
            throws AippException {
        MetaFilter metaFilter = getMetaFilter(appId);
        return getListMetaHandle(metaService, metaFilter, context);
    }

    private static MetaFilter getMetaFilter(String appId) {
        MetaFilter metaFilter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, Collections.singletonList(appId));
        metaFilter.setAttributes(attributes);
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        return metaFilter;
    }

    private static MetaFilter getMetaFilter(String appId, String aippType) {
        MetaFilter metaFilter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, Collections.singletonList(appId));
        attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.getType(aippType).type()));
        metaFilter.setAttributes(attributes);
        return metaFilter;
    }

    private static MetaFilter getPreviewMetaFilter(String metaId) {
        MetaFilter metaFilter = getAnyMetaFilter(metaId, null);
        metaFilter.setAttributes(new HashMap<String, List<String>>() {
            {
                // 仅查找预览aipp
                put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.PREVIEW.name()));
            }
        });
        return metaFilter;
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
     * 状态枚举
     */
    public enum NormalFilterEnum {
        // 已发布
        PUBLISHED,
        // 草稿
        DRAFT,
        // 默认
        DEFAULT
    }

    /**
     * 根据versionId批量删除meta
     *
     * @param metaService 使用的{@link MetaService}
     * @param versionIds 需要删除的versionId列表
     * @param context 操作人上下文
     */
    public static void deleteMetasByVersionIds(MetaService metaService, List<String> versionIds,
            OperationContext context) {
        versionIds.forEach(versionId -> metaService.delete(versionId, context));
    }

    /**
     * 根据 appId 获取 aippId
     *
     * @param metaService meta服务
     * @param appId 表示 app 的唯一标识
     * @param context 上下文
     * @return aipp标识
     * @throws AippTaskNotFoundException meta找不到时抛出
     */
    public static String getAippIdByAppId(MetaService metaService, String appId, OperationContext context)
            throws AippTaskNotFoundException {
        List<Meta> metas = MetaUtils.getAllMetasByAppId(metaService, appId, context);
        if (CollectionUtils.isEmpty(metas)) {
            log.error("Meta can not be null.");
            throw new AippTaskNotFoundException(TASK_NOT_FOUND);
        }
        return metas.get(0).getId();
    }
}