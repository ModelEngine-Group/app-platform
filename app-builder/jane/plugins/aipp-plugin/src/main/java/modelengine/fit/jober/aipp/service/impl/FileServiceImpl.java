/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.constant.AippConstant.NAS_SHARE_DIR;
import static modelengine.fit.jober.aipp.entity.FileExtensionEnum.getFileExtension;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.thread.StreamConsumer;
import modelengine.fit.jober.aipp.config.FormFileUploadConfig;
import modelengine.fit.jober.aipp.dto.FileRspDto;
import modelengine.fit.jober.aipp.dto.FormFileDto;
import modelengine.fit.jober.aipp.dto.GenerateImageDto;
import modelengine.fit.jober.aipp.service.FileService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.HttpUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.validation.FormFileValidator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.CustomResourceHandler;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.UuidUtils;
import modelengine.fitframework.util.support.Unzip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * FileService 接口的实现类。
 *
 * @author 陈潇文
 * @since 2024/11/18
 */
@Component
public class FileServiceImpl implements FileService, CustomResourceHandler {
    private static final Logger log = Logger.get(FileServiceImpl.class);
    private static final String CONFIG_JSON = "config.json";
    private static final String BUILD = "build";
    private static final String SCHEMA = "schema";
    private static final List<String> IMAGE_TYPE = new ArrayList<>(Arrays.asList("form.jpg", "form.png", "form.jpeg"));
    private static final String FORM_IMAGE = "form image";
    private static final String INDEX_HTML = "index.html";
    private static final String TEMPLATE_ZIP = "template.zip";
    private static final long UNZIP_MAX_SIZE = 0x5FFFFFL;
    private static final long FILE_MAX_COUNT = 1024L;

    private final FormFileValidator formFileValidator;
    private final UploadedFileManageService uploadedFileManageService;
    private final FormFileUploadConfig formFileUploadConfig;
    private final String formFullTemporaryPath;
    private final String formFullPath;
    private final String pathPrefix;
    private final String groupName;
    private final String resourceUrlPrefix;

    private final HttpClassicClientFactory httpClassicClientFactory;

    private final String imageGenModelUrl;

    private final String imageGenModel;

    public FileServiceImpl(HttpClassicClientFactory httpClassicClientFactory,
            @Value("${model.imageGen.share_url}") String imageGenModelUrl,
            @Value("${model.imageGen.model}") String imageGenModel, FormFileValidator formFileValidator,
            UploadedFileManageService uploadedFileManageService, FormFileUploadConfig formFileUploadConfig,
            @Value("${app-engine.form.path-prefix}") String pathPrefix,
            @Value("${app-engine.form.temporary-path}") String temporaryPath,
            @Value("${app-engine.form.group-name}") String groupName,
            @Value("${app-engine.form.path}") String formPath,
            @Value("${app-engine.resource.url-prefix}") String resourceUrlPrefix) {
        this.httpClassicClientFactory = httpClassicClientFactory;
        this.imageGenModelUrl = imageGenModelUrl;
        this.imageGenModel = imageGenModel;
        this.formFileValidator = formFileValidator;
        this.uploadedFileManageService = uploadedFileManageService;
        this.formFileUploadConfig = formFileUploadConfig;
        this.pathPrefix = pathPrefix;
        this.formFullPath = pathPrefix + formPath;
        this.formFullTemporaryPath = pathPrefix + temporaryPath;
        this.groupName = groupName;
        this.resourceUrlPrefix = resourceUrlPrefix;
    }

