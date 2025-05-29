/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.condition.FormQueryCondition;
import modelengine.fit.jober.aipp.config.AippFormCreateConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.dto.AppBuilderFormDto;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.common.RangedResultSet;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表单接口实现类
 *
 * @author 邬涨财
 * @since 2024-04-19
 */
@Component
public class AppBuilderFormServiceImpl implements AppBuilderFormService {
    private static final String RUNTIME = "runtime";
    private static final String VERSION = "1.0.0";
    private static final double NAME_MAX_LENGTH = 64;
    private static final double DESCRIPTION_MAX_LENGTH = 300;
    private static final String IMG_URL = "imgUrl";
    private static final String IFRAME_URL = "iframeUrl";
    private static final String FILE_UUID = "fileUuid";
    private static final String FILE_NAME = "fileName";
    private static final String SCHEMA = "schema";
    private static final String DESCRIPTION = "description";
    private static final int REMOVABLE = 1;
    private static final int IRREMOVABLE = 0;
    private static final String FORM_NAME_FORMAT = "^[\\u4E00-\\u9FA5A-Za-z0-9][\\u4E00-\\u9FA5A-Za-z0-9-_]*$";
    private static final Logger log = Logger.get(AppBuilderFormServiceImpl.class);

    private final AppBuilderFormRepository formRepository;
    private final AippFormCreateConfig aippFormCreateConfig;
    private final UploadedFileManageService uploadedFileManageService;
    private final List<String> excludeNames;

    public AppBuilderFormServiceImpl(AppBuilderFormRepository formRepository, AippFormCreateConfig aippFormCreateConfig,
            UploadedFileManageService uploadedFileManageService,
            @Value("${app-engine.form.exclude-names}") List<String> excludeNames) {
        this.formRepository = formRepository;
        this.aippFormCreateConfig = aippFormCreateConfig;
        this.uploadedFileManageService = uploadedFileManageService;
        this.excludeNames = excludeNames;
    }

