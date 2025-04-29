/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.carver.tool.service.ToolGroupService;
import modelengine.jade.common.filter.support.LoginFilter;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.config.KnowledgeConfig;
import modelengine.jade.knowledge.controller.KnowledgeController;
import modelengine.jade.knowledge.controller.vo.KnowledgePropertyVo;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.router.KnowledgeServiceRouter;
import modelengine.jade.knowledge.support.FlatFilterConfig;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static modelengine.jade.knowledge.enums.FilterType.REFERENCE_TOP_K;
import static modelengine.jade.knowledge.enums.FilterType.SIMILARITY_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 表示 {@link KnowledgeController} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2024-09-29
 */
@MvcTest(classes = {KnowledgeController.class, LoginFilter.class})
@DisplayName("测试 KnowledgeController")
public class KnowledgeControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Fit
    private KnowledgeController knowledgeController;

    @Mock
    private KnowledgeRepoService service;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private KnowledgeI18nService knowledgeI18nService;

    @Mock
    private ToolGroupService toolGroupService;

    private HttpClassicClientResponse<?> response;

    @Mock
    private KnowledgeServiceRouter knowledgeServiceRouter;

    @Mock
    private KnowledgeCenterService knowledgeCenterService;

    @Mock
    private KnowledgeConfig knowledgeConfig;

    @Mock
    private Authenticator authenticator;

    @Mock
    private Invoker invoker;

    @BeforeEach
    void setUp() {
        // 切换线程后，UserContextHolder Mock 失败。
        when(this.authenticationServiceMock.getUserName(any())).thenReturn("Jane");
        when(this.knowledgeCenterService.getApiKey(anyString(), anyString(), anyString())).thenReturn("");
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("查询知识库列表成功")
    void shouldOKWhenGetRepoInfo() {
        String defaultGroup = "default";
        List<KnowledgeDto> groupDataList = new ArrayList<>();
        groupDataList.add(KnowledgeDto.builder().groupId(defaultGroup).build());
        when(this.knowledgeConfig.getSupportList()).thenReturn(groupDataList);

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/knowledge-manager/list/groups")
                .responseType(TypeUtils.parameterized(List.class, new Type[] {KnowledgeDto.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        Assertions.assertThat(this.response.statusCode()).isEqualTo(200);
        List<KnowledgeDto> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        Assertions.assertThat(target.size()).isEqualTo(1);
        Assertions.assertThat(target.get(0).getGroupId()).isEqualTo(defaultGroup);
    }

    @Test
    @DisplayName("pageIndex 不合法，获取 edm 知识库列表接口失败")
    void shouldErrWhenPageIndexIllegal() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/knowledge-manager/list/repos")
                .param("groupId", "1")
                .param("repoName", "name")
                .param("pageIndex", "0")
                .param("pageSize", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {KnowledgeRepo.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        Assertions.assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("pageSize 不合法，获取当前 edm 知识库列表接口失败")
    void shouldErrWhenPageSizeIllegal() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/knowledge-manager/list/repos")
                .param("groupId", "1")
                .param("repoName", "name")
                .param("pageIndex", "1")
                .param("pageSize", "0")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {KnowledgeRepo.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        Assertions.assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("获取当前 edm 知识库列表接口成功")
    void shouldOKWhenParamIllegal() {
        List<KnowledgeRepo> repos = Collections.singletonList(new KnowledgeRepo("1",
                "name",
                "desc",
                "type",
                LocalDateTime.of(2024, 9, 29, 14, 0, 0)));
        when(this.knowledgeServiceRouter.getRouter(any(), anyString(), anyString())).thenReturn(this.invoker);
        when(this.invoker.invoke(any(), any(ListRepoQueryParam.class))).thenReturn(PageVo.of(1, repos));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/knowledge-manager/list/repos")
                .param("groupId", this.buildMockGroupId())
                .param("repoName", "name")
                .param("pageIndex", "1")
                .param("pageSize", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {KnowledgeRepo.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        Assertions.assertThat(this.response.statusCode()).isEqualTo(200);
        Assertions.assertThat(this.response.objectEntity()).isPresent();
        PageVo<KnowledgeRepo> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        KnowledgeRepo repo = target.getItems().get(0);
        Assertions.assertThat(repo.id()).isEqualTo("1");
        Assertions.assertThat(repo.name()).isEqualTo("name");
        Assertions.assertThat(repo.type()).isEqualTo("type");
        Assertions.assertThat(repo.createdAt()).isEqualTo("2024-09-29T14:00:00");
    }

    @Test
    @DisplayName("知识库获取配置成功")
    void shouldOkWhenGetProperty() {
        when(this.knowledgeI18nService.localizeText(any(IndexType.class))).thenReturn(new KnowledgeI18nInfo("测试检索方式",
                "description"));
        when(this.knowledgeServiceRouter.getRouter(any(), anyString(), anyString())).thenReturn(this.invoker);
        when(this.invoker.invoke(any())).thenReturn(this.getExpectProperty());

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/knowledge-manager/properties")
                .param("groupId", this.buildMockGroupId())
                .responseType(KnowledgePropertyVo.class);
        this.response = this.mockMvc.perform(requestBuilder);

        Assertions.assertThat(this.response.statusCode()).isEqualTo(200);
        Assertions.assertThat(this.response.objectEntity()).isPresent();
        KnowledgePropertyVo property = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(property.getEnableIndexType()).hasSize(1)
                .extracting(KnowledgeProperty.IndexInfo::type,
                        KnowledgeProperty.IndexInfo::name,
                        KnowledgeProperty.IndexInfo::description)
                .containsExactly(Tuple.tuple(IndexType.SEMANTIC.value(), "语义检索", "使用向量进行文本相关查询"));

        assertThat(property.getDisableIndexType()).hasSize(2)
                .map(SchemaItem::type)
                .contains(IndexType.HYBRID.value(), IndexType.FULL_TEXT.value());
        assertThat(property.getFilterConfig()).hasSize(2);
        assertThat(property.getRerankConfig()).hasSize(1);
    }

    private KnowledgeProperty getExpectProperty() {
        KnowledgeProperty.IndexInfo indexType =
                new KnowledgeProperty.IndexInfo(IndexType.SEMANTIC, "语义检索", "使用向量进行文本相关查询");
        FlatFilterConfig topKFilter = new FlatFilterConfig(FilterConfig.custom()
                .name("引用上限")
                .description("检索最大引用的数量")
                .type(REFERENCE_TOP_K)
                .minimum(0)
                .maximum(10)
                .defaultValue(3)
                .build());
        FlatFilterConfig similarityFilter = new FlatFilterConfig(FilterConfig.custom()
                .name("最低相关度")
                .description("检索文本最低相关度")
                .type(SIMILARITY_THRESHOLD)
                .minimum(0)
                .maximum(1)
                .defaultValue(0.5f)
                .build());
        KnowledgeProperty.RerankConfig rerankConfig =
                new KnowledgeProperty.RerankConfig("boolean", "结果重排", "对检索结果进行重新排序", false);
        KnowledgeProperty expectProperty = new KnowledgeProperty(Collections.singletonList(indexType),
                Arrays.asList(topKFilter, similarityFilter),
                Collections.singletonList(rerankConfig));
        return expectProperty;
    }

    private String buildMockGroupId() {
        String className = this.service.getClass().getSimpleName();
        return "$Fit$" + this.toLowerCaseFirst(className);
    }

    private String toLowerCaseFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        char firstChar = str.charAt(0);
        char updatedFirstChar = Character.toLowerCase(firstChar);
        String remainder = str.substring(1);
        return updatedFirstChar + remainder;
    }
}