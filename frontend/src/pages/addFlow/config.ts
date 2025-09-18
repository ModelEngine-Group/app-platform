/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import i18n from '../../locale/i18n';
import chatScreenshot from '../../assets/images/appConfig/chat-screenshot.jpg';
import heatmapScreenshot from '../../assets/images/appConfig/heatmap-screenshot.jpg';
const { origin } = window.location;
let baseUrl = '';
if (process.env.PACKAGE_MODE === 'spa') {
  baseUrl = `${origin}/appbuilder`;
} else {
  baseUrl = `${origin}/api/jober`;
}

export const configMap = {
  'development': {
    CONFIGS: [
      {
        node: "startNodeStart",
        urls: {
          customHistoryUrl: ""
        },
        appConfig: {
          appChatStyle: {
            name: 'appChatStyle',
            label: 'appChatStyle',
            options: [
              {
                value: 'default',
                label: i18n.t('default'),
                image: chatScreenshot,
              },
              {
                value: 'heatMap',
                label: i18n.t('heatMap'),
                image: heatmapScreenshot,
              },
            ],
            rules: [{required: true, message: 'appChatStyleCannotBeEmpty'}],
          },
        }
      },
      {
        node: "llmNodeState",
        urls: {
          llmModelEndpoint: "/modelApi/v1/api",
          toolListEndpoint: `${baseUrl}/store/plugins/tools`,
          workflowListEndpoint: ""
        },
        params: {
          tenantId: '',
          appId: '',
        }
      },
      {
        node: "manualCheckNodeState",
        urls: {
          runtimeFormUrl: `/elsaApi/v1/api/${TENANT_ID}/form/type/runtime`
        }
      },
      {
        node: "knowledgeState",
        urls: { knowledgeUrl: `` }
      },
      {
        node: "knowledgeRetrievalNodeState",
        urls: {
          llmModelEndpoint: `${baseUrl}/v1/api`
        }
      },
      {
        node: "fitInvokeState",
        urls: {
          serviceListEndpoint: "",
          fitableMetaInfoUrl: ""
        }
      },
      {
        node: "toolInvokeNodeState",
        urls: {
          versionInfo: "/elsaApi/v1/api/{tenant}/app/published/unique_name/{uniqueName}"
        }
      },
      {
        node: "codeNodeState",
        urls: {
          testCodeUrl: `${baseUrl}/v1/api/code/run`
        }
      },
      {
        node: 'evaluationAlgorithmsNodeState', urls: {
          evaluationAlgorithmsUrl: '',
        },
      },
      {
        node: 'evaluationTestSetNodeState', urls: {
          datasetUrlPrefix: '',
        },
      },{
        node: 'queryOptimizationNodeState',
        urls: {
          llmModelEndpoint: `/llmApi/v1/api/fetch/model-list`,
        },
      }, {
        node: 'textExtractionNodeState',
        urls: {
          llmModelEndpoint: `/v1/api/fetch/model-list`,
        },
      },{
        node: "questionClassificationNodeCondition",
        urls: {
          llmModelEndpoint: `/llmApi/v1/api/fetch/model-list`,
        }
      }
    ]
  },
  'production': {
    CONFIGS: [
      {
        node: "startNodeStart",
        urls: {
          customHistoryUrl: `${baseUrl}/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0`
        },
        appConfig: {
          appChatStyle: {
            name: 'appChatStyle',
            label: 'appChatStyle',
            options: [
              {
                value: 'default',
                label: i18n.t('default'),
                image: chatScreenshot,
              },
              {
                value: 'heatMap',
                label: i18n.t('heatMap'),
                image: heatmapScreenshot,
              },
            ],
            rules: [{required: true, message: 'appChatStyleCannotBeEmpty'}],
          },
        }
      },
      {
        node: "llmNodeState",
        urls: {
          llmModelEndpoint: `${baseUrl}/v1/api`,
          toolListEndpoint: `${baseUrl}/store/plugins/tools`,
          workflowListEndpoint: `${baseUrl}`,
          aippUrl: `${baseUrl}/v1/api`
        },
        params: {
          tenantId: '',
          appId: '',
        }
      },
      {
        node: "manualCheckNodeState",
        urls: {
          runtimeFormUrl: `${baseUrl}/v1/api/${TENANT_ID}/form/type/runtime`
        }
      },
      {
        node: "knowledgeState",
        urls: {
          knowledgeUrl: `${baseUrl}/v1/api/${TENANT_ID}/knowledge?pageNum=1&pageSize=10`
        }
      },
      {
        node: "knowledgeRetrievalNodeState",
        urls: {
          llmModelEndpoint: `${baseUrl}/v1/api`
        }
      },
      {
        node: "toolInvokeNodeState",
        urls: {
          versionInfo: `${baseUrl}/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
        }
      },
      {
        node: "codeNodeState",
        urls: {
          testCodeUrl: `${baseUrl}/v1/api/code/run`
        }
      },
      {
        node: 'evaluationAlgorithmsNodeState', urls: {
          evaluationAlgorithmsUrl: `${baseUrl}/store/plugins/tools/search`,
        },
      },
      {
        node: 'evaluationTestSetNodeState', urls: {
          datasetUrlPrefix: `${baseUrl}/eval`,
        },
      },{
        node: 'queryOptimizationNodeState',
        urls: {
          llmModelEndpoint: `${baseUrl}/v1/api/fetch/model-list`,
        },
      },{
        node: 'textExtractionNodeState',
        urls: {
          llmModelEndpoint: `${baseUrl}/v1/api/fetch/model-list`,
        },
      },
      {
        node: "questionClassificationNodeCondition",
        urls: {
          llmModelEndpoint: `${baseUrl}/v1/api/fetch/model-list`,
        }
      }
    ]
  }
}
