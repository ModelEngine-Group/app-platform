/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;

/**
 * {@link AppTask} 的工厂类
 *
 * @author 张越
 * @since 2025-02-06
 */
public class AppTaskUtils {
    /**
     * 转换为发布数据对象.
     *
     * @param appTask 任务领域对象.
     * @param appVersion 应用版本.
     * @param converterFactory 转化器工厂。
     * @return {@link AppBuilderAppDto} 对象.
     */
    public static AppBuilderAppDto toPublishedAppBuilderAppDto(AppTask appTask, AppVersion appVersion,
            ConverterFactory converterFactory) {
        AppBuilderAppDto appDto = converterFactory.convert(appVersion, AppBuilderAppDto.class);
        appDto.setPublishedDescription(appTask.getEntity().getPublishDescription());
        appDto.setPublishedUpdateLog(appTask.getEntity().getPublishLog());
        return appDto;
    }
}
