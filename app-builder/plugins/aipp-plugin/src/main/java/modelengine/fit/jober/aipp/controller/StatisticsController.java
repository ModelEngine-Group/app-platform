/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.StatisticsDTO;
import modelengine.fit.jober.aipp.service.StatisticsService;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

/**
 * appEngine统计相关接口
 *
 * @author 陈潇文
 * @since 2024-12-26
 */
@Component
@RequestMapping(path = "v1/api/{tenant_id}/statistics")
public class StatisticsController extends AbstractController {
    private final StatisticsService statisticsService;

    /**
     * 构造函数
     *
     * @param authenticator 表示验证器的 {@link Authenticator}。
     * @param statisticsService 表示统计服务的 {@link StatisticsService}。
     */
    public StatisticsController(Authenticator authenticator, StatisticsService statisticsService) {
        super(authenticator);
        this.statisticsService = statisticsService;
    }

    /**
     * 获取统计数据。
     *
     * @param httpRequest 表示http请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户标识的 {@link String}。
     * @return 表示统计数据的 {@link StatisticsDTO}。
     */
    @GetMapping(description = "查询appengine统计信息")
    public Rsp<StatisticsDTO> getStatistics(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(this.statisticsService.getStatistics(this.contextOf(httpRequest, tenantId)));
    }
}
