/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { createContext } from 'react';

export const AippContext = createContext({
  agent: null,
  showElsa: false,
  messageChecked: false,
  chatRunning: false,
  updateAippCallBack: null,
  setRequestLoading: '',
  reloadInspiration: ''
});
export const ChatContext = createContext({
  setShareClass: (type: string) => {},
  setInspiration: null,
  checkCallBack: () => {},
  addInspirationCb: () => {},
  tenantId: '',
  showCheck: false,
  chatStreaming: {},
  handleRejectClar: (params:any) => {},
  conditionConfirm: (params:any, logId?: string) => {},
  questionClarConfirm: (params:any, logId?: string) => {},
  useMemory: true
});

export const FlowContext = createContext({
  appInfo: {},
  type: '',
  modalInfo: {},
  showTime: false,
  setShowTime: () => {},
  setModalInfo: (params:any) => {},
  setFlowInfo: (params:any) => {}
});

export const RenderContext = createContext({
  renderRef: { current: false } as React.MutableRefObject<boolean>,
  elsaReadOnlyRef: { current: false } as React.MutableRefObject<boolean>
})
