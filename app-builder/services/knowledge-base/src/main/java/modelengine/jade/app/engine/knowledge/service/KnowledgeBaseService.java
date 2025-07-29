/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.knowledge.dto.KbChunkSearchDto;
import modelengine.jade.app.engine.knowledge.dto.KbGenerateConfigDto;
import modelengine.jade.app.engine.knowledge.dto.KbTextQueryDto;
import modelengine.jade.app.engine.knowledge.dto.KbVectorSearchDto;
import modelengine.jade.app.engine.knowledge.dto.TableKnowledgeColDto;
import modelengine.jade.app.engine.knowledge.params.TableKnowledgeParam;
import modelengine.jade.app.engine.knowledge.vo.PageResultVo;

import java.util.List;
import java.util.Map;

/**
 * KbGenerateService 知识生成
 *
 * @author YangPeng
 * @since 2024-05-20 11:10
 */
public interface KnowledgeBaseService {
    /**
     * 导入文本类型知识
     *
     * @param configDto 配置信息
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService.importTextKnowledge")
    void importTextKnowledge(KbGenerateConfigDto configDto);

    /**
     * 检索知识库数据，返回召回文本列表
     *
     * @param chunkSearchDto 检索参数
     * @return 检索结果
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService.searchKnowledgeTable")
    List<String> searchKnowledgeTable(KbChunkSearchDto chunkSearchDto);

    /**
     * 向量检索知识库数据，返回召回文本列表
     *
     * @param chunkSearchDto 检索参数
     * @return 检索结果
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService.vectorSearchKnowledgeTable")
    List<String> vectorSearchKnowledgeTable(KbVectorSearchDto chunkSearchDto);

    /**
     * <p>分页获取文本类型知识表chunk列表</p>
     *
     * @param textQueryDto 文本知识库分页查询参数
     * @return 分页查询结果
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService.textQueryChunkList")
    List<String> queryTextChunkList(KbTextQueryDto textQueryDto);

    /**
     * 导入表格类型知识
     *
     * @param param 表格型知识参数
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService.createTableKnowledge")
    void createTableKnowledge(TableKnowledgeParam param);

    /**
     * 查询列信息
     *
     * @param param 表格型知识参数
     * @return 列信息
     */
    @Genericable(
            id = "modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService.getTableKnowledgePreviewColumns")
    List<TableKnowledgeColDto> getTableKnowledgePreviewColumns(TableKnowledgeParam param);

    /**
     * 查询表格型知识总条数
     *
     * @param param 表格型知识参数
     * @return 知识表条数
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KbGenerateService.getTableKnowledgeRowsCount")
    long getTableKnowledgeRowsCount(TableKnowledgeParam param);

    /**
     * 查询表格型知识总条数
     *
     * @param param 表格型知识参数
     * @return 知识表条数
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KbGenerateService.getTableKnowledgeDbRows")
    PageResultVo<Map<String, Object>> getTableKnowledgeDbRows(TableKnowledgeParam param);

    /**
     * 查询表格型知识数据库中的列
     *
     * @param repoId 知识库id
     * @param tableId 知识表id
     * @return 知识表列
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KbGenerateService.getTableKnowledgeDbColumns")
    List<TableKnowledgeColDto> getTableKnowledgeDbColumns(Long repoId, Long tableId);
}
