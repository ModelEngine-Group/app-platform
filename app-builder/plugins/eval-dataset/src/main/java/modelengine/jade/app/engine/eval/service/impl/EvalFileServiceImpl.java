/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service.impl;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATA_INVALID_ERROR;
import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.FILE_INVALID_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.jade.app.engine.eval.entity.JsonEntity;
import modelengine.jade.app.engine.eval.service.EvalFileService;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.schema.SchemaGenerator;
import modelengine.jade.schema.SchemaValidator;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalFileService} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
@Component
public class EvalFileServiceImpl implements EvalFileService {
    private static final Logger log = Logger.get(EvalFileServiceImpl.class);

    private ObjectMapper objectMapper;

    private final SchemaGenerator generator;

    private final SchemaValidator validator;

    /**
     * 表示评估数据文件解析服务实现的构建器。
     *
     * @param generator 表示生成评估数据约束服务的 {@link SchemaGenerator}。
     * @param validator 表示根据数据约束校验评估数据的 {@link SchemaValidator}。
     */

    public EvalFileServiceImpl(SchemaGenerator generator, SchemaValidator validator) {
        this.generator = generator;
        this.validator = validator;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public JsonEntity parseJsonFileToEvalData(ReadableBinaryEntity file) {
        List<String> contents = null;
        String schema = null;
        try (InputStream data = file.getInputStream()) {
            StringBuilder jsonStrBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonStrBuilder.append(line);
                }
            }
            contents = this.parseJsonToList(jsonStrBuilder.toString());
            schema = this.generator.generateSchema(contents.get(0));
            this.validator.validate(schema, contents);
            return new JsonEntity(contents, schema);
        } catch (IOException exception) {
            throw new ModelEngineException(FILE_INVALID_ERROR, exception);
        } catch (JsonSchemaInvalidException exception) {
            log.error("Verify content error code: {}, error message: {}", exception.getCode(), exception.getMessage());
            throw new ModelEngineException(DATA_INVALID_ERROR,
                    exception,
                    contents.get(0),
                    schema,
                    exception.getMessage());
        }
    }

    private List<String> parseJsonToList(String json) throws JsonProcessingException {
        List<Map<String, Object>> jsonList =
                this.objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        return jsonList.stream().map(obj -> {
            try {
                return this.objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException exception) {
                throw new ModelEngineException(FILE_INVALID_ERROR, exception);
            }
        }).collect(Collectors.toList());
    }
}