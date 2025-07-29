/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;

import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 普通检索节点的checker
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Component
public class RetrievalNodeChecker extends AbstractNodeChecker {
    @Override
    public List<CheckResult> validate(AppCheckDto appCheckDto, OperationContext context) {
        return this.invalidNodeConfig(appCheckDto, appCheckDto.getType());
    }
}
