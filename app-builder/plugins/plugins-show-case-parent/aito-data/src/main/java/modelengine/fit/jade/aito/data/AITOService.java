/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aito.data;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

import java.util.List;
import java.util.Map;

/**
 * 内置问界插件接口。
 *
 * @author 夏斐
 * @since 2025/3/17
 */
@Group(name = "AITO")
public interface AITOService {
    /**
     * 问界产品信息查询。
     *
     * @param args 表示查询参数的 {@link String}。
     * @return 表示查询结果的 {@link String}。
     */
    @ToolMethod(name = "问界产品信息查询", description = "用于查询问界产品信息查询")
    @Genericable("modelengine.jober.aipp.AITO.describe")
    String allDescribe(@Property(description = "预留参数，当前传入空字符串即可", required = true) String args);

    /**
     * 问界产品宣传图。
     *
     * @param carType 表示车辆类型的 {@link String}。
     * @return 表示车辆图片的 {@link String}。
     */
    @ToolMethod(name = "问界产品宣传图", description = "用于查询问界产品宣传图")
    @Genericable("modelengine.jober.aipp.AITO.url")
    List<Map<String, String>> url(@Property(description = "车的型号", required = true) String carType);
}
