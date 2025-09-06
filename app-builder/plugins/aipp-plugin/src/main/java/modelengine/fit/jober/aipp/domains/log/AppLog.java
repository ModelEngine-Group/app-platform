/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.log;

import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_INFOS_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_INPUT_KEY;

import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.util.JsonUtils;

import lombok.Getter;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * 应用日志对象.
 *
 * @author 张越
 * @since 2025-02-07
 */
public class AppLog {
    private static final HashSet<String> QUESTION_TYPE = new HashSet<>(Arrays.asList(AippInstLogType.QUESTION.name(),
            AippInstLogType.HIDDEN_QUESTION.name(),
            AippInstLogType.QUESTION_WITH_FILE.name()));

    @Getter
    private final AippInstLog logData;

    AppLog(AippInstLog logData) {
        this.logData = logData;
    }

    /**
     * 是否是问题类型的日志.
     *
     * @return true/false.
     */
    public boolean isQuestionType() {
        return QUESTION_TYPE.contains(this.logData.getLogType());
    }

    /**
     * 将 {@link AppLog} 转换为 {@link AippInstLogDataDto.AippInstanceLogBody}.
     *
     * @return {@link AippInstLogDataDto.AippInstanceLogBody} 对象.
     */
    public AippInstLogDataDto.AippInstanceLogBody toBody() {
        return new AippInstLogDataDto.AippInstanceLogBody(this.logData.getLogId(), this.logData.getLogData(),
                this.logData.getLogType(), this.logData.getCreateAt(), this.logData.getCreateUserAccount());
    }

    /**
     * 判断是否是某个类型.
     *
     * @param types 类型列表.
     * @return true/false.
     */
    public boolean is(AippInstLogType... types) {
        String logType = this.logData.getLogType();
        return Arrays.stream(types).anyMatch(e -> StringUtils.equals(logType, e.name()));
    }

    /**
     * 获取输入信息.
     *
     * @return 输入信息的 {@link Optional} 对象.
     */
    public Optional<Map<String, Object>> getInput() {
        Map<String, Object> data = JsonUtils.parseObject(this.logData.getLogData());
        if (data.containsKey(BUSINESS_INFOS_KEY)) {
            Map<String, Object> infos = ObjectUtils.cast(data.get(BUSINESS_INFOS_KEY));
            if (infos.containsKey(BUSINESS_INPUT_KEY)) {
                return Optional.of(ObjectUtils.cast(infos.get(BUSINESS_INPUT_KEY)));
            }
        }
        return Optional.empty();
    }
}
