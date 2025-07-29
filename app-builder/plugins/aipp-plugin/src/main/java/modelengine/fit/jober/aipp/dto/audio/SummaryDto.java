/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.audio;

import modelengine.fit.jober.aipp.entity.ffmpeg.FfmpegUtil;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.LLMUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 总结Dto
 *
 * @author 易文渊
 * @since 2024/1/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDto {
    private static final Random POS_RANDOM = new Random();
    private static final Logger log = Logger.get(SummaryDto.class);

    @Property(description = "视频摘要")
    private String summary;

    @Property(description = "分段总结")
    private List<SummarySection> sectionList;

    /**
     * 构造函数
     *
     * @param summaryList list
     * @param segmentSize size
     */
    public SummaryDto(List<String> summaryList, int segmentSize) {
        this.sectionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(summaryList)) {
            for (int i = 0; i < summaryList.size(); ++i) {
                String item = summaryList.get(i);
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                try {
                    SummarySection section =
                            JsonUtils.parseObject(LLMUtils.tryFixLlmJsonString(item), SummarySection.class);
                    section.setPosition(FfmpegUtil.formatTimestamps(Math.max(
                            segmentSize * i - POS_RANDOM.nextInt(60) - 30, 0)));
                    sectionList.add(section);
                } catch (IOException | IllegalArgumentException | UnsupportedOperationException | ClassCastException
                    | NullPointerException e) {
                    log.warn("Llm generate unexpect rsp.");
                }
            }
        }
    }
}