    @Override
    public Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest, String type,
            String tenantId) {
        return Rsp.ok(this.formRepository.selectWithType(type, tenantId)
                .stream()
                .map(this::buildFormDto)
                .collect(Collectors.toList()));
    }

    @Override
    public AppBuilderForm selectWithId(String id) {
        return this.formRepository.selectWithId(id);
    }

    @Override
    public AppBuilderFormDto create(AppBuilderFormDto dto, OperationContext context) {
        long maximumSize = this.aippFormCreateConfig.getMaximumNum();
        if (this.formRepository.countWithType(RUNTIME, context.getTenantId()) >= maximumSize) {
            log.error("Create smart form failed: the number of existed form is up to maximum 400.");
            throw new AippException(AippErrCode.FORM_UP_TO_MAXIMUM, maximumSize);
        }
        if (dto == null) {
            log.error("Create smart form failed: the form info dto is null.");
            throw new AippException(AippErrCode.CREATE_FORM_NULL);
        }
        this.validate(dto, context.getTenantId(), false);
        AppBuilderForm appBuilderForm = AppBuilderForm.builder()
                .id(Entities.generateId())
                .name(dto.getName())
                .tenantId(context.getTenantId())
                .appearance(dto.getAppearance())
                .type(RUNTIME)
                .createBy(context.getOperator())
                .createAt(LocalDateTime.now())
                .updateBy(context.getOperator())
                .updateAt(LocalDateTime.now())
                .version(VERSION)
                .formSuiteId(Entities.generateId())
                .build();
        this.updateFile(dto.getAppearance(), IRREMOVABLE);
        this.formRepository.insertOne(appBuilderForm);
        log.info("Create smart form successfully.[formId={}]", appBuilderForm.getId());
        return this.buildFormDto(appBuilderForm);
    }

    private void updateFile(Map<String, Object> dto, int irremovable) {
        String fileUuid = cast(dto.get(FILE_UUID));
        this.uploadedFileManageService.changeRemovableWithFileUuid(fileUuid, irremovable);
        log.info("Update file removable status.[fileUuid={}]", fileUuid);
    }

    @Override
    public AppBuilderFormDto update(AppBuilderFormDto dto, String formId, OperationContext context) {
        if (dto == null) {
            log.error("Update smart form failed: the form info dto is null.");
            throw new AippException(AippErrCode.UPDATE_FORM_NULL);
        }
        dto.setId(formId);
        this.validate(dto, context.getTenantId(), true);
        AppBuilderForm appBuilderForm = AppBuilderForm.builder()
                .id(formId)
                .name(dto.getName())
                .tenantId(context.getTenantId())
                .appearance(dto.getAppearance())
                .type(RUNTIME)
                .createBy(dto.getCreateBy())
                .createAt(dto.getCreateAt())
                .updateBy(context.getOperator())
                .updateAt(LocalDateTime.now())
                .version(VERSION)
                .formSuiteId(dto.getFormSuiteId())
                .build();
        this.updateFile(dto.getAppearance(), IRREMOVABLE);
        this.formRepository.updateOne(appBuilderForm);
        log.info("Update smart form successfully.[formId={}]", formId);
        return this.buildFormDto(appBuilderForm);
    }

    @Override
    public RangedResultSet<AppBuilderFormDto> query(long pageNum, int pageSize, String name,
            OperationContext operationContext) {
        long offset = (pageNum - 1) * pageSize;
        FormQueryCondition cond = FormQueryCondition.builder()
                .tenantId(operationContext.getTenantId())
                .type(RUNTIME)
                .createBy(operationContext.getOperator())
                .offset(offset)
                .limit(pageSize)
                .name(name)
                .excludeNames(excludeNames)
                .build();
        long total = this.formRepository.countWithCondition(cond);
        List<AppBuilderFormDto> result = this.formRepository.selectWithCondition(cond)
                .stream()
                .map(this::buildFormDto)
                .collect(Collectors.toList());
        return RangedResultSet.create(result, offset, pageSize, total);
    }

    @Override
    public Void delete(String formId, OperationContext operationContext) {
        AppBuilderForm appBuilderForm = this.formRepository.selectWithId(formId);
        if (appBuilderForm == null) {
            throw new AippException(AippErrCode.FORM_DELETED_FAILED_CAUSE_NOT_EXISTED);
        }
        this.updateFile(appBuilderForm.getAppearance(), REMOVABLE);
        this.formRepository.delete(Collections.singletonList(formId));
        return null;
    }

    @Override
    public long countByType(String type, String tenantId) {
        return this.formRepository.countWithType(type, tenantId);
    }

    private void validate(AppBuilderFormDto dto, String tenantId, boolean isUpdate) {
        if (isUpdate) {
            String id = dto.getId();
            if (id == null || id.isEmpty()) {
                log.error("Update smart form failed: the form id is empty.");
                throw new AippException(AippErrCode.UPDATE_FORM_NOT_EXIST);
            }
            AppBuilderForm queryForm = this.formRepository.selectWithId(id);
            if (queryForm == null) {
                log.error("Update smart form failed: the form is not existed. [formId={}]", id);
                throw new AippException(AippErrCode.UPDATE_FORM_NOT_EXIST);
            }
        }
        this.validateName(dto, tenantId, isUpdate);
        Map<String, Object> appearance = dto.getAppearance();
        if (appearance.get(DESCRIPTION) != null) {
            this.validateDescription(cast(appearance.get(DESCRIPTION)));
        }
        if (appearance.get(IMG_URL) == null) {
            log.error("Create smart form failed: missing imgUrl. [formName={}]", dto.getName());
            throw new AippException(AippErrCode.CREATE_FORM_MISSING_INFO, IMG_URL);
        }
        if (appearance.get(IFRAME_URL) == null) {
            log.error("Create smart form failed: missing iframeUrl. [formName={}]", dto.getName());
            throw new AippException(AippErrCode.CREATE_FORM_MISSING_INFO, IFRAME_URL);
        }
        if (appearance.get(FILE_UUID) == null) {
            log.error("Create smart form failed: missing fileUuid. [formName={}]", dto.getName());
            throw new AippException(AippErrCode.CREATE_FORM_MISSING_INFO, FILE_UUID);
        }
        if (appearance.get(SCHEMA) == null) {
            log.error("Create smart form failed: missing schema. [formName={}]", dto.getName());
            throw new AippException(AippErrCode.CREATE_FORM_MISSING_INFO, SCHEMA);
        }
        if (appearance.get(FILE_NAME) == null) {
            log.error("Create smart form failed: missing fileName. [formName={}]", dto.getName());
            throw new AippException(AippErrCode.CREATE_FORM_MISSING_INFO, FILE_NAME);
        }
    }

    private void validateDescription(String description) {
        if (StringUtils.trim(description).length() > DESCRIPTION_MAX_LENGTH) {
            log.error("Create smart form failed: the length of form description is out of bounds. [description={}]",
                    description);
            throw new AippException(AippErrCode.FORM_DESCRIPTION_LENGTH_OUT_OF_BOUNDS);
        }
    }

    private void validateName(AppBuilderFormDto dto, String tenantId, boolean isUpdate) {
        String name = dto.getName();
        if (!name.matches(FORM_NAME_FORMAT)) {
            log.error("Create smart form failed: the name format is incorrect. [name={}]", name);
            throw new AippException(AippErrCode.APP_NAME_IS_INVALID);
        }
        if (name.length() > NAME_MAX_LENGTH) {
            log.error("Create smart form failed: the length of form name is out of bounds. [name={}]", name);
            throw new AippException(AippErrCode.FORM_NAME_LENGTH_OUT_OF_BOUNDS);
        }
        AppBuilderForm appBuilderForm = this.formRepository.selectWithName(name, tenantId);
        if (appBuilderForm != null) {
            if (isUpdate && StringUtils.equals(appBuilderForm.getId(), dto.getId())) {
                return;
            }
            log.error("Create smart form failed: the form name is existed.[name={}]", name);
            throw new AippException(AippErrCode.FORM_NAME_IS_EXISTED);
        }
    }

    private AppBuilderFormDto buildFormDto(AppBuilderForm appBuilderForm) {
        return AppBuilderFormDto.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .appearance(appBuilderForm.getAppearance())
                .type(appBuilderForm.getType())
                .createBy(appBuilderForm.getCreateBy())
                .createAt(appBuilderForm.getCreateAt())
                .updateAt(appBuilderForm.getUpdateAt())
                .updateBy(appBuilderForm.getUpdateBy())
                .version(appBuilderForm.getVersion())
                .formSuiteId(appBuilderForm.getFormSuiteId())
                .build();
    }
}
