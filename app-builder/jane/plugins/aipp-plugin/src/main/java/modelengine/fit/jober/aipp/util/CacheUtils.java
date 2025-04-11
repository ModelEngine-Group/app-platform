/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 本类提供一些缓存机制
 * <p>当前提供从appid查询meta的缓存</p>
 *
 * @author 姚江
 * @since 2024-11-04
 */
@Component
public class CacheUtils {
    private static final Logger log = Logger.get(CacheUtils.class);

    /**
     * 用于缓存appId to app
     */
    public static final Cache<String, AppBuilderAppPo> APP_CACHE =
            Caffeine.newBuilder().expireAfterAccess(48, TimeUnit.HOURS).maximumSize(30).build();

    /**
     * 用于缓存flowDefinitionId to flowInfo
     */
    public static final Cache<String, FlowInfo> FLOW_CACHE =
            Caffeine.newBuilder().expireAfterAccess(48, TimeUnit.HOURS).maximumSize(30).build();

    /**
     * 用于缓存app_id和aipp_id的关系
     */
    private static final Cache<String, String> APP_ID_TO_AIPP_ID_CACHE =
            Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.DAYS).maximumSize(1000).build();

    /**
     * 用于缓存应用ID和最新发布的元数据关系
     */
    private static final Cache<String, Meta> AIPP_ID_TO_LAST_META_CACHE =
            Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).maximumSize(1000).build();

    /**
     * 清理缓存
     */
    public static void clear() {
        APP_CACHE.invalidateAll();
        FLOW_CACHE.invalidateAll();
        APP_ID_TO_AIPP_ID_CACHE.invalidateAll();
        AIPP_ID_TO_LAST_META_CACHE.invalidateAll();

        APP_CACHE.cleanUp();
        FLOW_CACHE.cleanUp();
        APP_ID_TO_AIPP_ID_CACHE.cleanUp();
        AIPP_ID_TO_LAST_META_CACHE.cleanUp();
    }

    /**
     * 用于获取flowdefinition的缓存
     *
     * @param flowsService 操作flow的service
     * @param flowDefinitionId 缓存的flowDefinition的id
     * @param context 人员上下文
     * @return 缓存的FlowInfo
     */
    public static FlowInfo getPublishedFlowWithCache(FlowsService flowsService, String flowDefinitionId,
            OperationContext context) {
        return FLOW_CACHE.get(flowDefinitionId, id -> flowsService.getFlows(id, context));
    }

    /**
     * 根据应用唯一标识查询对应元数据。
     *
     * @param metaService 表示用于查询元数据的服务实例的 {@link MetaService}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param isDebug 表示是否为调试会话的 {@code boolean}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示应用对应的元数据信息的 {@link Meta}。
     */
    public static Meta getMetaByAppId(MetaService metaService, String appId, boolean isDebug,
            OperationContext context) {
        if (isDebug) {
            return getLatestMetaByAppId(metaService, appId, context);
        }
        // 获取一个aipp_id的缓存，然后根据aipp_id查询最新发布版的meta。
        String aippId =
                APP_ID_TO_AIPP_ID_CACHE.get(appId, id -> getLatestMetaByAppId(metaService, id, context).getId());
        return AIPP_ID_TO_LAST_META_CACHE.get(aippId, (ignore) -> {
            Meta lastPublishedMeta = MetaUtils.getLastPublishedMeta(metaService, appId, context);
            return Optional.ofNullable(lastPublishedMeta)
                    .orElseThrow(() -> new AippException(AippErrCode.APP_CHAT_PUBLISHED_META_NOT_FOUND));
        });
    }

    private static Meta getLatestMetaByAppId(MetaService metaService, String appId, OperationContext context) {
        List<Meta> metas = MetaUtils.getAllMetasByAppId(metaService, appId, context);
        if (CollectionUtils.isEmpty(metas)) {
            log.error("No metas found for appId: {}" + appId);
            throw new AippException(AippErrCode.APP_CHAT_DEBUG_META_NOT_FOUND);
        }
        return metas.get(0);
    }
}
