package modelengine.fit.jade.aipp.model.repository;

import modelengine.fit.jade.aipp.model.po.UserModelDetailPo;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

import java.util.List;

/**
 * 表示用户模型信息用于插件的持久化层的接口。
 *
 * @author lizhichao
 * @since 2025/4/9
 */
@Group(name = "User_Model_Tool")
public interface UserModelPluginRepo {
    /**
     * 根据用户标识来查询该用户可用的用户模型列表。
     *
     * @param userId 表示用户标识 {@link String}。
     * @return 表示该用户可用的用户模型列表 {@link List}{@code <}{@link UserModelDetailPo}{@code >}。
     */
    @ToolMethod(name = "get_user_model_list", description = "根据用户标识来查询该用户可用的用戶模型列表")
    @Genericable(id = "modelengine.fit.jade.aipp.model.getUserModelList")
    List<UserModelDetailPo> getUserModelList(@Property(description = "用户id", required = true) String userId);

    /**
     * 为用户添加模型。
     * <p>插入模型名称、访问地址与密钥等信息，并与指定用户进行绑定。</p>
     *
     * @param userId 表示用户标识 {@link String}。
     * @param apiKey 表示该用户访问模型所需的 API Key {@link String}。
     * @param modelName 表示模型名称 {@link String}。
     * @param baseUrl 表示模型访问的地址 {@link String}。
     * @return 添加结果提示信息 {@link String}。
     */
    @ToolMethod(name = "add_user_model", description = "为用户添加可用的模型信息")
    @Genericable(id = "modelengine.fit.jade.userModel.addUserModel")
    String addUserModel(@Property(description = "用户id", required = true) String userId,
            @Property(description = "模型访问的 API Key", required = true) String apiKey,
            @Property(description = "模型名称", required = true) String modelName,
            @Property(description = "模型访问地址", required = true) String baseUrl);

    /**
     * 删除用户绑定的模型信息。
     *
     * @param userId 表示用户标识 {@link String}。
     * @param modelId 表示待删除的模型标识 {@link String}。
     * @return 删除结果提示信息 {@link String}。
     */
    @ToolMethod(name = "delete_user_model", description = "删除用户绑定的模型信息")
    @Genericable(id = "modelengine.fit.jade.userModel.deleteUserModel")
    String deleteUserModel(@Property(description = "用户id", required = true) String userId,
            @Property(description = "模型id", required = true) String modelId);

    /**
     * 将指定模型设置为该用户的默认模型。
     *
     * @param userId 表示用户标识 {@link String}。
     * @param modelId 表示要设为默认的模型标识 {@link String}。
     * @return 切换默认模型的提示信息 {@link String}。
     */
    @ToolMethod(name = "switch_default_model", description = "将指定模型设置为用户的默认模型")
    @Genericable(id = "modelengine.fit.jade.userModel.switchDefaultModel")
    String switchDefaultModel(@Property(description = "用户id", required = true) String userId,
            @Property(description = "默认模型id", required = true) String modelId);
}
