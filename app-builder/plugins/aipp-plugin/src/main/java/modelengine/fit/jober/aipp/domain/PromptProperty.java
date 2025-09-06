/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 灵感大全属性实体类
 *
 * @author 夏斐
 * @since 2024/10/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PromptProperty extends AppBuilderFormProperty {
    private JSONObject data;

    private List<AppBuilderPromptCategoryDto> categories;

    private List<AppBuilderPromptDto.AppBuilderInspirationDto> inspirations;

    /**
     * PromptProperty构造函数
     *
     * @param property property
     */
    public PromptProperty(AppBuilderFormProperty property) {
        super(property.getId(), property.getFormId(), property.getName(), property.getDataType(),
                property.getDefaultValue(), property.getFrom(), property.getGroup(), property.getDescription(),
                property.getIndex(), property.getAppId(), null, null);

        Object defaultValue = this.getDefaultValue();
        this.data = JSONObject.parseObject(JSONObject.toJSONString(defaultValue));
        this.categories = this.data.getObject("category", new TypeReference<List<AppBuilderPromptCategoryDto>>() {});
        this.inspirations = this.data.getObject("inspirations",
                new TypeReference<List<AppBuilderPromptDto.AppBuilderInspirationDto>>() {});
    }

    /**
     * 获取所有通用的类目
     *
     * @return 类目列表
     */
    public List<AppBuilderPromptCategoryDto> getAllCategories() {
        return categories;
    }

    /**
     * 根据categoryId获取类目
     *
     * @param categoryId categoryId 类目id
     * @return 返回类目对象
     */
    public AppBuilderPromptCategoryDto getCategoryById(String categoryId) {
        if (Objects.equals(categoryId, "others")) {
            return AppBuilderPromptCategoryDto.builder().children(new ArrayList<>()).build();
        }
        return this.findCategoryById(categoryId, this.getAllCategories()).orElse(null);
    }

    /**
     * 获取通用的灵感大全
     *
     * @return 返回灵感大全对象列表
     */
    public List<AppBuilderPromptDto.AppBuilderInspirationDto> getAllInspirations() {
        return inspirations;
    }

    /**
     * 根据categoryId获取灵感大全
     *
     * @param categoryId categoryId 类目id
     * @return 返回灵感大全列表
     */
    public List<AppBuilderPromptDto.AppBuilderInspirationDto> getInspirationsByCategoryId(String categoryId) {
        AppBuilderPromptCategoryDto categoriesById = this.getCategoryById(categoryId);
        return getAllInspirations().stream().filter(dto -> {
            if (categoryId.equals("others")) {
                return Objects.isNull(dto.getCategory());
            }
            if (Objects.isNull(dto.getCategory())) {
                return categoryId.equals("root");
            }
            String[] category = dto.getCategory().split(":");
            // 如果目标节点是叶子节点，那么匹配id为后面部分否则为父节点id
            return categoryId.equals(category[this.ifLeaf(categoriesById) ? 1 : 0]);
        }).collect(Collectors.toList());
    }

    /**
     * 判断是否是直接查看“我的”类目
     *
     * @param categoryId categoryId 类目id
     * @return 返回boolean
     */
    public boolean isCustomCategory(String categoryId) {
        // 如果直接查看的是“我的”类目，categories为null
        return this.getCategoryById(categoryId) == null;
    }

    private Optional<AppBuilderPromptCategoryDto> findCategoryById(String id,
            List<AppBuilderPromptCategoryDto> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return Optional.empty();
        }
        for (AppBuilderPromptCategoryDto category : categories) {
            if (category.getId().equals(id)) {
                return Optional.of(category);
            }
            Optional<AppBuilderPromptCategoryDto> match = findCategoryById(id, category.getChildren());
            if (match.isPresent()) {
                return match;
            }
        }
        return Optional.empty();
    }

    private boolean ifLeaf(AppBuilderPromptCategoryDto category) {
        return CollectionUtils.isEmpty(category.getChildren());
    }
}
