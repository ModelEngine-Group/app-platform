/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.publish;

import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;

/**
 * 发布接口.
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface Publisher {
    /**
     * 发布.
     *
     * @param context 发布上下文信息.
     * @param appVersion 应用版本对象.
     */
    void publish(PublishContext context, AppVersion appVersion);
}
