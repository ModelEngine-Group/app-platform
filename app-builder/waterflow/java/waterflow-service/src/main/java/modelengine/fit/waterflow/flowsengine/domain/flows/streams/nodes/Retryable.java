/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;
import modelengine.fitframework.broker.client.ClientLocalExecutorNotFoundException;
import modelengine.fitframework.broker.client.TargetNotFoundException;
import modelengine.fitframework.broker.server.ServerLocalExecutorNotFoundException;
import modelengine.fitframework.exception.ClientException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 流程错误重试机制
 * 有业务更新发生错误后的context，并决定是否重试
 *
 * @author 高诗意
 * @since 2023/09/07
 */
public class Retryable<I> {
    private static final long RETRY_INTERVAL = 5000L;

    private final FlowContextRepo<I> repo;

    private final To<I, I> to;

    public Retryable(FlowContextRepo<I> repo, To<I, I> to) {
        this.repo = repo;
        this.to = to;
    }

    /**
     * 判断可重试的异常
     *
     * @param exception 异常
     * @return 是否可重试
     */
    private boolean isRetryableException(Exception exception) {
        return exception.getCause() instanceof TargetNotFoundException
                || exception.getCause() instanceof ClientException
                || exception.getCause() instanceof ClientLocalExecutorNotFoundException
                || exception.getCause() instanceof ServerLocalExecutorNotFoundException;
    }

    private boolean isMaxRetryCount(List<FlowContext<I>> contexts) {
        String toBatch = contexts.get(0).getToBatch();
        return this.repo.isMaxRetryCount(toBatch);
    }

    /**
     * 是否需要重试
     *
     * @param exception 出现的异常
     * @param contexts 异常上下文信息
     * @return 是否需要重试
     */
    public boolean isNeedRetry(Exception exception, List<FlowContext<I>> contexts) {
        return this.isRetryableException(exception) && !isMaxRetryCount(contexts);
    }

    /**
     * 发生错误后，处理contexts上下文
     * 对于可重试异常，更新重试记录表；否则，检索重试记录表，删除之前可能存在的重试记录
     *
     * @param exception 异常
     * @param contexts 需要错误处理的context列表
     */
    public void process(Exception exception, List<FlowContext<I>> contexts) {
        this.repo.saveWithoutPassData(contexts);
        this.repo.updateStatus(contexts, contexts.get(0).getStatus().toString(), contexts.get(0).getPosition());
        if (this.isNeedRetry(exception, contexts)) {
            scheduleRetry(contexts);
        } else {
            Optional.ofNullable(contexts.get(0).getToBatch())
                    .ifPresent(toBatch -> repo.deleteRetryRecord(Collections.singletonList(toBatch)));
        }
    }

    /**
     * 发生错误后，处理contexts上下文，并且发起重试
     *
     * @param exception 异常
     * @param contexts 需要错误处理的context列表
     */
    public void retry(Exception exception, List<FlowContext<I>> contexts) {
        this.process(exception, contexts);
        to.onProcess(contexts);
    }

    private void scheduleRetry(List<FlowContext<I>> contexts) {
        this.repo.saveRetrySchedule(contexts);
    }
}
