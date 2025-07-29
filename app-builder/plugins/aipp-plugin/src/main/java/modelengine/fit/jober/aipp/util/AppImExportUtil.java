/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.IMPORT_CONFIG_FIELD_ERROR;
import static modelengine.fit.jober.aipp.constant.AippConstant.NAS_SHARE_DIR;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippJsonDecodeException;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.dto.export.AppExportConfig;
import modelengine.fit.jober.aipp.dto.export.AppExportConfigProperty;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;
import modelengine.fit.jober.aipp.dto.export.AppExportForm;
import modelengine.fit.jober.aipp.dto.export.AppExportFormProperty;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.FormPropertyTypeEnum;

import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应用导入导出工具类。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
public class AppImExportUtil {
    private static final char FILE_EXTENSION_DELIM = '.';
    private static final String RENAME_PATTERN = "-副本([1-9]\\d*)?";
    private static final String RENAME_FORMATTER = "{0}-副本{1}";
    private static final Pattern TENANT_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+");
    private static final String ICON_URL_PATTERN = "/v1/api/{0}/file?filePath={1}&fileName={2}";
    private static final String[] FORM_PROPERTY_GROUP_SET =
            new String[]{"null", "ability", "basic", "workflow", "chat"};
    private static final String[] FORM_PROPERTY_FROM_SET = new String[]{"graph", "input", "none"};
    private static final int MAX_NAME_LENGTH = 64;
    private static final String[] LEGAL_ICON_TYPE = {"jpg", "jpeg", "png", "gif"};
    private static final String[] APP_BUILT_TYPE_SET = {"basic", "workflow"};
    private static final String[] APP_CATEGORY_SET = {"chatbot", "workflow", "agent"};
    private static final String[] APP_TYPE_SET = {"app", "waterflow", "template"};
    private static final String[] APP_ATTR_DEFAULT_SET = {"icon", "description"};

    /**
     * 将 {@link AppBuilderApp} 转换为 {@link AppExportApp}。
     *
     * @param appBuilderApp 表示应用基本信息的 {@link AppBuilderApp}，来自数据库表 app_builder_app。
     * @return 表示应用导出基本信息的 {@link AppExportApp}。
     */
    public static AppExportApp convertToAppExportApp(AppBuilderApp appBuilderApp) {
        return AppExportApp.builder()
                .name(appBuilderApp.getName())
                .tenantId(appBuilderApp.getTenantId())
                .type(appBuilderApp.getType())
                .appBuiltType(appBuilderApp.getAppBuiltType())
                .version(appBuilderApp.getVersion())
                .attributes(appBuilderApp.getAttributes())
                .appBuiltType(appBuilderApp.getAppBuiltType())
                .appCategory(appBuilderApp.getAppCategory())
                .appType(appBuilderApp.getAppType())
                .build();
    }

    /**
     * 将应用 configUI 配置和对应的表单属性导出为应用导出的 config 配置。
     *
     * @param appBuilderConfig 表示应用 configUI 配置的 {@link AppBuilderConfig}。
     * @return 表示应用导出 config 配置的 {@link AppExportConfig}。
     */
    public static AppExportConfig convertToAppExportConfig(AppBuilderConfig appBuilderConfig) {
        List<AppBuilderConfigProperty> configProperties = appBuilderConfig.getConfigProperties();
        AppBuilderForm appBuilderForm = appBuilderConfig.getForm();
        List<AppExportConfigProperty> exportProperties = configProperties.stream()
                .map(configProperty -> convertToAppExportConfigProperty(configProperty, appBuilderConfig.getAppVersion()))
                .filter(appExportConfigProperty -> appExportConfigProperty.getFormProperty() != null)
                .collect(Collectors.toList());
        AppExportForm exportForm = AppExportForm.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .appearance(appBuilderForm.getAppearance())
                .type(appBuilderForm.getType())
                .formSuiteId(appBuilderForm.getFormSuiteId())
                .version(appBuilderForm.getVersion())
                .build();
        return AppExportConfig.builder().form(exportForm).configProperties(exportProperties).build();
    }

