/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.impl;

import static modelengine.fit.jober.aipp.constant.AippConstant.NAS_SHARE_DIR;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.dto.audio.AudioSplitInfo;
import modelengine.fit.jober.aipp.dto.audio.SummaryDto;
import modelengine.fit.jober.aipp.dto.audio.SummarySection;
import modelengine.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;
import modelengine.fit.jober.aipp.enums.LlmModelNameEnum;
import modelengine.fit.jober.aipp.service.FfmpegService;
import modelengine.fit.jober.aipp.tool.FileExtractor;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.LLMUtils;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.jade.voice.service.VoiceService;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LlmAudio2Summary
 *
 * @author 易文渊
 * @since 2024/1/8
 */
@Component
public class AudioExtractor implements FileExtractor {
    private static final Logger log = Logger.get(AudioExtractor.class);

    private static final String PROMPT = "\nPerform the following actions:\n"
            + "1. - Use chinese summarize the following video delimited by <> limit in 100 words.\n"
            + "2. - Write a title for the summary.\n"
            + "3. - Output a json object that contains the following keys: title, text.\n\n" + "EXAMPLE\n"
            + "Video: <文本摘要旨在将文本或文本集合转换为包含关键信息的简短摘要...>\n" + "Output JSON:\n"
            + "{\"title\": \"文本摘要简介\", \"text\": \"文本摘要...\"}\n\n" + "--------\n" + "Video: <%s>\n"
            + "Output JSON:\n";

    private static final String TMP_DIR_PREFIX = "audioTmp-";

    private static final ExecutorService SUMMARY_EXECUTOR =
            new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    private final ChatModel openAiClient;
    private final VoiceService voiceService;
    private final String endpoint;
    private final String pathPrefix;
    private final FfmpegService ffmpegService;

    public AudioExtractor(FfmpegService ffmpegService, @Fit ChatModel openAiClient,
                          @Fit VoiceService voiceService, @Value("${app-engine.endpoint}") String endpoint,
                          @Value("${app-engine.pathPrefix}") String pathPrefix) {
        this.ffmpegService = ffmpegService;
        this.openAiClient = openAiClient;
        this.voiceService = voiceService;
        this.endpoint = endpoint;
        this.pathPrefix = pathPrefix;
    }

    private SummaryDto batchSummary(List<File> audioList, int segmentSize) throws InterruptedException, IOException {
        int taskCnt = audioList.size();
        List<String> output = new ArrayList<>(Collections.nCopies(taskCnt, null));
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(taskCnt);
        for (int i = 0; i < taskCnt; ++i) {
            int id = i;
            SUMMARY_EXECUTOR.execute(() -> {
                try {
                    File audio = audioList.get(id);
                    String audioPath = AippFileUtils.getFileDownloadFilePath(
                            endpoint, this.pathPrefix, audio.getPath());
                    log.info("audio filePath: {}, audio fileName: {}", audioPath, audio.getName());
                    String text = voiceService.getText(audioPath + "&fileName=" + audio.getName());
                    String summary = LLMUtils.askModelForSummary(openAiClient, String.format(PROMPT, text),
                            LlmModelNameEnum.QWEN_72B, 16000);
                    output.set(id, summary);
                } catch (IOException e) {
                    output.set(id, "");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        SummaryDto summaryDto = generateSummary(output, segmentSize);
        long endTime = System.currentTimeMillis();
        log.info("Summarize {} task use time {} seconds, segment size: {} seconds.",
                taskCnt,
                (endTime - startTime) / 1000,
                segmentSize);
        return summaryDto;
    }

    private SummaryDto generateSummary(List<String> output, int segmentSize) {
        SummaryDto summaryDto = new SummaryDto(output, segmentSize);
        StringBuilder sb = new StringBuilder();
        summaryDto.getSectionList().forEach(sec -> sb.append(sec.getText()));
        try {
            String llmOutput = LLMUtils.askModelForSummary(openAiClient, String.format(PROMPT, sb),
                    LlmModelNameEnum.QWEN_72B, 16000);
            SummarySection section =
                    JsonUtils.parseObject(LLMUtils.tryFixLlmJsonString(llmOutput), SummarySection.class);
            summaryDto.setSummary(section.getText());
        } catch (IOException e) {
            log.error("Llm generate unexpect rsp, msg: {}.", e);
            summaryDto.setSummary("");
        }
        return summaryDto;
    }

    private AudioSplitInfo covertAudio(String dirName, File audio) throws IOException {
        File targetDir = Paths.get(NAS_SHARE_DIR, dirName).toFile();
        FfmpegMeta meta = ffmpegService.stat(audio.getCanonicalPath());
        FileUtils.copyFile(audio, Paths.get(targetDir.getPath(), audio.getName()).toFile());
        File copyAudio = Paths.get(targetDir.getPath(), audio.getName()).toFile();
        if (meta.getDuration() >= 6 * 60) {
            int segmentCount = Math.max(1, Math.min(meta.getDuration() / 300, 8));
            int segmentSize = (meta.getDuration() + segmentCount - 1) / segmentCount;
            ffmpegService.splitAudio(audio.getCanonicalPath(),
                    targetDir.getCanonicalPath() + "/split_%03d." + meta.getVideoExt(),
                    segmentSize);
            FileUtils.delete(copyAudio);
            return new AudioSplitInfo(targetDir.getCanonicalPath(), segmentSize);
        }
        return new AudioSplitInfo(targetDir.getCanonicalPath(), meta.getDuration());
    }

    private AudioSplitInfo covertAudioSimple(String dirName, File audio) throws IOException {
        File targetDir = Paths.get(NAS_SHARE_DIR, dirName).toFile();
        FileUtils.copyFile(audio, Paths.get(targetDir.getPath(), audio.getName()).toFile());
        return new AudioSplitInfo(targetDir.getCanonicalPath(), 0);
    }

    @Fitable("llmAudio2Summary")
    @Override
    public String extractFile(File file) {
        // file -> audioDir 切分为多个音频文件，存在临时目录下
        String tmpDir = TMP_DIR_PREFIX + UUIDUtil.uuid();
        AudioSplitInfo audioSplitInfo;
        try {
            audioSplitInfo = this.covertAudioSimple(tmpDir, file);
        } catch (IOException e) {
            log.error("error occurs during audio segmentation.");
            throw new AippException(AippErrCode.AUDIO_SEGMENTATION_FAILED);
        }

        try (Stream<Path> audioPathStream = Files.list(Paths.get(audioSplitInfo.getDirPath()))) {
            List<File> audioFiles = audioPathStream.map(Path::toFile).collect(Collectors.toList());
            SummaryDto summaryDto = batchSummary(audioFiles, audioSplitInfo.getSegmentSize());
            if (summaryDto.getSectionList().isEmpty()) {
                log.error("audio summary result is empty.");
                throw new AippException(AippErrCode.AUDIO_SUMMARY_EMPTY);
            }
            return summaryDto.getSummary();
        } catch (InterruptedException | IOException e) {
            throw new AippException(AippErrCode.AUDIO_CONTENT_EXTRACT_FAILED);
        } finally {
            // 删除临时目录
            try {
                FileUtils.deleteDirectory(Paths.get(NAS_SHARE_DIR, tmpDir).toFile());
            } catch (IOException e) {
                log.error("delete audio file tmp directory failed.");
            }
        }
    }
}