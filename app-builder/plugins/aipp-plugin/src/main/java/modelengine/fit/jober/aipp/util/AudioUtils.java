/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.common.AudioTextFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 音频操作工具
 *
 * @author 刘信宏
 * @since 2024/1/19
 */
public class AudioUtils {
    /**
     * 并行提取音频文本
     *
     * @param executor 线程执行器
     * @param audioList 音频文件列表
     * @param extractor 提取器
     * @return 文本列表
     * @throws InterruptedException 线程中断异常
     */
    public static List<String> extractAudioTextParallel(ExecutorService executor, List<File> audioList,
            AudioTextFunction<File, String> extractor) throws InterruptedException {
        int taskCnt = audioList.size();
        List<String> output = new ArrayList<>(Collections.nCopies(taskCnt, null));
        CountDownLatch countDownLatch = new CountDownLatch(taskCnt);
        for (int i = 0; i < taskCnt; ++i) {
            int id = i;
            executor.execute(() -> {
                try {
                    String text = extractor.apply(audioList.get(id));
                    output.set(id, text);
                } catch (IOException e) {
                    output.set(id, "");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        return output;
    }
}