    /**
     * 将应用 configUI 配置属性导出为应用导出的 config 配置属性。
     *
     * @param configProperty 表示应用 configUI 配置的 {@link AppBuilderConfigProperty}。
     * @param appVersion 应用版本。
     * @return 表示应用导出 config 配置的 {@link AppExportConfig}。
     */
    public static AppExportConfigProperty convertToAppExportConfigProperty(AppBuilderConfigProperty configProperty,
            AppVersion appVersion) {
        return AppExportConfigProperty.builder()
                .nodeId(configProperty.getNodeId())
                .formProperty(
                        convertToAppExportFormProperty(appVersion.getFormProperty(configProperty.getFormPropertyId())))
                .build();
    }

    /**
     * 将应用 configUI 表单属性导出为应用导出配置的表单属性。
     *
     * @param appBuilderFormProperty 表示应用 configUI 表单属性的 {@link AppBuilderFormProperty}。
     * @return 表示应用导出配置表单属性的 {@link AppExportFormProperty}。
     */
    public static AppExportFormProperty convertToAppExportFormProperty(AppBuilderFormProperty appBuilderFormProperty) {
        if (appBuilderFormProperty == null) {
            return null;
        }
        return AppExportFormProperty.builder()
                .name(appBuilderFormProperty.getName())
                .dataType(appBuilderFormProperty.getDataType())
                .defaultValue(JsonUtils.toJsonString(appBuilderFormProperty.getDefaultValue()))
                .from(appBuilderFormProperty.getFrom())
                .group(appBuilderFormProperty.getGroup())
                .description(appBuilderFormProperty.getDescription())
                .index(appBuilderFormProperty.getIndex())
                .build();
    }

    /**
     * 将应用流程编排配置导出为应用导出流程配置。
     *
     * @param appBuilderFlowGraph 表示应用流程编排设置的 {@link AppBuilderFlowGraph}。
     * @return 表示应用导出流程编排配置的 {@link AppExportFlowGraph}。
     */
    public static AppExportFlowGraph convertToAppExportFlowGraph(AppBuilderFlowGraph appBuilderFlowGraph) {
        return AppExportFlowGraph.builder()
                .name(appBuilderFlowGraph.getName())
                .appearance(appBuilderFlowGraph.getAppearance())
                .build();
    }

    /**
     * 解析头像文件的后缀。
     *
     * @param iconFileName 表示头像文件文件名的 {@link String}。
     * @return 表示应用头像文件后缀的 {@link String}。
     */
    public static String extractIconExtension(String iconFileName) {
        int idx = iconFileName.lastIndexOf(FILE_EXTENSION_DELIM);
        if (idx == -1) {
            return StringUtils.EMPTY;
        }
        return iconFileName.substring(idx + 1);
    }

