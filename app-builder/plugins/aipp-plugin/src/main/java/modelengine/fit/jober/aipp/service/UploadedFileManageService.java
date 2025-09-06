/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import java.io.IOException;
import java.util.List;

/**
 * 文件上传管理器
 *
 * @author 孙怡菲
 * @since 2024/5/10
 */
public interface UploadedFileManageService {
    /**
     * 清理aipp文件
     *
     * @param aippId id
     */
    void cleanAippFiles(List<String> aippId);

    /**
     * 添加文件记录
     *
     * @param aipp id
     * @param createUserAccount 创建者账号
     * @param filename 文件名
     * @param fileUuid 文件uuid
     */
    void addFileRecord(String aipp, String createUserAccount, String filename, String fileUuid);

    /**
     * 更改文件可清理标志
     *
     * @param fileName 文件名称
     * @param status 文件是否可以清理的标识
     */
    void changeRemovable(String fileName, Integer status);

    /**
     * 根据文件uuid更改可清理标志
     *
     * @param fileUuid 表示文件uuid的 {@link String}。
     * @param status 表示文件是否可以清理的标识的 {@link Integer}。
     */
    void changeRemovableWithFileUuid(String fileUuid, Integer status);

    /**
     * 更新头像文件记录
     *
     * @param appId app唯一标识
     * @param fileName 文件名称
     * @param status 文件是否可以清理的标识
     */
    void updateRecord(String appId, String fileName, Integer status);

    /**
     * 拷贝图标文件.
     *
     * @param icon 图标.
     * @param aippId 应用id.
     * @param operator 操作人.
     * @return {@link String} 拷贝后的图标.
     * @throws IOException io异常.
     */
    String copyIconFiles(String icon, String aippId, String operator) throws IOException;
}
