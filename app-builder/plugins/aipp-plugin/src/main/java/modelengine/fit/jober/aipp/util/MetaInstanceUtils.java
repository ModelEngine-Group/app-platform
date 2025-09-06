/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MetaInstance 操作工具类
 *
 * @author 方誉州
 * @since 2024/6/14
 */
public class MetaInstanceUtils {
    private static final Logger log = Logger.get(MetaInstanceUtils.class);

    /**
     * 更新businessData中meta实例的信息
     *
     * @param service 使用的元数据实例服务{@link MetaInstanceService}
     * @param info Meta实例声明信息{@link InstanceDeclarationInfo}
     * @param businessData business data
     * @param context 操作上下文{@link OperationContext}
     */
    public static void persistInstance(MetaInstanceService service, InstanceDeclarationInfo info,
            Map<String, Object> businessData, OperationContext context) {
        String versionId = notBlank(ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY)),
                "Get blank meta version id");
        String instId = notBlank(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)),
                "Get blank aipp instance id");
        service.patchMetaInstance(versionId, instId, info, context);
    }

    /**
     * 获取实例详情
     *
     * @param versionId aipp version id，唯一标识
     * @param instanceId 实例Id
     * @param context 操作上下文
     * @param metaInstanceService 使用的元数据实例服务{@link MetaInstanceService}
     * @return instance信息
     */
    public static Instance getInstanceDetail(String versionId, String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        RangedResultSet<Instance> instances = getOneInstance(versionId, instanceId, context, metaInstanceService);
        if (instances.getRange().getTotal() == 0) {
            log.error("versionId {} inst{} not found.", versionId, instanceId);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "aipp inst not found. inst " + instanceId);
        }
        return instances.getResults().get(0);
    }

    /**
     * 仅使用instanceId获取实例详情
     * <p>注意：本操作仅把相关内容放到fitable中了</p>
     *
     * @param instanceId 实例Id
     * @param context 操作上下文
     * @param metaInstanceService 使用的元数据实例服务{@link MetaInstanceService}
     * @return instance信息
     */
    public static Instance getInstanceDetailByInstanceId(String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        return Optional.ofNullable(metaInstanceService.retrieveById(instanceId, context))
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        "aipp inst not found. inst " + instanceId));
    }

    /**
     * 获取批量实例信息
     *
     * @param versionId aipp version id，唯一标识
     * @param instanceId 实例Id
     * @param context 操作上下文
     * @param metaInstanceService 使用的元数据实例服务{@link MetaInstanceService}
     * @return 批量返回的Instance信息
     */
    public static RangedResultSet<Instance> getOneInstance(String versionId, String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        return metaInstanceService.list(Collections.singletonList(instanceId), 0, 1, context);
    }

    /**
     * 获取MetaList
     *
     * @param versionId 表示实例所属meta唯一标识
     * @param metaInstanceService 处理Meta请求的service
     * @param context 上下文
     * @return 结果
     * @throws AippException 抛出aipp的异常
     */
    public static List<Instance> getOneInstance(String versionId, MetaInstanceService metaInstanceService,
            OperationContext context) throws AippException {
        final int limitPerQuery = 10;
        return MetaUtils.getAllFromRangedResult(limitPerQuery,
                        (offset) -> metaInstanceService.list(versionId, offset, limitPerQuery, context))
                .collect(Collectors.toList());
    }
}
