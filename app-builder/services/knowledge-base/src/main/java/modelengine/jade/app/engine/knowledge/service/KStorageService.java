/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.knowledge.dto.KStorageDto;
import modelengine.jade.app.engine.knowledge.dto.enums.KStorageType;

import java.util.List;

/**
 * 存储服务的管理接口。
 *
 * @since 2024-05-18
 */
public interface KStorageService {
    /**
     * 获取所有存储服务。
     *
     * @return 返回存储服务列表。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.getAllStorages")
    List<KStorageDto> getAllStorages();

    /**
     * 获取存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     * @return 返回存储服务。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.getById")
    KStorageDto getById(Long id);

    /**
     * 删除存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.delete")
    void delete(Long id);

    /**
     * 创建存储服务。
     *
     * @param kStorageDto 表示存储服务的 {@link KStorageDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.create")
    void create(KStorageDto kStorageDto);

    /**
     * 更新存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     * @param kStorageDto 表示存储服务的 {@link KStorageDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.update")
    void update(Long id, KStorageDto kStorageDto);

    /**
     * 根据tableId获取存储服务。
     *
     * @param tableId 知识表的主键 {@link Long}。
     * @return 返回存储服务。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.getByTableId")
    KStorageDto getByTableId(Long tableId);

    /**
     * 根据存储类型获取存储服务。
     *
     * @param type 知识表的主键 {@link KStorageType}。
     * @return 返回存储服务。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KStorageService.getByType")
    KStorageDto getByType(KStorageType type);
}
