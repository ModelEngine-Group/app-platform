/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.tool.parallel.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jade.aipp.tool.parallel.entities.Config;
import modelengine.fit.jade.aipp.tool.parallel.entities.ToolCall;
import modelengine.fit.jade.aipp.tool.parallel.support.AippInstanceStatus;
import modelengine.fit.jade.aipp.tool.parallel.support.TaskExecutor;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * 批量工具调用的请求。
 *
 * @author 宋永坦
 * @since 2025-04-23
 */
public class BatchRequest {
    private static final Logger LOG = Logger.get(BatchRequest.class);

    private final String id = UUID.randomUUID().toString();
    private final Queue<ToolCallTask> undoToolCallTasks = new LinkedList<>();
    private final Map<Integer, ToolCallTask> doingToolCallTasks = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, Object> results = new LinkedHashMap<>();
    private final Config config;
    private final ToolExecuteService toolExecuteService;
    private final TaskExecutor taskExecutor;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final AippInstanceStatus aippInstanceStatus;
    private final Map<String, Object> context;

    private volatile int waitOutputCount;
    private volatile Throwable exception = null;
    private volatile ToolCallTask exceptionToolCallTask = null;

    /**
     * 批量执行工具的请求的构造方法。
     *
     * @param toolCalls 表示目标调用工具列表的 {@link List}{@code <}{@link ToolCall}{@code >}。
     * @param config 表示批量调用配置的 {@link Config}。
     * @param toolExecuteService 表示工具执行服务的 {@link ToolExecuteService}。
     * @param taskExecutor 表示任务执行器的 {@link TaskExecutor}。
     * @param aippInstanceStatus 表示对话实例状态服务的 {@link AippInstanceStatus}。
     * @param context 表示对话执行上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public BatchRequest(List<ToolCall> toolCalls, Config config, ToolExecuteService toolExecuteService,
            TaskExecutor taskExecutor, AippInstanceStatus aippInstanceStatus, Map<String, Object> context) {
        Validation.notEmpty(toolCalls, "The tool call list should not be empty.");
        Validation.notNull(toolExecuteService, "The tool execute service should not be null.");
        Validation.notNull(taskExecutor, "The task executor should not be null.");
        Validation.notNull(aippInstanceStatus, "The instance status service should not be null.");
        Validation.notNull(context, "The context should not be null.");
        toolCalls.forEach(toolCall -> undoToolCallTasks.add(ToolCallTask.builder()
                .index(this.undoToolCallTasks.size())
                .toolCall(toolCall)
                .build()));
        this.waitOutputCount = this.undoToolCallTasks.size();
        this.toolExecuteService = toolExecuteService;
        this.config = config;
        this.taskExecutor = taskExecutor;
        this.aippInstanceStatus = aippInstanceStatus;
        this.context = context;
    }

    /**
     * 开始该批工具的调用。
     */
    public void post() {
        LOG.info("Start calling the batch tools. [batchId={}, concurrency={}, toolUniqueNames={}]",
                this.id,
                this.config.getConcurrency(),
                this.dumpyUndoToolUniqueNames());
        for (int i = 0; i < this.config.getConcurrency(); ++i) {
            if (!this.postUndoTask()) {
                break;
            }
        }
    }

    /**
     * 投递未执行的任务。
     *
     * @return 表示是否有任务投递的 {@code boolean}。
     */
    private boolean postUndoTask() {
        ToolCallTask task;
        synchronized (this.undoToolCallTasks) {
            if (this.undoToolCallTasks.isEmpty()) {
                return false;
            }
            task = this.undoToolCallTasks.poll();
        }
        this.taskExecutor.post(() -> {
            if (this.hasException()) {
                LOG.warn("Ignore the tool call, because the batch request has exception. [batchId={}, "
                        + "toolUniqueName={}, index={}]", this.id, task.getToolCall().getUniqueName(), task.getIndex());
                return;
            }
            try {
                if (!this.aippInstanceStatus.isRunning(this.context)) {
                    LOG.warn("Ignore the tool call, because the batch request is not running. [batchId={}, "
                                    + "toolUniqueName={}, index={}, context={}]",
                            this.id,
                            task.getToolCall().getUniqueName(),
                            task.getIndex(),
                            this.context.toString());
                    this.setException(task,
                            new IllegalStateException(StringUtils.format("The instance is not running. [context={0}]",
                                    this.context.toString())));
                    return;
                }
                LOG.info("Start calling the tool. [batchId={}, toolUniqueName={}, index={}]",
                        this.id,
                        task.getToolCall().getUniqueName(),
                        task.getIndex());

                this.doingToolCallTasks.put(task.getIndex(), task);
                String jsonArgs = JSONObject.toJSONString(task.getToolCall().getArgs(),
                        SerializerFeature.WriteMapNullValue);
                this.complete(task,
                        JSONArray.parse(this.toolExecuteService.execute(task.getToolCall().getUniqueName(), jsonArgs)));
            } catch (Throwable ex) {
                this.setException(task, ex);
            } finally {
                this.doingToolCallTasks.remove(task.getIndex());
            }

        });
        return true;
    }

    /**
     * 等待该批工具的完成，并返回结果。
     *
     * @return 表示工具执行结果的 {@link List}{@code <}{@link Object}{@code >}。
     * @throws IllegalStateException 当有工具调用失败/中断时。
     */
    public Map<String, Object> await() {
        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(this.exception.getMessage(), this.exception);
        }
        if (this.hasException()) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to call the tool. [batchId={0}, uniqueName={1}, index={2}, errorMessage={3}]",
                    this.id,
                    this.exceptionToolCallTask.getToolCall().getUniqueName(),
                    this.exceptionToolCallTask.getIndex(),
                    this.exception.getMessage()), this.exception);
        }
        return this.results;
    }

    private boolean hasException() {
        return this.exception != null;
    }

    private void complete(ToolCallTask task, Object result) {
        LOG.info("The tool call is completed. [batchId={}, uniqueName={}, index={}]",
                this.id,
                task.getToolCall().getUniqueName(),
                task.getIndex());

        synchronized (this.results) {
            this.results.put(task.getToolCall().getOutputName(), result);
            this.waitOutputCount--;
            if (this.waitOutputCount == 0) {
                this.countDownLatch.countDown();
            }
        }
        this.postUndoTask();
    }

    private void setException(ToolCallTask task, Throwable ex) {
        LOG.error("Tool exception. [batchId={}, uniqueName={}, index={}, errorMessage={}, doingTools={}]",
                this.id,
                task.toolCall.getUniqueName(),
                task.index,
                ex.getMessage(),
                this.dumpyDoingToolUniqueNames());

        synchronized (this.results) {
            if (hasException()) {
                return;
            }
            this.exception = ex;
            this.exceptionToolCallTask = task;
        }
        this.countDownLatch.countDown();
    }

    private String dumpyDoingToolUniqueNames() {
        return this.doingToolCallTasks.values()
                .stream()
                .map(toolCallTask -> StringUtils.format("{0}-{1}",
                        toolCallTask.getIndex(),
                        toolCallTask.getToolCall().getUniqueName()))
                .collect(Collectors.joining(","));
    }

    private String dumpyUndoToolUniqueNames() {
        synchronized (this.undoToolCallTasks) {
            return this.undoToolCallTasks.stream()
                    .map(toolCallTask -> StringUtils.format("{0}-{1}",
                            toolCallTask.getIndex(),
                            toolCallTask.getToolCall().getUniqueName()))
                    .collect(Collectors.joining(","));
        }
    }

    @Data
    @Builder
    private static class ToolCallTask {
        private final int index;
        private final ToolCall toolCall;
    }
}
