/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestHeader;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.dto.FileRspDto;
import modelengine.fit.jober.aipp.dto.FormFileDto;
import modelengine.fit.jober.aipp.dto.GenerateImageDto;
import modelengine.fit.jober.aipp.service.FileService;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import java.io.IOException;
import java.util.List;

/**
 * 文件接口
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "文件相关操作")
public class FileController extends AbstractController {
    private static final Logger log = Logger.get(FileController.class);

    private final OperatorService operatorService;

    private final FileService fileService;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param operatorService 操作服务
     * @param fileService 文件服务
     */
    public FileController(Authenticator authenticator, OperatorService operatorService, FileService fileService) {
        super(authenticator);
        this.operatorService = operatorService;
        this.fileService = fileService;
    }

    /**
     * 从远端下载文件或者从nas下载文件
     *
     * @param httpRequest Http请求
     * @param tenantId 租户ID
     * @param fileCanonicalPath 文件的规范路径
     * @param fileName 文件名
     * @param httpClassicServerResponse Http响应
     * @return 文件实体
     * @throws IOException 文件读取异常
     */
    @GetMapping(path = "/file", description = "下载文件")
    public FileEntity getFile(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "filePath", required = false) String fileCanonicalPath,
            @RequestParam(value = "fileName") String fileName, HttpClassicServerResponse httpClassicServerResponse)
            throws IOException {
        OperationContext context = new OperationContext();
        return this.fileService.getFile(context, fileCanonicalPath, fileName, httpClassicServerResponse);
    }

    /**
     * 上传文件
     *
     * @param httpRequest Http请求
     * @param tenantId 租户ID
     * @param fileName 文件名
     * @param aippId AIPP ID
     * @param receivedFile 接收到的文件
     * @return 文件响应DTO
     * @throws IOException 文件读取异常
     */
    @CarverSpan(value = "operation.file.upload")
    @PostMapping(path = "/file", description = "上传文件")
    public Rsp<FileRspDto> uploadFile(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestHeader(value = "attachment-filename", defaultValue = "blank") @SpanAttr("fileName") String fileName,
            @RequestParam(value = "aipp_id", required = false) String aippId, PartitionedEntity receivedFile) throws IOException {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        List<FileEntity> files = AippFileUtils.getFileEntity(receivedFile);
        if (files.isEmpty()) {
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }
        FileRspDto fileRspDto = this.fileService.uploadFile(context, tenantId, fileName, aippId, files.get(0));
        return Rsp.ok(fileRspDto);
    }

    /**
     * 根据名称和描述生成图片
     *
     * @param httpRequest Http请求
     * @param tenantId 租户ID
     * @param imageDto 图片生成内容
     * @return base64编码的图片字符串
     */
    @CarverSpan(value = "operation.generate.image")
    @PostMapping(value = "/generateImage", description = "根据名称和描述生成图片")
    public Rsp<String> generateImage(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody GenerateImageDto imageDto) {
        return this.fileService.generateImage(imageDto);
    }

    /**
     * 上传表单文件
     *
     * @param httpRequest 表示Http请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户Id的 {@link String}。
     * @param fileName 表示文件名称的 {@link String}。
     * @param receivedFile 表示接收到的文件的 {@link PartitionedEntity}。
     * @return 表示表单文件DTO的 {@link FormFileDto}。
     */
    @PostMapping(path = "/file/smart_form", description = "上传表单文件")
    @CarverSpan(value = "operation.upload.smart.form")
    public Rsp<FormFileDto> uploadSmartForm(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @RequestHeader(value = "attachment-filename", defaultValue = "blank") @SpanAttr("fileName") String fileName,
            PartitionedEntity receivedFile) throws IOException {
        return Rsp.ok(this.fileService.uploadSmartForm(receivedFile, fileName, contextOf(httpRequest, tenantId)));
    }

    /**
     * 获取表单模板
     *
     * @param httpRequest 表示Http请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户Id的 {@link String}。
     * @return 表示表单模板文件的 {@link FileEntity}。
     * @throws IOException 文件读取异常。
     */
    @GetMapping(path = "/file/smart_form/template", description = "下载表单模板")
    public FileEntity getSmartFormTemplate(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId) throws IOException {
        return this.fileService.getSmartFormTemplate(httpRequest, contextOf(httpRequest, tenantId));
    }

    /**
     * 批量上传文件
     *
     * @param httpRequest Http请求
     * @param tenantId 租户ID
     * @param appId APP ID
     * @param receivedFiles 接收到的文件
     * @return 文件响应DTO
     */
    @CarverSpan(value = "operation.file.batch.upload")
    @PostMapping(path = "/files", description = "批量上传文件")
    public Rsp<List<FileRspDto>> batchUploadFile(HttpClassicServerRequest httpRequest,
        @PathVariable("tenant_id") String tenantId, @RequestParam(value = "app_id", required = false) String appId,
        PartitionedEntity receivedFiles) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        List<FileRspDto> fileRspDtos = AippFileUtils.getFileEntity(receivedFiles)
            .stream()
            .map(
                fileEntity -> {
                    try {
                        return this.fileService.uploadFile(context, tenantId, fileEntity.filename(), appId, fileEntity);
                    } catch (IOException e) {
                        throw new AippException(AippErrCode.UPLOAD_FAILED);
                    }
                })
            .toList();
        return Rsp.ok(fileRspDtos);
    }
}
