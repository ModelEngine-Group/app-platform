package modelengine.fit.jade.aipp.model.repository.impl;

import modelengine.fit.jade.aipp.model.mapper.ModelMapper;
import modelengine.fit.jade.aipp.model.mapper.UserModelMapper;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.po.UserModelDetailPo;
import modelengine.fit.jade.aipp.model.po.UserModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelPluginRepo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.jade.carver.tool.annotation.Attribute;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表示用户模型信息用于插件的持久化层的接口 {@link UserModelRepo} 的实现。
 *
 * @author lizhichao
 * @since 2025/4/9
 */
@Component
@Group(name = "User_Model_Tool_Impl")
public class UserModelPluginRepoImpl implements UserModelPluginRepo {
    private static final Logger log = Logger.get(UserModelRepoImpl.class);
    private static final String FITABLE_ID = "aipp.model.repository";
    public static final String DEFAULT_MODEL_TYPE = "chat_completions";
    private final ModelMapper modelMapper;
    private final UserModelMapper userModelMapper;

    /**
     * 构造方法。
     *
     * @param modelMapper       模型信息表的 MyBatis 映射接口，用于处理模型增删查改。
     * @param userModelMapper   用户与模型绑定关系的 MyBatis 映射接口，用于管理用户模型映射数据。
     */
    public UserModelPluginRepoImpl(ModelMapper modelMapper, UserModelMapper userModelMapper) {
        this.modelMapper = modelMapper;
        this.userModelMapper = userModelMapper;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "获取用户模型列表", description = "根据用户标识来查询该用户可用的模型列表", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "返回该用户可用的模型列表")
    public List<UserModelDetailPo> getUserModelList(String userId) {
        log.info("start get model list for {}.", userId);
        List<UserModelPo> userModelPos = this.userModelMapper.listUserModels(userId);
        if (CollectionUtils.isEmpty(userModelPos)) {
            log.warn("No user model records found for userId={}.", userId);
            return Collections.emptyList();
        }
        List<String> modelIds = userModelPos.stream()
                .map(UserModelPo::getModelId)
                .distinct()
                .collect(Collectors.toList());
        List<ModelPo> modelPos = this.modelMapper.listModels(modelIds);
        // 构建 modelId → ModelPo 映射
        Map<String, ModelPo> modelMap = modelPos.stream()
                .map(model -> Map.entry(model.getModelId(), model))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a
                ));
        return userModelPos.stream().map(userModel -> {
            ModelPo model = modelMap.get(userModel.getModelId());
            return new UserModelDetailPo(
                    userModel.getCreatedAt(),
                    userModel.getModelId(),
                    userModel.getUserId(),
                    model != null ? model.getName() : null,
                    model != null ? model.getBaseUrl() : null,
                    userModel.getIsDefault()
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "添加模型", description = "为用户添加可用的模型信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "为用户添加可用的模型信息")
    public String addUserModel(String userId, String apiKey,
                               String modelName, String baseUrl) {
        log.info("start add user model for {}.", userId);
        String modelId = UUID.randomUUID().toString().replace("-", "");
        int isDefault = this.userModelMapper.userHasDefaultModel(userId) ? 0 : 1;

        ModelPo modelPo = new ModelPo(modelId, modelName, modelId, baseUrl, DEFAULT_MODEL_TYPE);
        modelPo.setCreatedBy(userId);
        modelPo.setUpdatedBy(userId);
        this.modelMapper.insertModel(modelPo);

        UserModelPo userModelPo = new UserModelPo(userId, modelId, apiKey, isDefault);
        userModelPo.setCreatedBy(userId);
        userModelPo.setUpdatedBy(userId);
        this.userModelMapper.addUserModel(userModelPo);
        return "添加模型成功。";
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "删除模型", description = "删除用户绑定的模型信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "删除用户绑定的模型信息")
    public String deleteUserModel(String userId, String modelId) {
        log.info("start delete user model for {}.", userId);
        List<UserModelPo> userModels = this.userModelMapper.listUserModels(userId);
        if (userModels == null || userModels.isEmpty()) {
            return "删除模型失败，当前用户没有任何模型记录。";
        }

        UserModelPo target = userModels.stream()
                .filter(m -> Objects.equals(m.getModelId(), modelId))
                .findFirst()
                .orElse(null);
        if (target == null) {
            return "删除模型失败，该模型不属于当前用户。";
        }
        this.userModelMapper.deleteByModelId(modelId);
        this.modelMapper.deleteByModelId(modelId);
        // 如果删除的不是默认模型，直接返回
        if (target.getIsDefault() != 1) {
            return "删除模型成功。";
        }
        userModels.remove(target);
        // 如果没有默认模型，但还有其他记录，则设置最新创建的为默认
        if (!userModels.isEmpty()) {
            UserModelPo latest = userModels.stream()
                    .max(Comparator.comparing(UserModelPo::getCreatedAt))
                    .orElse(null);
            this.userModelMapper.switchDefaultForUser(userId, latest.getModelId());
            return String.format("删除默认模型成功，添加%s为默认模型。", this.modelMapper.get(latest.getModelId()).getName());
        }
        return "删除模型成功，当前无默认模型。";
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "切换默认模型", description = "将指定模型设置为用户的默认模型", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "将指定模型设置为用户的默认模型")
    public String switchDefaultModel(String userId, String modelId) {
        log.info("start switch default model for {}.", userId);
        int rows = this.userModelMapper.switchDefaultForUser(userId, modelId);
        if (rows == 0) {
            return "未查到对应模型。";
        }
        return String.format("已切换%s为默认模型。", this.modelMapper.get(modelId).getName());
    }
}
