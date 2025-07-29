/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.dto.aipplog.AippUploadedFileInfoDto;

import java.util.List;

/**
 * aipp实例文件记录db接口
 *
 * @author 熊以可
 * @since 2024-02-04
 */
public interface AippUploadedFileMapper {
    /**
     * 查询过期文件
     *
     * @return 查询结果list
     */
    List<String> queryExpiredFiles();

    /**
     * 通过用户Aipp查询文件
     *
     * @param aippId aipp唯一标识
     * @return 查询结果list
     */
    List<String> queryFilesByUserAipp(String aippId);

    /**
     * 删除文件记录
     *
     * @param aippId aipp唯一标识
     * @param fileNames 文件名称
     */
    void deleteFileRecords(String aippId, List<String> fileNames);

    /**
     * 插入文件记录
     *
     * @param fileInfo 文件信息
     */
    void insertFileRecord(AippUploadedFileInfoDto fileInfo);

    /**
     * 删除文件记录
     *
     * @param aippIds aipp唯一标识
     */
    void deleteByAippIds(List<String> aippIds);

    /**
     * 更新文件记录状态和appId
     *
     * @param appId app唯一标识
     * @param fileName 文件名称
     * @param status 文件是否可以清理的标识
     */
    void updateRecord(String appId, String fileName, Integer status);

    /**
     * 根据文件uuid更改可清理标志
     *
     * @param fileUuid 表示文件uuid的 {@link String}。
     * @param status 表示文件是否可以清理的标识的 {@link Integer}。
     */
    void updateRecordWithFileUuid(String fileUuid, Integer status);
}
