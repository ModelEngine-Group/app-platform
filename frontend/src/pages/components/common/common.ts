export const APP_TYPE = {
  BASIC: {
    name: "basic",
    configId: "df87073b9bc85a48a9b01eccc9afccc4"
  },
  WORK_FLOW: {
    name: "workflow",
    configId: "df87073b9bc85a48a9b01eccc9afccc5",
  }
};

export enum NodeType {
  LLM = 'llmNodeState',
  KNOWLEDGE_RETRIEVAL = 'knowledgeRetrievalNodeState',
  RETRIEVAL = 'retrievalNodeState',
  END = 'endNodeEnd',
  MANUAL_CHECK = 'manualCheckNodeState',
  HUGGING_FACE = 'huggingFaceNodeState',
  TOOL_INVOKE = 'toolInvokeNodeState'
};

export const APP_BUILT_TYPE = {
  BASIC: 'BASIC',
  WORK_FLOW: 'WORK_FLOW',
};

export const APP_BUILT_CLASSIFICATION = {
  ASSISTANT: 'assistant',
  AGENT: 'agent',
  WORKFLOW: 'workflow',
};