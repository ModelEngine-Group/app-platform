/*---------------------------------------------------------------------------------------------*
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useRef } from 'react';
import {
  DashboardOutlined,
  BookOutlined,
  CheckCircleOutlined,
  CodeOutlined,
  ThunderboltOutlined,
  DatabaseOutlined,
  FileTextOutlined,
  BranchesOutlined,
  AppstoreOutlined,
  MessageOutlined,
  PlayCircleOutlined,
  SearchOutlined,
  SettingOutlined,
  ShareAltOutlined,
  ConsoleSqlOutlined,
  MenuOutlined,
  CloseOutlined,
  RightOutlined,
  LoadingOutlined,
  GithubOutlined,
  CopyOutlined,
  CheckOutlined,
} from '@ant-design/icons';
import hljs from 'highlight.js';
import 'highlight.js/styles/monokai-sublime.min.css';
import './index.less';

// 使用 require 导入中文文件名的图片
const wechatQrcode = require('../../assets/images/二维码.png');

// 瑞金医院病理推理成功案例
const ruijinCase = {
  partner: '上海交通大学医学院附属瑞金医院',
  title: 'RuiPath 病理推理 AI 应用',
  subtitle: '基于 Aido 应用编排平台，构建新一代智慧病理辅助诊断平台',
  icon: DatabaseOutlined,
  tag: '医疗 · 病理诊断',
  highlights: [
    {
      label: '覆盖范围',
      value: '90%',
      desc: '覆盖中国每年全癌种发病人数 90% 的常见癌种',
    },
    {
      label: '诊断准确率',
      value: '90%+',
      desc: '病理医生常见病理诊断问答准确率超过 90%',
    },
    {
      label: '诊断速度',
      value: '秒级',
      desc: '单切片 AI 诊断时间为秒级，实现更快的互动式辅助诊断',
    },
  ],
  capabilities: [
    '低代码 RAG 编排，支持多种知识库接入',
    '病理诊断业务流编排工具，实现不同癌种诊断界面差异化开发',
    '支持癌种识别、特征描述到对话式诊断生成',
    '提供病理报告生成示范应用',
  ],
  practiceDocUrl: 'https://gitcode.com/ModelEngine/doc/blob/main/%E5%8C%BB%E7%96%97%E8%A1%8C%E4%B8%9A%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5/%E5%8C%BB%E7%96%97%E8%A1%8C%E4%B8%9A%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5.md',
  videoUrl: 'https://modelengine-ai.com/resource/video/1.%E7%91%9E%E9%87%91%E7%97%85%E7%90%86%E5%A4%A7%E6%A8%A1%E5%9E%8B_Copilot%E8%BE%85%E5%8A%A9%E8%AF%8A%E7%96%97%E6%A8%A1%E5%BC%8F%EF%BC%88%E5%B8%A6%E8%AE%B2%E8%A7%A3+%E8%AF%8D%E6%9D%A1%EF%BC%89.mp4',
};

const buildModes = [
  {
    id: 'speed',
    icon: ThunderboltOutlined,
    title: '极速模式 (Prompt-to-App)',
    desc: '一句话描述需求，AI 自动生成完整应用配置。',
    placeholder: '帮我做一个分析销售合同风险的 Agent...',
    steps: [
      '解析意图：风险控制 / 合同审计',
      '自动挂载：OCR 识别插件, Legal-Bert 模型',
      '正在生成工作流配置...',
    ],
  },
  {
    id: 'visual',
    icon: AppstoreOutlined,
    title: '编排模式 (Visual Flow)',
    desc: '所见即所得的画布，内置 50+ 逻辑节点与数据算子。',
    placeholder: '从 CRM 获取客户合同，输出审核结论',
    steps: [
      '节点：数据入湖 → 模型推理 → 报告生成',
      '联调：Webhook、内网 API、工具插件',
      '版本：v2.4 / 最近更新 5 分钟前',
    ],
  },
  {
    id: 'code',
    icon: ConsoleSqlOutlined,
    title: '极客模式 (Code Extension)',
    desc: '通过 Python/Java 代码节点处理复杂逻辑，打破低代码边界。',
    placeholder: '在执行第 3 步时，调用风控内核自定义脚本',
    steps: [
      '挂载专有算子：RiskGuard.fn',
      '已连接：私有模型集群 / Kafka 流式输入',
      '状态：单元测试通过，准备部署',
    ],
  },
];

const bentoItems = [
  {
    icon: BranchesOutlined,
    title: '多智能体协作',
    desc: '支持层级式编排与任务路由，复杂业务拆解更可靠。',
    accent: 'purple',
  },
  {
    icon: ThunderboltOutlined,
    title: '模型自由切换',
    desc: '统一模型接入层，秒级切换主流 LLM 或私有模型。',
    accent: 'blue',
  },
  {
    icon: SettingOutlined,
    title: '全链路观测',
    desc: '调用链追踪、指标监控、异常告警一次打通。',
    accent: 'cyan',
  },
  {
    icon: ShareAltOutlined,
    title: '多渠道发布',
    desc: '一键生成 Web、小程序、API 网关多种形态。',
    accent: 'pink',
  },
];

const coreAbilities = [
  {
    title: '知识库',
    desc: '内置 ModelEngine 向量化知识库，亦可接入第三方或私有知识库。',
  },
  {
    title: '外部模型',
    desc: '适配主流大模型，支持通过标准化 API 连接私有推理服务。',
  },
  {
    title: '流程编排',
    desc: '拖拽式可视化编辑器，轻松搭建复杂工作流。',
  },
  {
    title: '自定义交互',
    desc: '将表单、图表等 UI 组件嵌入对话，打造任务型体验。',
  },
  {
    title: '调试与监控',
    desc: '提供实时调试与监控工具，快速定位性能与逻辑问题。',
  },
  {
    title: '评测体系',
    desc: '完善的评测指标与基准集，指导持续迭代优化。',
  },
];

const quickstartCode = `# 1. 获取源码
git clone https://github.com/ModelEngine-Group/app-platform.git
cd app-platform

# 2. 安装并启动数据库（PostgreSQL）
# 确保 PostgreSQL ≥ 14 已安装并运行
# 初始化数据库（在 shell 目录下执行，替换为实际数据库信息）
cd shell
sh build_win.sh <数据库IP> <数据库端口> <数据库用户名> <数据库密码>
cd ..

# 3. 编译后端代码
mvn clean install

# 4. 安装前端依赖
cd frontend
npm install --legacy-peer-deps --force

# 5. 启动前端开发服务器（终端窗口 1）
npm run start
# 前端服务将在 http://localhost:3310 启动

# 6. 启动后端服务（新终端窗口 2）
# 在 FIT 框架输出目录的 bin 目录下执行
# 注意：需要先将编译产物复制到 FIT 框架输出目录
cd <FIT框架输出目录>/bin
fit start
# 或使用调试模式
# fit debug`;

const resourceCards = [
  {
    title: '开发文档',
    desc: '完整的架构说明、API 参考、插件示例与最佳实践。',
    action: '查看 Docs',
    link: 'https://github.com/ModelEngine-Group/app-platform#readme',
    icon: BookOutlined,
  },
  {
    title: 'Release & Roadmap',
    desc: '了解每周构建、版本日志与路线规划，快速跟进最新能力。',
    action: '浏览 Release',
    link: 'https://github.com/ModelEngine-Group/app-platform/releases',
    icon: DashboardOutlined,
  },
  {
    title: '社区与贡献指南',
    desc: '加入 讨论群，共享 Issue、PR、技术分享会。',
    action: '加入社区',
    link: 'https://github.com/ModelEngine-Group/app-platform/blob/main/CONTRIBUTING.md',
    icon: MessageOutlined,
  },
];

const SuccessCasesSection: React.FC = () => (
  <section className="success-case-section">
    <div className="section-heading center">
      <div>
        <h2>成功案例</h2>
        <p className="section-desc">看看企业伙伴如何用 Aido 应用编排平台在关键业务场景中落地 AI 应用。</p>
      </div>
    </div>
    <div className="success-case-card">
      <div className="success-case-header">
        <div className="success-case-partner">
          <div className="success-case-icon">
            <ruijinCase.icon />
          </div>
          <div>
            <div className="success-case-tag">{ruijinCase.tag}</div>
            <h3>{ruijinCase.partner}</h3>
            <p className="success-case-title">{ruijinCase.title}</p>
          </div>
        </div>
      </div>
      <p className="success-case-subtitle">{ruijinCase.subtitle}</p>
      
      <div className="success-case-highlights">
        {ruijinCase.highlights.map((highlight, index) => (
          <div key={index} className="success-case-highlight">
            <div className="highlight-value">{highlight.value}</div>
            <div className="highlight-label">{highlight.label}</div>
            <div className="highlight-desc">{highlight.desc}</div>
          </div>
        ))}
      </div>

      <div className="success-case-video">
        <h4>案例演示视频</h4>
        <div className="video-wrapper">
          <video
            controls
            preload="metadata"
            poster=""
            className="success-video-player"
          >
            <source src={ruijinCase.videoUrl} type="video/mp4" />
            您的浏览器不支持视频播放。
          </video>
        </div>
        <p className="video-desc">观看 RuiPath 病理大模型 Copilot 辅助诊疗模式的完整演示</p>
      </div>

      <div className="success-case-capabilities">
        <h4>核心能力</h4>
        <ul>
          {ruijinCase.capabilities.map((capability, index) => (
            <li key={index}>
              <CheckCircleOutlined />
              <span>{capability}</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="success-case-footer">
        <a 
          href={ruijinCase.practiceDocUrl} 
          target="_blank" 
          rel="noreferrer"
          className="success-case-link"
        >
          <FileTextOutlined />
          查看详细落地实践文档
          <RightOutlined />
        </a>
      </div>
    </div>
  </section>
);

const BuildModesSection: React.FC = () => {
  const [activeId, setActiveId] = useState('visual');
  const activeMode = buildModes.find((mode) => mode.id === activeId) ?? buildModes[0];

  return (
    <section className="build-section" id="build-modes">
      <div className="section-heading center">
        <div>
          <h2>三种构建模式，分层满足</h2>
          <p className="section-desc">无论你是业务专家还是资深开发者，这里都有适合你的工具。</p>
        </div>
      </div>
      <div className="build-grid">
        <div className="build-tabs">
          {buildModes.map((mode) => (
            <button
              key={mode.id}
              type="button"
              className={`build-tab ${activeId === mode.id ? 'is-active' : ''}`}
              onClick={() => setActiveId(mode.id)}
            >
              <div className="build-tab-indicator" />
              <div className="build-tab-content">
                <mode.icon />
                <div>
                  <p>{mode.title}</p>
                  <span>{mode.desc}</span>
                </div>
              </div>
            </button>
          ))}
        </div>
        <div className="build-preview">
          <div className="preview-header">
            <div className="preview-dots">
              <span />
              <span />
              <span />
            </div>
            <div className="preview-title-bar">
              Aido Studio - {activeId === 'speed' ? 'Generator' : activeId === 'visual' ? 'Canvas' : 'IDE'}
            </div>
          </div>
          <div className="preview-body">
            {activeId === 'speed' && (
              <div className="preview-speed">
                <div className="preview-speed-header">
                  <h3>你想构建什么？</h3>
                  <p>只需描述，剩下的交给我们。</p>
                </div>
                <div className="preview-input-wrapper">
                  <input disabled type="text" value={activeMode.placeholder} />
                  <button type="button">
                    <ThunderboltOutlined />
                  </button>
                </div>
                <div className="preview-steps-list">
                  {activeMode.steps.map((step, index) => (
                    <div key={step} className="preview-step-item" style={{ animationDelay: `${index * 0.2}s`, opacity: 0 }}>
                      {index < activeMode.steps.length - 1 ? (
                        <CheckCircleOutlined className="step-icon success" />
                      ) : (
                        <LoadingOutlined className="step-icon loading" />
                      )}
                      <span>{step}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
            {activeId === 'visual' && (
              <div className="preview-visual">
                <div className="preview-node" style={{ top: '50%', left: '10%', transform: 'translateY(-50%)' }}>
                  <div className="node-header">
                    <MessageOutlined />
                    Start Input
                  </div>
                  <div className="node-content">
                    <div className="node-bar" />
                    <div className="node-bar short" />
                  </div>
                  <div className="node-connector right" />
                </div>
                <div className="preview-node" style={{ top: '25%', left: '50%', transform: 'translate(-50%, -25%)' }}>
                  <div className="node-header blue">
                    <SearchOutlined />
                    Knowledge Base
                  </div>
                  <div className="node-content">
                    <div className="node-bar" />
                  </div>
                  <div className="node-connector left" />
                  <div className="node-connector right" />
                </div>
                <div className="preview-node" style={{ bottom: '25%', left: '50%', transform: 'translate(-50%, 25%)' }}>
                  <div className="node-header yellow">
                    <CodeOutlined />
                    Code Parser
                  </div>
                  <div className="node-content">
                    <div className="node-bar" />
                  </div>
                  <div className="node-connector left" />
                  <div className="node-connector right" />
                </div>
                <div className="preview-node" style={{ top: '50%', right: '10%', transform: 'translateY(-50%)' }}>
                  <div className="node-header green">
                    <ThunderboltOutlined />
                    LLM Output
                  </div>
                  <div className="node-content">
                    <div className="node-bar" />
                  </div>
                  <div className="node-connector left" />
                </div>
                <svg className="preview-connections">
                  <path d="M 210 250 C 250 250, 250 160, 360 160" stroke="#6b7280" strokeWidth="2" fill="none" strokeDasharray="5,5" />
                  <path d="M 210 250 C 250 250, 250 340, 360 340" stroke="#6b7280" strokeWidth="2" fill="none" />
                  <path d="M 520 160 C 580 160, 580 250, 660 250" stroke="#6b7280" strokeWidth="2" fill="none" />
                  <path d="M 520 340 C 580 340, 580 250, 660 250" stroke="#6b7280" strokeWidth="2" fill="none" />
                </svg>
              </div>
            )}
            {activeId === 'code' && (
              <div className="preview-code">
                <div className="code-tabs">
                  <span className="code-tab active">custom_tool.py</span>
                  <span className="code-tab">utils.java</span>
                </div>
                <div className="code-content">
                  <div className="code-line comment"># Define a custom data processing tool</div>
                  <div className="code-line">
                    <span className="keyword">def</span> <span className="function">process_financial_data</span>(input_data):
                  </div>
                  <div className="code-line indent">
                    <span className="string">"""Custom logic to handle CSV parsing"""</span>
                  </div>
                  <div className="code-line indent">clean_data = []</div>
                  <div className="code-line indent">
                    <span className="keyword">for</span> row <span className="keyword">in</span> input_data:
                  </div>
                  <div className="code-line indent-2">
                    <span className="keyword">if</span> row[<span className="string">'value'</span>] &gt; <span className="number">1000</span>:
                  </div>
                  <div className="code-line indent-3">risk_score = calculate_risk(row)</div>
                  <div className="code-line indent-3">clean_data.append({'{'}</div>
                  <div className="code-line indent-4">
                    <span className="string">'id'</span>: row[<span className="string">'id'</span>],
                  </div>
                  <div className="code-line indent-4">
                    <span className="string">'score'</span>: risk_score
                  </div>
                  <div className="code-line indent-3">{'}'})</div>
                  <div className="code-line indent">
                    <span className="keyword">return</span> clean_data
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  );
};

const BentoGridSection: React.FC = () => (
  <section className="bento-section">
    <div className="section-heading center">
      <div>
        <h2>超越简单的中间件，深入模型底层</h2>
        <p className="section-desc">我们解决了 Dify 等轻量级平台无法处理的复杂数据工程难题。</p>
      </div>
    </div>
    <div className="bento-grid">
      <article className="bento-card large">
        <div className="bento-card-glow" />
        <div className="bento-icon">
          <DatabaseOutlined />
        </div>
        <h3>专业级 RAG 数据清洗</h3>
        <p>内置 50+ ETL 算子（去重、分段、QA提取），支持 Markdown/PDF 表格解析，让知识库不再是垃圾堆。</p>
        <div className="bento-etl-visual">
          <div className="etl-item">
            <div className="etl-label">Raw Data</div>
            <div className="etl-box">
              <div className="etl-bar" />
              <div className="etl-bar" />
              <div className="etl-bar error" />
              <div className="etl-bar" />
            </div>
          </div>
          <RightOutlined className="etl-arrow" />
          <div className="etl-operators">
            <div className="etl-operator purple">去重</div>
            <div className="etl-operator blue">分块</div>
          </div>
          <RightOutlined className="etl-arrow" />
          <div className="etl-item">
            <div className="etl-label">Vector DB</div>
            <div className="etl-box clean">
              <div className="etl-bar clean" />
              <div className="etl-bar clean" />
              <div className="etl-bar clean" />
            </div>
          </div>
        </div>
      </article>
      <article className="bento-card tall">
        <div className="bento-icon">
          <BookOutlined />
        </div>
        <h3>企业级知识库构建</h3>
        <p>一站式完成非结构化数据的解析、分块与向量化，快速构建高精度企业知识库，支持混合检索。</p>
        <div className="bento-queue">
          <div className="queue-header">
            <span>Indexing Queue</span>
            <span className="queue-status">Active</span>
          </div>
          <div className="queue-list">
            <div className="queue-item">
              <FileTextOutlined />
              <span>Product_Manual.pdf</span>
              <CheckCircleOutlined className="queue-check" />
            </div>
            <div className="queue-item">
              <FileTextOutlined />
              <span>Sales_Q3.docx</span>
              <CheckCircleOutlined className="queue-check" />
            </div>
            <div className="queue-item processing">
              <FileTextOutlined />
              <span>Legal_Terms.txt</span>
              <LoadingOutlined className="queue-loading" />
            </div>
          </div>
        </div>
      </article>
      <article className="bento-card small">
        <div className="bento-card-top">
          <div className="bento-icon">
            <SettingOutlined />
          </div>
          <div className="bento-model-badges">
            <span>GPT</span>
            <span>Lla</span>
          </div>
        </div>
        <h4>多模型路由</h4>
        <p>一键切换 GPT-4, Claude 或私有模型，自动降级熔断。</p>
      </article>
      <article className="bento-card small">
        <div className="bento-card-top">
          <div className="bento-icon">
            <DashboardOutlined />
          </div>
          <span className="bento-uptime">99.9% Uptime</span>
        </div>
        <h4>全链路监控</h4>
        <p>Token 消耗、响应延迟、用户反馈大盘，尽在掌握。</p>
      </article>
    </div>
  </section>
);

const OpenSourceSection: React.FC = () => {
  const features = [
    {
      icon: DatabaseOutlined,
      title: 'RAG 工作流编排',
      desc: '支持编排 RAG 检索、重排、结果过滤与校验等完整链路。可接入 ModelEngine 知识库或第三方知识库插件，灵活构建检索策略。',
      color: 'purple',
    },
    {
      icon: BranchesOutlined,
      title: '智能体编排与协作',
      desc: '支持层级式编排与任务路由，复杂业务拆解更可靠。多智能体协作，让 AI 应用更强大。',
      color: 'blue',
    },
    {
      icon: ThunderboltOutlined,
      title: '模型自由切换',
      desc: '统一模型接入层，秒级切换主流 LLM 或私有模型。支持 GPT-4、Claude、文心一言等主流模型。',
      color: 'cyan',
    },
    {
      icon: CodeOutlined,
      title: 'MCP 工具接入',
      desc: '无缝接入 Model Context Protocol 工具生态，扩展 AI 应用能力边界，连接更多外部服务。',
      color: 'green',
    },
    {
      icon: AppstoreOutlined,
      title: '富媒体对话交互',
      desc: '对话过程中，AI 回复可嵌入表单、图表等富媒体组件。支持简单编排表单，也可插入自定义前端组件，突破纯文本对话限制。',
      color: 'indigo',
    },
    {
      icon: SearchOutlined,
      title: '插件化知识库接入',
      desc: '支持通过插件接入任意第三方知识库，只需开发对应插件并上传即可接入其检索能力，也可配合 ModelEngine 知识库使用。',
      color: 'teal',
    },
    {
      icon: SettingOutlined,
      title: '自托管级全链路观测',
      desc: '支持 Kubernetes / Docker Compose 等私有环境部署，并内置调用链追踪、指标监控与异常告警。',
      color: 'orange',
    },
    {
      icon: ShareAltOutlined,
      title: '企业级可扩展',
      desc: '插件体系与 SDK 统一管理，快速集成内网 API、数据湖或专有模型集群，满足复杂业务扩展。',
      color: 'pink',
    },
  ];

  return (
    <section className="oss-section">
      <div className="section-heading center">
        <div>
          <h2>真正属于你的开源 AI 应用平台</h2>
          <p className="section-desc">Aido 以 MIT 许可完全开源，不仅是一个智能体平台，更是企业级 AI 应用的全栈解决方案。所有能力原生支持自托管，按需裁剪并嵌入你的业务系统。</p>
        </div>
      </div>
      <div className="oss-features-grid">
        {features.map((feature, index) => (
          <article key={feature.title} className="oss-feature-card" style={{ animationDelay: `${index * 0.1}s` }}>
            <div className={`oss-feature-icon ${feature.color}`}>
              <feature.icon />
            </div>
            <h3>{feature.title}</h3>
            <p>{feature.desc}</p>
          </article>
        ))}
      </div>
    </section>
  );
};

const QuickstartSimpleSection: React.FC = () => {
  const steps = [
    'git clone https://github.com/ModelEngine-Group/app-platform.git',
    'cd app-platform',
    'cp docker/.env.example docker/.env',
    'bash docker/deploy.sh',
  ];

  return (
    <section className="quickstart-simple">
      <div className="section-heading center">
        <div>
          <h2>5 分钟启动 Aido</h2>
          <p className="section-desc">保留最核心的 4 步，让你快速启动并验证整套工程。</p>
        </div>
      </div>
      <div className="quickstart-terminal">
        <div className="terminal-header">
          <span className="dot red" />
          <span className="dot yellow" />
          <span className="dot green" />
        </div>
        <pre className="quickstart-terminal-body">
          <code>{steps.join('\n')}</code>
        </pre>
      </div>
    </section>
  );
};

const DeveloperResourcesSection: React.FC = () => (
  <section className="resource-section">
    <div className="section-heading center">
      <div>
        <h2>开发者资源一站直达</h2>
        <p className="section-desc">Docs、Release、社区、贡献指南全在这里，持续输出工程实践。</p>
      </div>
    </div>
    <div className="resource-grid">
      {resourceCards.map((card) => (
        <article key={card.title} className="resource-card">
          <div className="resource-icon">
            <card.icon />
          </div>
          <h3>{card.title}</h3>
          <p>{card.desc}</p>
          <a href={card.link} target="_blank" rel="noreferrer">
            {card.action}
            <RightOutlined />
          </a>
        </article>
      ))}
    </div>
  </section>
);


const Footer: React.FC = () => (
  <footer className="welcome-footer">
    <div className="footer-cta">
      <div>
        <h2>拉取代码，启动你的专属AI应用平台</h2>
        <p>Star、Fork、提交 PR，与数千名开发者并肩建设 ModelEngine。</p>
      </div>
      <div className="footer-cta-actions">
        <a className="footer-btn primary" href="https://github.com/ModelEngine-Group/app-platform" target="_blank" rel="noreferrer">
          前往 GitHub
        </a>
      </div>
    </div>
    <div className="footer-links">
      <div className="footer-column">
        <h4>开源项目</h4>
        <ul>
          <li><a href="https://github.com/ModelEngine-Group/app-platform#readme" target="_blank" rel="noreferrer">项目概览</a></li>
          <li><a href="https://github.com/ModelEngine-Group/app-platform/releases" target="_blank" rel="noreferrer">版本发布</a></li>
          <li><a href="https://github.com/ModelEngine-Group/app-platform/blob/main/CONTRIBUTING.md" target="_blank" rel="noreferrer">贡献指南</a></li>
          <li><a href="https://github.com/ModelEngine-Group/app-platform/issues" target="_blank" rel="noreferrer">Issue 列表</a></li>
        </ul>
      </div>
      <div className="footer-column">
        <h4>开发资源</h4>
        <ul>
          <li><a href="https://github.com/ModelEngine-Group/app-platform#readme" target="_blank" rel="noreferrer">开发文档</a></li>
          <li><a href="https://github.com/ModelEngine-Group/app-platform/tree/main/examples" target="_blank" rel="noreferrer">示例与模板</a></li>
          <li><a href="https://github.com/ModelEngine-Group/app-platform/discussions" target="_blank" rel="noreferrer">Discussions</a></li>
        </ul>
      </div>
      <div className="footer-column footer-follow">
        <h4>关注我们</h4>
        <p className="footer-follow-desc">扫码加入技术交流群，掌握产品更新、获取技术支持，与更多开发者协作。</p>
        <div className="footer-qrcodes">
          <div className="footer-qrcode-item">
            <div className="footer-qrcode-media">
              <img 
                src={wechatQrcode} 
                alt="微信二维码"
              />
            </div>
            <p className="footer-qrcode-label">微信扫码加入社区</p>
          </div>
          <div className="footer-qrcode-item">
            <div className="footer-qrcode-media">
              <img 
                src="https://modelengine-ai.com/assets/fit-wechat-account.6e8dca4b.png" 
                alt="微信公众号"
              />
            </div>
            <p className="footer-qrcode-label">关注微信公众号</p>
          </div>
        </div>
      </div>
    </div>
    <div className="footer-bottom">&copy; 2025 ModelEngine Group · MIT Licensed</div>
  </footer>
);


const HeroSection: React.FC<{ experienceUrl: string }> = ({ experienceUrl }) => (
    <section className="hero-section">
      <div className="hero-backdrop" />
      <div className="hero-backdrop secondary" />
      <div className="hero-grid">
        <div className="hero-left">
          <h1>
            构建懂业务的 <br />
            <span className="hero-gradient-text">超级AI应用</span>
          </h1>
          <p>
            Aido 专为工程团队打造：拉取源码即可本地运行，开箱支持 ETL + RAG、智能体编排、MCP工具接入、插件体系与企业级监控，帮助你在自有环境快速落地复杂 AI 应用。
          </p>
          <div className="hero-actions">
            <a className="cta-btn primary" href="https://github.com/ModelEngine-Group/app-platform" target="_blank" rel="noreferrer">
              <GithubOutlined />
              Star &amp; Fork
            </a>
            <a className="cta-btn ghost" href={experienceUrl} target="_blank" rel="noreferrer">
              <PlayCircleOutlined />
              在线体验
            </a>
          </div>
          <div className="hero-metrics">
            <div>
              <h3>1.4K+</h3>
              <p>GitHub Stars</p>
            </div>
            <div>
              <h3>23+</h3>
              <p>社区贡献者</p>
            </div>
            <div>
              <h3>MIT</h3>
              <p>完全开源许可</p>
            </div>
          </div>
        </div>
        <div className="hero-right">
          <div className="hero-card data animate-slow">
            <div className="hero-card__header">
              <DatabaseOutlined />
              <span>orders_stream.json</span>
            </div>
            <div className="hero-card__bars">
              {Array.from({ length: 5 }).map((_, index) => (
                <span key={index} style={{ animationDelay: `${index * 0.1}s` }} />
              ))}
            </div>
          </div>
          <div className="hero-card workflow animate-medium">
            <div className="hero-card__header">
              <BranchesOutlined />
              <span>Flow Monitor</span>
            </div>
            <div className="hero-nodes">
              <span className="node purple">Webhook</span>
              <span className="node blue">规则引擎</span>
              <span className="node green">CRM Sync</span>
              <svg className="hero-lines">
                <path d="M 40 40 Q 80 40, 90 75 T 150 60" stroke="rgba(255,255,255,0.1)" fill="none" strokeWidth="2" />
                <path d="M 40 110 Q 120 140, 200 90" stroke="rgba(255,255,255,0.1)" fill="none" strokeWidth="2" />
              </svg>
            </div>
          </div>
          <div className="hero-card chat animate-fast">
            <div className="hero-chat__row">
              <span className="traffic">
                <i />
                <i />
                <i />
              </span>
              <span className="preview-mode">
                <span className="status-dot" />
                实时联络
              </span>
            </div>
            <div className="hero-chat__bubble user">
              <div className="chat-avatar" />
              <div className="chat-message">请同步订单 #A132 的最新物流状态并告知客服团队。</div>
            </div>
            <div className="hero-chat__bubble bot">
              <div className="chat-avatar bot-avatar">
                <ThunderboltOutlined />
              </div>
              <div className="chat-message">
                <p>
                  正在执行 <strong>订单回流流程</strong>
                  <LoadingOutlined className="chat-loading" />
                </p>
                <div className="hero-console">
                  <code>&gt; Fetching tracking info... OK</code>
                  <code>&gt; Updating CRM status... OK</code>
                  <span className="console-cursor" />
                </div>
                <p className="chat-result">已完成同步，订单 #A132 - 预计 2 小时内送达，客服已收到通知。</p>
                <div className="hero-tag">CRM Sync 11:45</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );

const Navbar: React.FC<{ freeTrialUrl: string }> = ({ freeTrialUrl }) => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav className={`welcome-navbar ${isScrolled ? 'scrolled' : ''}`}>
      <div className="navbar-content">
        <div className="navbar-logo">
          <div className="logo-icon">A</div>
          <span className="logo-text">Aido</span>
        </div>
        <div className="navbar-actions">
          <a
            className="navbar-link-doc"
            href="https://modelengine-ai.com/#/docs/zh_CN/model-engine-intro"
            target="_blank"
            rel="noreferrer"
          >
            文档
          </a>
          <a
            className="navbar-btn-ghost"
            href="https://github.com/ModelEngine-Group/app-platform"
            target="_blank"
            rel="noreferrer"
          >
            <GithubOutlined />
            GitHub
          </a>
          <a className="navbar-btn-primary" href={freeTrialUrl} target="_blank" rel="noreferrer">免费体验</a>
                </div>
        <div className="navbar-mobile-toggle" onClick={() => setMobileMenuOpen(!mobileMenuOpen)}>
          {mobileMenuOpen ? <CloseOutlined /> : <MenuOutlined />}
                </div>
                </div>
      {mobileMenuOpen && (
        <div className="navbar-mobile-menu">
          <a
            className="navbar-link-doc"
            href="https://modelengine-ai.com/#/docs/zh_CN/model-engine-intro"
            target="_blank"
            rel="noreferrer"
            onClick={() => setMobileMenuOpen(false)}
          >
            文档
          </a>
          <a
            className="navbar-btn-ghost"
            href="https://github.com/ModelEngine-Group/app-platform"
            target="_blank"
            rel="noreferrer"
          >
            <GithubOutlined />
            GitHub
          </a>
          <a
            className="navbar-btn-primary"
            href={freeTrialUrl}
            target="_blank"
            rel="noreferrer"
            onClick={() => setMobileMenuOpen(false)}
          >
            免费开始
          </a>
        </div>
      )}
    </nav>
  );
};

/**
 * 欢迎页面组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const WelcomePage: React.FC = () => {
  const experienceUrl = '/#/home';
  useEffect(() => {
    if (typeof window === 'undefined') {
      return;
    }
    const handlePageShow = (event: Event) => {
      const pageEvent = event as Event & { persisted?: boolean };
      if (pageEvent.persisted) {
        window.location.reload();
      }
    };
    window.addEventListener('pageshow', handlePageShow);
    return () => window.removeEventListener('pageshow', handlePageShow);
  }, []);

  return (
    <div className="welcome-layout gpu-safe-mode">
      <Navbar freeTrialUrl={experienceUrl} />
      <div className="welcome-content">
        <HeroSection experienceUrl={experienceUrl} />
        <OpenSourceSection />
        <QuickstartSimpleSection />
        <SuccessCasesSection />
        {/* <BuildModesSection /> */}
        <DeveloperResourcesSection />
        <Footer />
      </div>
    </div>
  );
};

export default WelcomePage;