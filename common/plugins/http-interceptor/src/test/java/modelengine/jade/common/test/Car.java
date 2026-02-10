/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.test;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 表示汽车的测试数据。
 *
 * @author 李金绪
 * @since 2024-09-03
 */
public class Car {
    @Min(value = 0, message = "{test.car.seats}")
    @Max(value = 6, message = "{test.car.seats}")
    private int seats;

    @NotBlank(message = "品牌不能为空!")
    private String brand;

    /**
     * 表示创建一个 {@link Car} 的新实例。
     */
    public Car() {}

    /**
     * 表示创建一个 {@link Car} 的新实例。
     *
     * @param seats 表示座位数量的 {@code int}。
     * @param brand 表示品牌的 {@link String}。
     */
    public Car(int seats, String brand) {
        this.seats = seats;
        this.brand = brand;
    }
}
