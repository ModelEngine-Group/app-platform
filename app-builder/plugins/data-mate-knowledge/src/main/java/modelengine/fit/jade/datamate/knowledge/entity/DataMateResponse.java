/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.entity;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * DataMate 接口返回值结构。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
public class DataMateResponse<T> {
    private T data;
    private String code;
    private String message;

    /**
     * 从响应上下文 Map 中解析并构建 {@link DataMateResponse} 对象。
     * <p>
     * 针对 DataMateRetrievalResult 的特殊性，如果 context 中的 data 是一个 List，
     * 会自动将其封装进 DataMateRetrievalResult 对象的 data 属性中。
     * </p>
     *
     * @param <T>     响应数据的泛型类型。
     * @param context 包含响应信息的上下文 {@link Map}。
     * @param type    目标类型的 {@link Class}。
     * @return 返回构建好的 {@link DataMateResponse} 实例。
     */
    public static <T> DataMateResponse<T> from(Map<String, Object> context, Class<T> type) {
        DataMateResponse<T> response = new DataMateResponse<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Object rawData = context.get("data");

        // 核心逻辑：如果目标类型是 DataMateRetrievalResult，且 rawData 是个 List
        if (type.equals(DataMateRetrievalResult.class) && rawData instanceof List) {
            DataMateRetrievalResult resultObject = new DataMateRetrievalResult();
            // 将 List 里的 Map 转换为 List<DataMateRetrievalChunksEntity>
            JavaType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, DataMateRetrievalChunksEntity.class);
            List<DataMateRetrievalChunksEntity> chunks = objectMapper.convertValue(rawData, listType);

            resultObject.setData(chunks);
            response.data = (T) resultObject; // 强制转换回泛型 T
        } else {
            // 其他常规情况按原逻辑处理
            response.data = objectMapper.convertValue(rawData, type);
        }

        response.code = ObjectUtils.cast(context.get("code"));
        response.message = ObjectUtils.cast(context.get("message"));
        return response;
    }
}

