/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.header.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.header.regexp.HeaderRegExp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public enum HeaderEnum {
    EDUCATION("教育背景"),
    PROFESSION("工作经历"),
    PROJECT("项目经历"),
    INTERNSHIP("实习经历"),
    SKILL("专业技能"),
    ACKNOWLEDGEMENT("荣誉奖项"),
    RESEARCH("科研"),
    SELF_EVALUATION("自我评价");

    private final String header;

    public static String normalizeHeader(String sectionHeader) {
        for (HeaderEnum instance : HeaderEnum.values()) {
            Matcher matcher = Pattern.compile(HeaderRegExp.getSectionRegExp(instance), Pattern.CASE_INSENSITIVE)
                    .matcher(sectionHeader);
            if (!matcher.find()) {
                continue;
            }
            return instance.header;
        }

        return sectionHeader;
    }
}