    @Override
    public Rsp<String> generateImage(GenerateImageDto imageDto) {
        log.info("Start generate image.");
        HttpClassicClientRequest request =
                httpClassicClientFactory.create().createRequest(HttpRequestMethod.POST, imageGenModelUrl);
        Map<String, String> requestData = new HashMap<>();
        requestData.put("model", imageGenModel);
        requestData.put("size", imageDto.getSize());
        requestData.put("prompt", generateImagePrompt(imageDto.getName(), imageDto.getDescription()));
        request.jsonEntity(requestData);
        try (HttpClassicClientResponse<Object> response = HttpUtils.execute(request)) {
            if (HttpResponseStatus.OK.statusCode() != response.statusCode()) {
                log.error("Generate image error, response code: {}, message: {}.",
                        response.statusCode(),
                        response.reasonPhrase());
                throw new AippException(AippErrCode.GENERATE_IMAGE_FAILED);
            }
            if (!response.objectEntity().isPresent()) {
                log.error("Generate image error, result is empty.");
                throw new AippException(AippErrCode.GENERATE_IMAGE_FAILED);
            }
            Map<String, Object> responseData =
                    JsonUtils.parseObject(JsonUtils.toJsonString(response.objectEntity().get().object()));
            List<Object> data = ObjectUtils.cast(responseData.get("data"));
            Map<String, Object> dataMap = ObjectUtils.cast(data.get(0));
            return Rsp.ok(dataMap.get("b64_json").toString());
        } catch (IOException e) {
            log.error("Generate image error, error message:{}.", e.getMessage());
            throw new AippException(AippErrCode.GENERATE_IMAGE_FAILED);
        }
    }

    private String generateImagePrompt(String imageName, String description) {
        return String.format("根据以下信息生成一张图片：\n\n" + "- **图片名称**：%s\n" + "- **描述**：%s\n\n"
                        + "图片应当直观地反映名称和描述中的内容，捕捉描述中的主题、氛围以及关键元素。"
                        + "图像的构图、色彩和风格应与描述中的主要概念一致。请发挥创意，确保生成的图片能够生动呈现名称和描述中的画面感。",
                imageName,
                description);
    }

    @Override
    public FileEntity getFile(OperationContext context, String fileCanonicalPath, String fileName,
            HttpClassicServerResponse httpClassicServerResponse) throws IOException {
        if (StringUtils.isNotBlank(fileCanonicalPath)) {
            String operator = context.getOperator();
            if (!fileCanonicalPath.startsWith(NAS_SHARE_DIR) || fileCanonicalPath.contains("..")) {
                log.error("Download file error: invalid file path, fileCanonicalPath={}.", fileCanonicalPath);
                throw new AippException(AippErrCode.INVALID_FILE_PATH);
            }
            log.info("Download file: operator={}, fileCanonicalPath={}.", operator, fileCanonicalPath);
            Path filePath = Paths.get(fileCanonicalPath);
            if (!filePath.toFile().exists()) {
                throw new AippException(context, AippErrCode.FILE_EXPIRED_OR_BROKEN);
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(filePath));
            return FileEntity.createAttachment(httpClassicServerResponse,
                    fileName,
                    byteArrayInputStream,
                    byteArrayInputStream.available());
        } else {
            throw new IllegalArgumentException("FileCanonicalPath is empty");
        }
    }

    @Override
    public FileRspDto uploadFile(OperationContext context, String tenantId, String fileName, String aippId,
            FileEntity receivedFile) throws IOException{
        String uniqueFileName = generateUniqueFileName(fileName);
        log.info("upload file fileName={} uniqueFileName={}", fileName, uniqueFileName);
        File targetFile = Paths.get(NAS_SHARE_DIR, uniqueFileName).toFile();

        try (InputStream inStream = receivedFile.getInputStream()) {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inStream, targetFile);
            uploadedFileManageService.addFileRecord(aippId, context.getAccount(), targetFile.getCanonicalPath(),
                    Entities.generateId());
        } catch (IOException e) {
            Path fileToDeletePath = Paths.get(targetFile.getPath());
            if (Files.exists(fileToDeletePath)) {
                try {
                    Files.delete(fileToDeletePath);
                } catch (IOException e1) {
                    log.error("Failed to delete file.", e1);
                }
            }
            log.error("write file={} fail.", fileName, e);
            // 待添加 context 入参， 否则国际化未生效
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }

