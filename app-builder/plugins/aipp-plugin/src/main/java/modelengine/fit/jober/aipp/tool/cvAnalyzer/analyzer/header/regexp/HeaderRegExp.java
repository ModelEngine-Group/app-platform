/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.header.regexp;

import lombok.Getter;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.header.enums.HeaderEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderRegExp {
    private static final String PREFIX = "^[\\w ]*"; // PDF每行开头的特殊符号可能会被转换成字母+空格
    private static final String SUFFIX = "$";

    private static final Map<HeaderEnum, List<String>> SECTION_NAME_ZH = new HashMap<>();
    private static final Map<HeaderEnum, List<String>> SECTION_NAME_EN = new HashMap<>();
    private static final Map<HeaderEnum, String> SECTION_REG_EXP = new HashMap<>();

    @Getter
    private static String overallRegExp;

    static {
        initSectionNames();
        initSectionRegExp();
    }

    private static void initSectionNames() {
        initChineseSectionNames();
        initEnglishSectionNames();
    }

    private static void initChineseSectionNames() {
        SECTION_NAME_ZH.put(HeaderEnum.EDUCATION,
                Arrays.asList("教育", "教育经历", "教育背景", "学术经历", "学术背景", "工作学习经历"));
        SECTION_NAME_ZH.put(HeaderEnum.PROFESSION, Arrays.asList("工作经历", "职业经历", "工作履历", "职业履历"));
        SECTION_NAME_ZH.put(HeaderEnum.PROJECT,
                Arrays.asList("项目[\\p{P}\\u4E00-\\u9FFF]*",
                        "在校项目经历",
                        "个人项目",
                        "工作项目",
                        "学习经历",
                        "科研经历",
                        "科研项目经历",
                        "科研与项目经历",
                        "作品展示"));
        SECTION_NAME_ZH.put(HeaderEnum.INTERNSHIP, Arrays.asList("实习经历", "暑期实习"));
        SECTION_NAME_ZH.put(HeaderEnum.SKILL,
                Arrays.asList("技能[\\p{P}\\u4E00-\\u9FFF]*",
                        "专业技能",
                        "专业能力",
                        "个人技能",
                        "个人优势",
                        "相关技能",
                        "掌握技能",
                        "技术能力",
                        "技术与技能",
                        "研究方向",
                        "工具",
                        "开源贡献"));
        SECTION_NAME_ZH.put(HeaderEnum.SELF_EVALUATION,
                Arrays.asList("校园经历",
                        "社团和组织经历",
                        "学生工作",
                        "学生活动",
                        "社会实践",
                        "学生工作与兴趣爱好",
                        "兴趣",
                        "兴趣爱好",
                        "兴趣/特长",
                        "兴趣爱好&校园经历",
                        "个人优势",
                        "个人评价",
                        "个人总结",
                        "自我评价",
                        "个性特点",
                        "语言能力",
                        "等级证书",
                        "技能/证书",
                        "其他"));
        SECTION_NAME_ZH.put(HeaderEnum.ACKNOWLEDGEMENT,
                Arrays.asList("荣誉奖项",
                        "荣誉奖项及论文成果",
                        "荣誉奖励",
                        "荣誉证书",
                        "奖励与荣誉",
                        "奖项荣誉",
                        "所获奖项",
                        "获得奖励",
                        "获奖证书",
                        "获奖情况",
                        "奖项与证书",
                        "证书"));
        SECTION_NAME_ZH.put(HeaderEnum.RESEARCH,
                Arrays.asList("论文发表", "发表论文", "研究成果", "出版物", "成果", "专利"));
    }

    private static void initEnglishSectionNames() {
        SECTION_NAME_EN.put(HeaderEnum.EDUCATION,
                Arrays.asList("education[s]?", "educational background", "academic background"));
        SECTION_NAME_EN.put(HeaderEnum.PROFESSION,
                Arrays.asList("professional history",
                        "work history",
                        "employment history",
                        "professional experience",
                        "work experience",
                        "employment experience"));
        SECTION_NAME_EN.put(HeaderEnum.PROJECT,
                Arrays.asList("project[s]?", "project list", "research & projects", "experience[s]?"));
        SECTION_NAME_EN.put(HeaderEnum.INTERNSHIP, Arrays.asList("internship", "internship experience"));
        SECTION_NAME_EN.put(HeaderEnum.SKILL,
                Arrays.asList("skills", "technology skills", "professional skills", "research fields"));
        SECTION_NAME_EN.put(HeaderEnum.SELF_EVALUATION,
                Arrays.asList("profile", "professional summary", "interests", "languages"));
        SECTION_NAME_EN.put(HeaderEnum.ACKNOWLEDGEMENT, Arrays.asList("awards", "honors & awards"));
        SECTION_NAME_EN.put(HeaderEnum.RESEARCH, Arrays.asList("publication[s]?", "patent"));
    }

    private static void initSectionRegExp() {
        for (HeaderEnum sectionHeader : HeaderEnum.values()) {
            String regExp = generateSingleRegExp(sectionHeader) + "|" + generateTupleRegExp(sectionHeader);
            SECTION_REG_EXP.put(sectionHeader, PREFIX + "(" + regExp + ")" + SUFFIX);
        }

        overallRegExp = String.join("|", SECTION_REG_EXP.values());
    }

    private static String generateSingleRegExp(HeaderEnum sectionHeader) {
        return String.join("|", SECTION_NAME_ZH.get(sectionHeader)) + "|" + String.join("|",
                SECTION_NAME_EN.get(sectionHeader));
    }

    private static String generateTupleRegExp(HeaderEnum sectionHeader) {
        List<String> chineseNames = SECTION_NAME_ZH.get(sectionHeader);
        List<String> englishNames = SECTION_NAME_EN.get(sectionHeader);
        StringBuilder doubleRegExp = new StringBuilder();
        for (String chineseName : chineseNames) {
            for (String englishName : englishNames) {
                if (doubleRegExp.length() > 0) {
                    doubleRegExp.append("|");
                }
                doubleRegExp.append(chineseName)
                        .append(" +")
                        .append(englishName)
                        .append("|")
                        .append(englishName)
                        .append(" +")
                        .append(chineseName);
            }
        }
        return doubleRegExp.toString();
    }

    public static String getSectionRegExp(HeaderEnum sectionHeader) {
        return SECTION_REG_EXP.get(sectionHeader);
    }
}
