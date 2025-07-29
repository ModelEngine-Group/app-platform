/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.utils;

import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.jober.common.ErrorCodes.UN_EXCEPTED_ERROR;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序条件解析器
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Builder
public class SortConditionParser {
    @Getter
    private final List<String> encodeSorts;

    @Getter
    private final List<Sorter> decodeSorts;

    private static Sorter decodeSorter(String encodeStr) throws JobberParamException {
        if (StringUtils.isBlank(encodeStr)) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, encodeStr);
        }
        int pos = encodeStr.indexOf(',');
        if (pos == 0 || pos == -1 || pos == encodeStr.length() - 1) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "param format: item_name,direction(descend/ascend)");
        }
        String key = encodeStr.substring(0, pos);
        String value = encodeStr.substring(pos + 1);

        return Sorter.builder().name(key).dir(DirectionEnum.getDirection(value)).build();
    }

    /**
     * 自定义SortConditionParser的builder，不允许同时指定encodeSorts和sorters
     *
     * @author 刘信宏
     * @since 2023-12-08
     */
    public static class SortConditionParserBuilder {
        private List<String> encodeSorts;

        private List<Sorter> decodeSorts;

        /**
         * 接收排序条件的列表，并转换成对应排序对象列表
         *
         * @param encodeSorts 排序条件列表
         * @return SortConditionParserBuilder
         * @throws JobberParamException 解析字符串错误
         * @throws BadRequestException builder错误
         */
        public SortConditionParserBuilder encodeSorts(List<String> encodeSorts)
                throws JobberParamException, BadRequestException {
            this.encodeSorts = encodeSorts;
            verifyBuilder();
            this.decodeSorts = new ArrayList<>();
            for (String rowStr : encodeSorts) {
                this.decodeSorts.add(decodeSorter(rowStr));
            }
            return this;
        }

        /**
         * 限制builder参数，不允许同时指定encodeSorts和sorters
         *
         * @param sorters 解码后的排序条件列表
         * @return SortConditionParserBuilder
         * @throws BadRequestException builder校验异常
         */
        public SortConditionParserBuilder decodeSorts(List<Sorter> sorters) throws BadRequestException {
            verifyBuilder();
            this.decodeSorts = sorters;
            return this;
        }

        private void verifyBuilder() throws BadRequestException {
            if (encodeSorts != null && decodeSorts != null) {
                throw new BadRequestException(UN_EXCEPTED_ERROR, "not allow set 'encodeSorts' and 'sorters'");
            }
        }
    }

    /**
     * 排序条件
     *
     * @author 刘信宏
     * @since 2023-12-08
     */
    @Setter
    @Getter
    @Builder
    public static class Sorter {
        private String name;

        private DirectionEnum dir;
    }
}