        log.info("upload file fileName={} uniqueFileName={} success.", fileName, uniqueFileName);
        return FileRspDto.builder()
                .fileName(fileName)
                .filePath(targetFile.getCanonicalPath())
                .fileType(getFileExtension(fileName))
                .build();
    }

    private String generateUniqueFileName(String fileName) {
        return UUID.randomUUID() + "." + getFileExtension(fileName);
    }

    @Override
    public FormFileDto uploadSmartForm(PartitionedEntity receivedFile, String fileName, OperationContext context)
            throws IOException {
        if (!fileName.endsWith(".zip")) {
            throw new AippException(AippErrCode.UPLOADED_FORM_FILE_FORMAT_ERROR);
        }
        String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
        String uniqueFileName = generateUniqueFileName(decodedFileName);
        log.info("upload file fileName={} uniqueFileName={}", decodedFileName, uniqueFileName);
        List<NamedEntity> entities =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (entities.isEmpty()) {
            throw new AippException(AippErrCode.NO_FILE_UPLOAD_ERROR);
        }
        this.validateFormConstraintInfo();
        File tempDir;
        if (StringUtils.isNotBlank(this.groupName)) {
            tempDir = this.unZipFormFile(uniqueFileName, this.formFullTemporaryPath, this.formFullPath, entities);
            this.setNewGroupName();
        } else {
            tempDir = this.unZipFormFile(uniqueFileName, this.formFullTemporaryPath, this.formFullPath, entities);
        }

        File[] files = this.getFiles(tempDir);
        try {
            this.validateForm(files);
        } catch (AippException e) {
            FileUtils.delete(tempDir);
            throw e;
        }
        Map<String, Object> schema = this.getSchema(this.getFile(files, CONFIG_JSON));
        String tempDirPath = tempDir.toURI().getPath();
        return this.saveMetaData(this.removePrefix(tempDirPath),
                this.getFile(files, FORM_IMAGE).getName(),
                cast(schema.get(SCHEMA)),
                decodedFileName,
                context);
    }

    /**
     * 针对 windows 路径：/D:/xxx/smart_form/e00d2b9c-da61-4763-97a3-c83341d29bc1/
     * 针对 linux 路径：/xxx/smart_form/e00d2b9c-da61-4763-97a3-c83341d29bc1/
     * 前缀为 /xxx
     * 去除前缀后，同为 /smart_form/e00d2b9c-da61-4763-97a3-c83341d29bc1/
     *
     * @param path 需要去除的路径
     * @return 去除前缀后的路径
     */
    private String removePrefix(String path) {
        String tmpPath = path;
        if (tmpPath.startsWith("/") && tmpPath.length() > 2 && tmpPath.charAt(2) == ':') {
            tmpPath = tmpPath.substring(3);
        }
        if (tmpPath.startsWith(this.pathPrefix)) {
            return tmpPath.substring(this.pathPrefix.length());
        }
        return tmpPath;
    }

    private void setNewGroupName() throws IOException {
        Process changeGroupProcess = new ProcessBuilder("chgrp", "-R", this.groupName, this.formFullPath).start();
        try {
            handleProcessOutputAndWait(changeGroupProcess);
        } catch (InterruptedException e) {
            log.error("Failed to get original group");
            throw new AippException(AippErrCode.SAVE_FORM_FILE_FAILED);
        }
    }

    private static void handleProcessOutputAndWait(Process changeGroupProcess) throws InterruptedException {
        StreamConsumer errConsumer = new StreamConsumer(changeGroupProcess.getErrorStream());
        StreamConsumer outputConsumer = new StreamConsumer(changeGroupProcess.getInputStream());
        errConsumer.start();
        outputConsumer.start();
        changeGroupProcess.waitFor();
        errConsumer.join();
        outputConsumer.join();
    }

    @Override
    public FileEntity getSmartFormTemplate(HttpClassicServerRequest httpRequest, OperationContext context)
            throws IOException {
        String operator = context.getOperator();
        String templatePath = this.formFullPath + "/" + TEMPLATE_ZIP;
        log.info("Download form template file: operator={}.", operator);
        Path path = Paths.get(templatePath);
        if (!path.toFile().exists()) {
            throw new AippException(AippErrCode.FILE_EXPIRED_OR_BROKEN);
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(path));
        return FileEntity.createAttachment(httpRequest,
                TEMPLATE_ZIP,
                byteArrayInputStream,
                byteArrayInputStream.available());
    }

    @Override
    public FileEntity handle(String positionName, HttpClassicServerRequest request,
            HttpClassicServerResponse response) {
        String requestPath = request.path();
        int urlPathPrefixIndex = requestPath.indexOf(this.resourceUrlPrefix);
        if (urlPathPrefixIndex == -1 || requestPath.contains("..")) {
            log.error("Url is invalid. Url={}", requestPath);
            throw new IllegalArgumentException(requestPath);
        }
        String formPath = requestPath.substring(urlPathPrefixIndex + this.resourceUrlPrefix.length());
        String handledFormFullPath = this.getFormFullPath(formPath);
        Path path = Paths.get(handledFormFullPath);
        if (!path.toFile().exists()) {
            throw new AippException(AippErrCode.FILE_EXPIRED_OR_BROKEN);
        }
        ByteArrayInputStream byteArrayInputStream;
        try {
            byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
        int index = formPath.lastIndexOf("/");
        if (index == -1) {
            log.error("Url is incorrect. Url={}", request.path());
            throw new IllegalArgumentException(request.path());
        }
        return FileEntity.createInline(request,
                formPath.substring(index + 1),
                byteArrayInputStream,
                byteArrayInputStream.available());
    }

    @Override
    public boolean canHandle(String positionName, HttpClassicServerRequest request) {
        int urlPathPrefixIndex = request.path().indexOf(this.resourceUrlPrefix);
        return urlPathPrefixIndex != -1;
    }

    private String getFormFullPath(String formPath) {
        return this.pathPrefix + formPath;
    }

    private File unZipFormFile(String uniqueFileName, String fromTemporaryPath, String formPath,
            List<NamedEntity> entities) {
        FileEntity file = entities.get(0).asFile();
        File targetTemporaryFile = Paths.get(fromTemporaryPath, UuidUtils.randomUuidString(), uniqueFileName).toFile();
        try {
            this.storeFormFile(uniqueFileName, file, targetTemporaryFile);
        } catch (AippException e) {
            log.error("Failed to unzip form file, msg:{}", e.getMessage());
            log.error("Failed to unzip form file, msg:{} ", e);
            throw new AippException(AippErrCode.SAVE_FORM_FILE_FAILED);
        }
        log.info("Save the file to the temporary file directory. [fileName={}]", uniqueFileName);
        File tempDir = new File(formPath, uniqueFileName.split("\\.")[0]);
        try {
            // 校验zip文件最大为5M
            Unzip.Security security = new Unzip.Security(UNZIP_MAX_SIZE, FILE_MAX_COUNT, false);
            FileUtils.unzip(targetTemporaryFile, Charset.defaultCharset()).secure(security).target(tempDir).start();
        } catch (SecurityException e) {
            throw new AippException(AippErrCode.FORM_FILE_MAX_SIZE_EXCEED);
        } catch (IOException e) {
            log.error("Failed to unzip plugin file. [file={}]", uniqueFileName);
            throw new AippException(AippErrCode.SAVE_FORM_FILE_FAILED);
        }
        FileUtils.delete(targetTemporaryFile.getParentFile());
        return tempDir;
    }

    private void validateFormConstraintInfo() {
        try {
            FileStore fileStore = Files.getFileStore(Paths.get(this.formFullPath));
            long totalSpace = fileStore.getTotalSpace();
            long usedSpace = totalSpace - fileStore.getUsableSpace();
            double usedStorageRatio = (double) usedSpace / (double) totalSpace;
            log.info("the intelligent forms used storage ratio is {}.", usedStorageRatio);
            if (usedStorageRatio > this.formFileUploadConfig.getMaxStorageRatio()) {
                throw new AippException(AippErrCode.STORAGE_RATIO_UP_TO_MAXIMUM,
                        this.formFileUploadConfig.getMaxStorageRatio() * 100);
            }
        } catch (IOException e) {
            log.error("get system physical storage info failed.");
            throw new AippException(AippErrCode.VALIDATE_FORM_CONSTRAINT_FAILED);
        }
    }

    private void storeFormFile(String fileName, FileEntity file, File targetFile) {
        log.info("fileName:{}, targetFile path:{}, targetFile name:{}",
                fileName,
                targetFile.getPath(),
                targetFile.getName());
        File targetDirectory = targetFile.getParentFile();
        try {
            FileUtils.ensureDirectory(targetDirectory);
        } catch (IOException e) {
            log.error("Failed to ensureDirectory when store form file. [msg={}, fileName={}]",
                    e.getMessage(),
                    fileName);
            log.error("Failed to ensureDirectory when store form file", e);
            throw new AippException(AippErrCode.ENSURE_FORM_DIRECTORY_FAILED);
        }
        try (InputStream inStream = file.getInputStream();
             OutputStream outStream = Files.newOutputStream(targetFile.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("Failed to read data when store form file. [msg={}, fileName={}]", e.getMessage(), fileName);
            log.error("Failed to read data when store form file", e);
            FileUtils.delete(targetFile.getPath());
            throw new AippException(AippErrCode.WRITE_FORM_FILE_FAILED);
        }
    }

    private void validateForm(File[] files) {
        if (files.length > 3) {
            throw new AippException(AippErrCode.CONTAIN_EXTRA_FILE);
        }
        List<String> missingFileNames = this.checkFiles(files);
        if (!missingFileNames.isEmpty()) {
            throw new AippException(AippErrCode.FORM_FILE_MISSING, String.join(",", missingFileNames));
        }
        this.formFileValidator.validateComponent(this.getFile(files, BUILD));
        this.formFileValidator.validateImg(this.getFile(files, FORM_IMAGE));
        this.formFileValidator.validateSchema(this.getSchema(this.getFile(files, CONFIG_JSON)));
    }

    private File[] getFiles(File tempDir) {
        File[] files = tempDir.listFiles();
        if (files == null || files.length == 0) {
            throw new AippException(AippErrCode.FORM_FILE_IS_EMPTY);
        }
        return files;
    }

    private List<String> checkFiles(File[] files) {
        List<String> fileNames =
                Arrays.stream(files).filter(File::isFile).map(File::getName).collect(Collectors.toList());
        List<String> directoryNames =
                Arrays.stream(files).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        List<String> missingFileNames = new ArrayList<>();
        if (fileNames.stream().noneMatch(CONFIG_JSON::equals)) {
            missingFileNames.add(CONFIG_JSON);
        }

        if (fileNames.stream().noneMatch(IMAGE_TYPE::contains)) {
            missingFileNames.add(FORM_IMAGE);
        }
        long imgFileCount = IMAGE_TYPE.stream().filter(fileNames::contains).count();
        if (imgFileCount > 1) {
            throw new AippException(AippErrCode.FORM_IMG_FILE_COUNT_ERROR);
        }

        if (directoryNames.stream().noneMatch(BUILD::equals)) {
            missingFileNames.add(BUILD);
        }
        return missingFileNames;
    }

    private File getFile(File[] files, String fileName) {
        if (Objects.equals(fileName, FORM_IMAGE)) {
            return Arrays.stream(files)
                    .filter(file -> IMAGE_TYPE.contains(file.getName()))
                    .collect(Collectors.toList())
                    .get(0);
        }
        if (Objects.equals(fileName, CONFIG_JSON)) {
            return Arrays.stream(files)
                    .filter(file -> CONFIG_JSON.equals(file.getName()))
                    .collect(Collectors.toList())
                    .get(0);
        }
        if (Objects.equals(fileName, BUILD)) {
            return Arrays.stream(files)
                    .filter(file -> file.isDirectory() && file.getName().equals(BUILD))
                    .collect(Collectors.toList())
                    .get(0);
        }
        throw new AippException(AippErrCode.FORM_FILE_MISSING, fileName);
    }

    private FormFileDto saveMetaData(String filePath, String imgFileName, Map<String, Object> schema, String fileName,
            OperationContext context) {
        String fileUuid = Entities.generateId();
        this.uploadedFileManageService.addFileRecord("", context.getAccount(), filePath, fileUuid);
        String imgUrl = filePath + imgFileName;
        String iframeUrl = filePath + BUILD + "/" + INDEX_HTML;
        return FormFileDto.builder()
                .imgUrl(imgUrl)
                .iframeUrl(iframeUrl)
                .fileUuid(fileUuid)
                .schema(schema)
                .fileName(fileName)
                .build();
    }

    private Map<String, Object> getSchema(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new AippException(AippErrCode.FORM_SCHEMA_JSON_FORMAT_ERROR);
        }
    }
}
