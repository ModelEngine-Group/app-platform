/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.tool.parallel.service.impl;

import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jade.aipp.tool.parallel.domain.BatchRequest;
import modelengine.fit.jade.aipp.tool.parallel.entities.Config;
import modelengine.fit.jade.aipp.tool.parallel.entities.ToolCall;
import modelengine.fit.jade.aipp.tool.parallel.service.ParallelToolService;
import modelengine.fit.jade.aipp.tool.parallel.support.AippInstanceStatus;
import modelengine.fit.jade.aipp.tool.parallel.support.TaskExecutor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.tool.annotation.Attribute;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 并行工具调用节点服务接口的实现。
 *
 * @author 宋永坦
 * @since 2025-04-23
 */
@Component
@Group(name = "ParallelToolImpl")
public class ParallelToolServiceImpl implements ParallelToolService {
    private static final Logger LOG = Logger.get(ParallelToolServiceImpl.class);
    private static final int MIN_CONCURRENCY = 1;
    private static final int MAX_CONCURRENCY = 32;

    private final ToolExecuteService toolExecuteService;
    private final TaskExecutor taskExecutor;
    private final Config defaultConfig;
    private final AippInstanceStatus aippInstanceStatus;

    public ParallelToolServiceImpl(@Fit ToolExecuteService toolExecuteService, TaskExecutor taskExecutor,
            @Value("${parallel-tool.concurrency:8}") int defaultConcurrency, AippInstanceStatus aippInstanceStatus) {
        this.toolExecuteService = toolExecuteService;
        this.taskExecutor = taskExecutor;
        this.defaultConfig = Config.builder()
                .concurrency(Validation.between(defaultConcurrency,
                        MIN_CONCURRENCY,
                        MAX_CONCURRENCY,
                        StringUtils.format("The parallel tool concurrent should between {0} and {1}.",
                                MIN_CONCURRENCY,
                                MAX_CONCURRENCY)))
                .build();
        this.aippInstanceStatus = aippInstanceStatus;
    }

    @Override
    @Fitable("default")
    @ToolMethod(name = "parallelToolDefault", description = "用于并行执行工具", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "BASIC"),
            @Attribute(key = "tags", value = "PARALLELNODESTATE")
    })
    @Property(description = "并行执行工具的结果")
    public Map<String, Object> call(List<ToolCall> toolCalls, Config config, Map<String, Object> context) {
        this.validateToolCalls(toolCalls);

        BatchRequest batchRequest = new BatchRequest(toolCalls,
                this.getConfig(config),
                this.toolExecuteService,
                this.taskExecutor,
                this.aippInstanceStatus,
                context);
        batchRequest.post();
        return batchRequest.await();
    }

    private void validateToolCalls(List<ToolCall> toolCalls) {
        Validation.notEmpty(toolCalls, "The tools should not be empty.");
        toolCalls.forEach(this::validateToolCall);
        this.validateOutputName(toolCalls);
    }

    private void validateToolCall(ToolCall toolCall) {
        Validation.notNull(toolCall, "The tool should not be null.");
        Validation.isTrue(StringUtils.isNotEmpty(toolCall.getUniqueName()),
                "The tool unique name should not be empty.");
        Validation.notNull(toolCall.getArgs(), "The tool args should not be null.");
        toolCall.getArgs().entrySet().forEach(arg -> {
            Validation.notNull(arg,
                    StringUtils.format("The tool arg should not be null. [toolUniqueName={0}]",
                            toolCall.getUniqueName()));
            Validation.isTrue(StringUtils.isNotEmpty(arg.getKey()),
                    StringUtils.format("The tool arg name should not be empty. [toolUniqueName={0}]",
                            toolCall.getUniqueName()));
        });
    }

    private void validateOutputName(List<ToolCall> toolCalls) {
        Set<String> hitSet = new HashSet<>();
        toolCalls.forEach(toolCall -> {
            Validation.isTrue(StringUtils.isNotEmpty(toolCall.getOutputName()),
                    StringUtils.format("The tool output name should not be empty. [toolUniqueName={0}]",
                            toolCall.getUniqueName()));
            Validation.isTrue(hitSet.add(toolCall.getOutputName()),
                    StringUtils.format("Duplicate tool output name detected. [toolUniqueName={0}, outputName={1}]",
                            toolCall.getUniqueName(),
                            toolCall.getOutputName()));
        });
    }

    private Config getConfig(Config config) {
        if (config == null || config.getConcurrency() == null) {
            return this.defaultConfig;
        }
        if (config.getConcurrency() < MIN_CONCURRENCY || config.getConcurrency() > MAX_CONCURRENCY) {
            LOG.warn("The given concurrency is illegal, it should between {} and {}, use default config instead. "
                    + "[concurrency={}]", MIN_CONCURRENCY, MAX_CONCURRENCY, config.getConcurrency());
            return this.defaultConfig;
        }
        return config;
    }
}
