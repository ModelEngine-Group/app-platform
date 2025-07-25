package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.AppCoordinate;

import java.util.Map;

/**
 * 表示应用同步执行服务。
 *
 * @author 宋永坦
 * @since 2025-07-24
 */
public interface AppSyncInvokerService {
    /**
     * 执行应用并获取结果。
     *
     * @param appCoordinate 表示应用坐标的 {@link AppCoordinate}。
     * @param initContext 表示应用启动参数的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param timeout 表示超时时间的 {@code long}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @return 表示应用返回结果。
     */
    Object invoke(AppCoordinate appCoordinate, Map<String, Object> initContext, long timeout,
            OperationContext operationContext);
}