    /**
     * 读取文件所有字节。
     *
     * @param inputStream 表示文件输入流的 {@link InputStream}。
     * @return 表示文件所有字节的 {@code byte[]}。
     * @throws IOException 流异常。
     */
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        try (BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    /**
     * 校验导入的应用的配置。
     *
     * @param config 表示导入的应用配置的 {@link AppExportDto}。
     * @throws AippException 配置异常时抛出。
     */
    public static void checkAppExportDto(AppExportDto config) {
        checkAppExportApp(config.getApp());
        checkAppExportConfig(config.getConfig());
        checkAppExportFlowGraph(config.getFlowGraph());
    }

    private static void checkAppExportApp(AppExportApp app) {
        if (app == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app");
        }
        if (StringUtils.isBlank(app.getName())) {
            app.setName(Entities.generateId());
        }
        if (app.getName().length() > MAX_NAME_LENGTH) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.name");
        }
        if (!TENANT_PATTERN.matcher(app.getTenantId()).matches()) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.tenantId");
        }
        if (Stream.of(APP_TYPE_SET).noneMatch(type -> StringUtils.equals(type, app.getType()))) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.type");
        }
        if (app.getVersion() == null || !VERSION_PATTERN.matcher(app.getVersion()).matches()) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.version");
        }
        if (app.getAttributes() == null) {
            app.setAttributes(MapBuilder.<String, Object>get().build());
        }
        Stream.of("icon", "app_type", "greeting", "description")
                .forEach(field -> app.getAttributes().putIfAbsent(field, ""));
        if (Stream.of(APP_BUILT_TYPE_SET).noneMatch(type -> StringUtils.equals(type, app.getAppBuiltType()))) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.appBuiltType");
        }
        if (Stream.of(APP_CATEGORY_SET).noneMatch(type -> StringUtils.equals(app.getAppCategory(), type))) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.appCategory");
        }
        if (StringUtils.isBlank(app.getAppType())) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "app.appType");
        }
    }

    private static void checkAppExportConfig(AppExportConfig config) {
        if (config == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "config");
        }
        checkAppExportForm(config.getForm());
        if (config.getConfigProperties() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "config.configProperties");
        }
        for (AppExportConfigProperty configProperty : config.getConfigProperties()) {
            if (StringUtils.isNotBlank(configProperty.getNodeId()) && configProperty.getNodeId().length() > 255) {
                throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "configProperty.nodeId");
            }
            checkAppExportFormProperty(configProperty.getFormProperty());
        }
    }

    private static void checkAppExportForm(AppExportForm form) {
        if (form == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form");
        }
        if (form.getId() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form.id");
        }
        if (form.getName() == null) {
            form.setName("llm_config");
        }
        if (form.getName().length() > MAX_NAME_LENGTH) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form.name");
        }
        if (form.getAppearance() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form.appearance");
        }
        if (Stream.of("component").noneMatch(type -> StringUtils.equals(type, form.getType()))) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form.type");
        }
        if (form.getFormSuiteId() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form.formSuiteId");
        }
        if (form.getVersion() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "form.version");
        }
    }

    private static void checkAppExportFormProperty(AppExportFormProperty formProperty) {
        if (formProperty == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty");
        }
        if (StringUtils.isBlank(formProperty.getName()) || formProperty.getName().length() > 255) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.name");
        }
        if (StringUtils.isBlank(formProperty.getDataType())) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.dataType");
        }
        if (formProperty.getDefaultValue() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.defaultValue");
        }
        try {
            JsonUtils.parseObject(formProperty.getDefaultValue(),
                    FormPropertyTypeEnum.getClazz(formProperty.getDataType()));
        } catch (AippJsonDecodeException e) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.defaultValue");
        }
        if (Arrays.stream(FORM_PROPERTY_FROM_SET).noneMatch(from -> StringUtils.equals(from, formProperty.getFrom()))) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.from");
        }
        if (Arrays.stream(FORM_PROPERTY_GROUP_SET)
                .noneMatch(group -> StringUtils.equals(group, formProperty.getGroup()))) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.group");
        }
        if (formProperty.getDescription() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "formProperty.description");
        }
    }

    private static void checkAppExportFlowGraph(AppExportFlowGraph flowGraph) {
        if (flowGraph == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "flowGraph");
        }
        if (flowGraph.getName() == null) {
            flowGraph.setName("LLM模板");
        }
        if (flowGraph.getName().length() > MAX_NAME_LENGTH) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "flowGraph.name");
        }
        if (flowGraph.getAppearance() == null) {
            throw new AippException(IMPORT_CONFIG_FIELD_ERROR, "flowGraph.appearance");
        }
    }

    /**
     * 生成导入应用的新的名字，重名时进行重命名。
     *
     * @param existedNames 表示数据库中类似名字的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param initName 表示导入应用初始名字的 {@link String}。
     * @return 表示生成的新的名字。
     */
    public static String generateNewAppName(List<String> existedNames, String initName) {
        if (existedNames.isEmpty() || !StringUtils.equals(existedNames.get(0), initName)) {
            return initName;
        }
        Pattern pattern = Pattern.compile(initName + RENAME_PATTERN);
        List<Integer> sortedCopyNums = existedNames.stream()
                .map(name -> extractCopyNumber(pattern, name))
                .filter(OptionalInt::isPresent)
                .map(OptionalInt::getAsInt)
                .sorted()
                .collect(Collectors.toList());
        return generateMinCopyName(sortedCopyNums, initName);
    }

    private static OptionalInt extractCopyNumber(Pattern pattern, String existedName) {
        Matcher matcher = pattern.matcher(existedName);
        if (!matcher.matches()) {
            return OptionalInt.empty();
        }
        String existNumber = matcher.group(1);
        return OptionalInt.of(StringUtils.isBlank(existNumber) ? Integer.valueOf(0) : Integer.valueOf(existNumber));
    }

    private static String generateMinCopyName(List<Integer> sortedCopyNums, String initName) {
        int targetCopyNum = 0;
        for (int existCopyNum : sortedCopyNums) {
            if (existCopyNum == targetCopyNum) {
                targetCopyNum += 1;
                continue;
            }
            break;
        }
        return StringUtils.format(RENAME_FORMATTER, initName,
                targetCopyNum == 0 ? StringUtils.EMPTY : String.valueOf(targetCopyNum));
    }

    /**
     * 将应用导入配置 {@link AppExportDto} 转换为内部的应用结构 {@link AppBuilderApp}。
     *
     * @param appExportDto 表示导入应用的配置的 {@link AppExportDto}。
     * @param context 表示转换上下文的 {@link OperationContext}。
     * @return 表示导入应用的信息的 {@link AppBuilderApp}。
     */
    public static AppBuilderApp convertToAppBuilderApp(AppExportDto appExportDto, OperationContext context) {
        AppExportApp appExportApp = appExportDto.getApp();
        AppBuilderConfig config = convertToAppBuilderConfig(appExportDto.getConfig(), context);
        AppBuilderFlowGraph flowGraph = convertToAppBuilderFlowGraph(appExportDto.getFlowGraph(), context);
        appExportApp.getAttributes().put("icon", "");
        appExportApp.setAttributes(resetAppAttributes(appExportApp.getAttributes()));
        return AppBuilderApp.builder()
                .createBy(context.getOperator())
                .config(config)
                .flowGraph(flowGraph)
                .attributes(appExportApp.getAttributes())
                .name(appExportApp.getName())
                .version(appExportApp.getVersion())
                .type(appExportApp.getType())
                .appBuiltType(appExportApp.getAppBuiltType())
                .appCategory(appExportApp.getAppCategory())
                .isDeleted(false)
                .state(AppState.IMPORTING.getName())
                .tenantId(context.getTenantId())
                .formProperties(getFormProperties(config.getConfigProperties()))
                .appType(appExportApp.getAppType())
                .build();
    }

    /**
     * 将应用的配置 {@link List}{@code <}{@link AppBuilderConfigProperty}{@code >} 转换
     * 为应用的表单配置 {@link List}{@code <}{@link AppBuilderFormProperty}{@code >}。
     *
     * @param configProperties 表示应用配置列表。
     * @return {@link List}{@code <}{@link AppBuilderFormProperty}{@code >} 表示表单配置。
     */
    public static List<AppBuilderFormProperty> getFormProperties(List<AppBuilderConfigProperty> configProperties) {
        return configProperties
                .stream()
                .map(AppBuilderConfigProperty::getFormProperty)
                .collect(Collectors.toList());
    }

    /**
     * 将应用属性重置为预定义的默认属性集，仅保留输入参数与默认集。
     *
     * @param attributes 表示导入应用属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示重置后应用属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> resetAppAttributes(Map<String, Object> attributes) {
        return Stream.of(APP_ATTR_DEFAULT_SET)
                .collect(Collectors.toMap(Function.identity(), attributes::get));
    }

    /**
     * 将应用导入配置 {@link AppExportConfig} 转换为内部的 ConfigUI 配置 {@link AppBuilderConfig}。
     *
     * @param appExportConfig 表示导入应用的配置的 {@link AppExportConfig}。
     * @param context 表示转换上下文的 {@link OperationContext}。
     * @return 表示导入应用的 ConfigUI 的 {@link AppBuilderConfig}。
     */
    public static AppBuilderConfig convertToAppBuilderConfig(AppExportConfig appExportConfig,
            OperationContext context) {
        List<AppBuilderConfigProperty> configProperties = appExportConfig.getConfigProperties()
                .stream()
                .map(AppImExportUtil::convertToAppBuilderConfigProperty)
                .collect(Collectors.toList());
        AppBuilderForm form = convertToAppBuilderForm(appExportConfig.getForm(), context);
        return AppBuilderConfig.builder()
                .form(form)
                .configProperties(configProperties)
                .tenantId(context.getTenantId())
                .build();
    }

    /**
     * 将应用导入的配置的表单配置转换为应用的表单配置。
     *
     * @param appExportForm 表示导入应用的配置的表单配置的 {@link AppExportForm}。
     * @param context 表示转换上下文的 {@link OperationContext}。
     * @return 表示应用的 ConfigUI 表单配置的 {@link AppBuilderForm}。
     */
    public static AppBuilderForm convertToAppBuilderForm(AppExportForm appExportForm, OperationContext context) {
        return AppBuilderForm.builder().id(appExportForm.getId())
                .name(appExportForm.getName())
                .type(appExportForm.getType())
                .appearance(appExportForm.getAppearance())
                .tenantId(context.getTenantId())
                .formSuiteId(appExportForm.getFormSuiteId())
                .version(appExportForm.getVersion())
                .build();
    }

    /**
     * 将应用导出配置的流程配置转换为应用流程配置。
     *
     * @param appExportFlowGraph 表示应用导出的配置中的流程配置的 {@link AppExportFlowGraph}。
     * @param context 表示转换上下文的 {@link OperationContext}。
     * @return 表示应用流程配置的 {@link AppBuilderFlowGraph}。
     */
    public static AppBuilderFlowGraph convertToAppBuilderFlowGraph(AppExportFlowGraph appExportFlowGraph,
            OperationContext context) {
        return AppBuilderFlowGraph.builder()
                .name(appExportFlowGraph.getName())
                .appearance(appExportFlowGraph.getAppearance())
                .build();
    }

    /**
     * 将应用导出配置的 ConfigUI 属性转换为应用的 ConfigUI 属性。
     *
     * @param configProperty 表示应用导出配置的 ConfigUI 属性的 {@link AppExportConfigProperty}。
     * @return 表示应用的 ConfigUI 属性的 {@link AppBuilderConfigProperty}。
     */
    public static AppBuilderConfigProperty convertToAppBuilderConfigProperty(AppExportConfigProperty configProperty) {
        AppBuilderFormProperty formProperty = convertToAppBuilderFormProperty(configProperty.getFormProperty());
        return AppBuilderConfigProperty.builder()
                .formProperty(formProperty)
                .formPropertyId(formProperty.getId())
                .nodeId(configProperty.getNodeId())
                .build();
    }

    /**
     * 将应用导出配置的 ConfigUI 表单属性转换为应用的 ConfigUI 表单属性。
     *
     * @param formProperty 表示应用导出配置的 ConfigUI 表单属性的 {@link AppExportFormProperty}。
     * @return 表示应用的 ConfigUI 表单属性的 {@link AppBuilderFormProperty}。
     */
    public static AppBuilderFormProperty convertToAppBuilderFormProperty(AppExportFormProperty formProperty) {
        return AppBuilderFormProperty.builder()
                .id(Entities.generateId())
                .name(formProperty.getName())
                .dataType(formProperty.getDataType())
                .defaultValue(JsonUtils.parseObject(formProperty.getDefaultValue(),
                        FormPropertyTypeEnum.getClazz(formProperty.getDataType())))
                .from(formProperty.getFrom())
                .group(formProperty.getGroup())
                .description(formProperty.getDescription())
                .index(formProperty.getIndex())
                .build();
    }

    /**
     * 保存头像文件。
     *
     * @param iconContent 表示 base64 编码的图像字节的 {@link String}。
     * @param iconExtension 表示图像类型后缀的 {@link String}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param contextRoot 表示请求上下文根的 {@link String}。
     * @param resourcePath 表示资源目录的 {@link String}。
     * @return 表示构造好的图像的路径，可以存放在 attribute 中的 {@link String}。
     */
    public static String saveIconFile(String iconContent, String iconExtension, String tenantId, String contextRoot,
            String resourcePath) {
        boolean isValidExtension = Stream.of(LEGAL_ICON_TYPE)
                .anyMatch(type -> StringUtils.equalsIgnoreCase(type, iconExtension));
        if (!isValidExtension) {
            return StringUtils.EMPTY;
        }
        String newFileName = UUIDUtil.uuid() + "." + iconExtension;
        File iconFile = Paths.get(resourcePath, newFileName).toFile();
        byte[] iconBytes = iconContent.getBytes(StandardCharsets.UTF_8);
        try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(iconBytes))) {
            FileUtils.copyInputStreamToFile(inputStream, iconFile);
            return MessageFormat.format(contextRoot + ICON_URL_PATTERN, tenantId, iconFile.getCanonicalPath(),
                    newFileName);
        } catch (IOException | IllegalArgumentException e) {
            iconFile.delete();
            return StringUtils.EMPTY;
        }
    }

    /**
     * 判断上传的文件是否为 json 格式。
     *
     * @param fileName 表示上传的文件的文件名的 {@link String}。
     * @return 是否为 json 格式。
     */
    public static boolean isJsonFile(String fileName) {
        int loc = fileName.lastIndexOf(FILE_EXTENSION_DELIM);
        if (loc == -1) {
            return false;
        }
        String extension = fileName.substring(loc);
        return StringUtils.equals(extension, ".json");
    }
}
