/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.jadeconfig.JadeConfig;
import modelengine.fit.jober.aipp.domains.jadeconfig.JadeShape;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.util.UsefulUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用构建器配置类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class AppBuilderConfig extends BaseDomain {
    private String id;
    private String formId;
    private String appId;
    private String tenantId;
    private AppBuilderForm form;
    private AppVersion appVersion;
    private List<AppBuilderConfigProperty> configProperties;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppBuilderAppRepository appRepository;
    private AppVersionService appVersionService;
    private List<AppBuilderFormProperty> formProperties;

    public AppBuilderConfig(AppBuilderFormRepository formRepository,
            AppBuilderFormPropertyRepository formPropertyMapper,
            AppBuilderConfigPropertyRepository configPropertyRepository, AppVersionService appVersionService) {
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyMapper;
        this.configPropertyRepository = configPropertyRepository;
        this.appVersionService = appVersionService;
    }

    public AppBuilderForm getForm() {
        return lazyGet(this.form, this::loadForm, this::setForm);
    }

    private AppBuilderForm loadForm() {
        Validation.notNull(this.formId, "App builder config can not be null.");
        AppBuilderForm appBuilderForm = this.formRepository.selectWithId(this.formId);
        Validation.notNull(appBuilderForm, "App builder form can not be null.");
        appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderForm;
    }

    public List<AppBuilderConfigProperty> getConfigProperties() {
        return lazyGet(this.configProperties, this::loadConfigProperties, this::setConfigProperties);
    }

    private List<AppBuilderConfigProperty> loadConfigProperties() {
        return this.configPropertyRepository.selectWithConfigId(this.id);
    }

    /**
     * 获取应用版本.
     *
     * @return {@link AppVersion} 对象.
     */
    public AppVersion getAppVersion() {
        return lazyGet(this.appVersion, () -> this.appVersionService.retrieval(this.appId),
                v -> this.appVersion = v);
    }

    /**
     * 获取表单配置项集合.
     *
     * @return {@link List}{@code <}{@link AppBuilderFormProperty}{@code >} 集合.
     */
    public List<AppBuilderFormProperty> getFormProperties() {
        return UsefulUtils.lazyGet(this.formProperties,
                () -> this.formPropertyRepository.selectWithAppId(this.appId),
                ps -> this.formProperties = ps);
    }

    /**
     * 创建app时调用，用于刷新id等操作.
     *
     * @param formProperties 表单属性列表.
     * @param context 操作人上下文信息.
     */
    public void clone(List<AppBuilderFormProperty> formProperties, OperationContext context) {
        AppBuilderForm appBuilderForm = this.getForm();
        List<AppBuilderConfigProperty> configPropertyList = this.getConfigProperties();

        // 这里先根据旧的formId查询得到formProperties
        Map<String, AppBuilderFormProperty> formPropertyMap = formProperties.stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));

        // 先根据旧的configId查询得到configProperties
        this.setId(Entities.generateId());
        configPropertyList.forEach(cp -> this.resetProperty(cp, formPropertyMap, appBuilderForm.getId()));

        // 其他属性设置.
        LocalDateTime now = LocalDateTime.now();
        this.setFormId(appBuilderForm.getId());
        this.setCreateBy(context.getOperator());
        this.setCreateAt(now);
        this.setUpdateBy(context.getOperator());
        this.setUpdateAt(now);
        appBuilderForm.setCreateBy(context.getOperator());
        appBuilderForm.setCreateAt(now);
        appBuilderForm.setUpdateBy(context.getOperator());
        appBuilderForm.setUpdateAt(now);
    }

    private void resetProperty(AppBuilderConfigProperty configProperty,
            Map<String, AppBuilderFormProperty> idToFormPropertyMap, String formId) {
        configProperty.setId(Entities.generateId());
        configProperty.setConfigId(this.getId());
        AppBuilderFormProperty formProperty = idToFormPropertyMap.get(configProperty.getFormPropertyId());
        formProperty.setId(Entities.generateId());
        formProperty.setFormId(formId);
        configProperty.setFormPropertyId(formProperty.getId());
    }

    /**
     * 通过新的properties对之前的进行删除、新增、修改操作.
     *
     * @param properties 新的属性集合.
     */
    public void updateByProperties(List<AppBuilderConfigFormPropertyDto> properties) {
        Map<String, AppBuilderConfigFormPropertyDto> newPropertyMap = properties.stream()
                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getId, Function.identity()));

        // 删除
        this.deleteByProperties(properties);

        // 新增
        this.addProperties(properties);

        // 修改, 待修改的内容, 循环修改
        this.getFormProperties().stream()
                .filter(formProperty -> newPropertyMap.containsKey(formProperty.getId()))
                .forEach(formProperty -> {
                    AppBuilderConfigFormPropertyDto propertyDto = newPropertyMap.get(formProperty.getId());
                    formProperty.setName(propertyDto.getName());
                    formProperty.setDataType(propertyDto.getDataType());
                    formProperty.setDefaultValue(propertyDto.getDefaultValue());
                    this.formPropertyRepository.updateOne(formProperty);
                });
    }

    private void deleteByProperties(List<AppBuilderConfigFormPropertyDto> properties) {
        Map<String, AppBuilderConfigFormPropertyDto> newPropertyMap = properties.stream()
                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getId, Function.identity()));
        List<String> toDeleteConfigPropertyIds = this.getConfigProperties()
                .stream()
                .filter(cp -> !newPropertyMap.containsKey(cp.getFormPropertyId()))
                .map(AppBuilderConfigProperty::getId)
                .collect(Collectors.toList());
        List<String> toDeleteFormPropertyIds = this.getFormProperties()
                .stream()
                .map(AppBuilderFormProperty::getId)
                .filter(id -> !newPropertyMap.containsKey(id))
                .collect(Collectors.toList());
        this.configPropertyRepository.deleteMore(toDeleteConfigPropertyIds);
        this.formPropertyRepository.deleteMore(toDeleteFormPropertyIds);
    }

    private void addProperties(List<AppBuilderConfigFormPropertyDto> properties) {
        Set<String> formPropertyIds = this.getFormProperties()
                .stream()
                .map(AppBuilderFormProperty::getId)
                .collect(Collectors.toSet());
        List<AppBuilderConfigProperty> toAddConfigProperties = properties.stream()
                .filter(pd -> this.isLegalProperty(pd, formPropertyIds))
                .map(this::buildAppBuilderConfigProperty)
                .collect(Collectors.toList());
        List<AppBuilderFormProperty> toAddFormProperties = toAddConfigProperties.stream()
                .map(AppBuilderConfigProperty::getFormProperty)
                .collect(Collectors.toList());

        this.configPropertyRepository.insertMore(toAddConfigProperties);
        this.formPropertyRepository.insertMore(toAddFormProperties);
    }

    private boolean isLegalProperty(AppBuilderConfigFormPropertyDto pd, Set<String> formPropertyIds) {
        return StringUtils.isBlank(pd.getId()) || !formPropertyIds.contains(pd.getId());
    }

    private AppBuilderConfigProperty buildAppBuilderConfigProperty(AppBuilderConfigFormPropertyDto propertyDto) {
        AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder()
                .formId(this.getFormId())
                .name(propertyDto.getName())
                .dataType(propertyDto.getDataType())
                .defaultValue(propertyDto.getDefaultValue())
                .id(Entities.generateId())
                .appId(this.getAppId())
                .build();
        return AppBuilderConfigProperty.builder()
                .id(Entities.generateId())
                .configId(this.getId())
                .nodeId(propertyDto.getNodeId())
                .formPropertyId(formProperty.getId())
                .formProperty(formProperty)
                .build();
    }

    /**
     * 通过appearance修改配置.
     *
     * @param appearance graph数据序列化数据.
     */
    public void updateByAppearance(String appearance) {
        // 这个map {nodeId:{name:value}}
        JadeConfig jadeConfig = new JadeConfig(appearance);
        Map<String, AppBuilderFormProperty> idToFormPropertyMap = this.getFormProperties()
                .stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));

        // 这样写避免循环的时候去查询数据库获取configProperty对应的formProperty
        for (AppBuilderConfigProperty cp : this.getConfigProperties()) {
            if (!idToFormPropertyMap.containsKey(cp.getFormPropertyId())) {
                // 2024/4/29 0029 这里可能拿到null，这里暂时不知道什么问题，先把拿不到的跳过
                continue;
            }
            cp.setFormProperty(idToFormPropertyMap.get(cp.getFormPropertyId()));
            String nodeId = cp.getNodeId();
            if (StringUtils.isBlank(nodeId)) {
                // 这里排除掉空nodeId的config
                continue;
            }
            Optional<JadeShape> shapeOp = jadeConfig.getShapeById(nodeId);
            AppBuilderFormProperty formProperty = cp.getFormProperty();
            if (shapeOp.isEmpty()) {
                // 2024/4/29 0029 暂时先不删除了，仅修改现存的内容
                continue;
            }
            formProperty.updateByShape(shapeOp.get());

            // 更新
            this.formPropertyRepository.updateOne(formProperty);
        }
    }
}
