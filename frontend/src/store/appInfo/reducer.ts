import { 
  SET_APP_ID, 
  SET_APP_INFO, 
  SET_TENANT_ID, 
  SET_AT_APP_ID, 
  SET_AT_APP_INFO, 
  SET_VALIDATE_INFO, 
  SET_NODE_ID, 
  SET_AGENT_INFO,
  SET_AIPP_ID, 
  SET_APP_VERSION } from './action-types';
import { TENANT_ID } from '../../pages/chatPreview/components/send-editor/common/config';

const initialState = {
  appId: '',
  tenantId: TENANT_ID,
  appInfo: {},
  atAppId: null,
  atAppInfo: null,
  validateInfo: [],
  choseNodeId: '',
  agentInfo: {},
  aippId: '',
  appVersion: ''
}

const appInfoReducers = (state = initialState, action) => {
  switch (action.type) {
    case SET_APP_ID:
      return { ...state, appId: action.payload };
    case SET_APP_INFO:
      return { ...state, appInfo: action.payload };
    case SET_TENANT_ID:
      return { ...state, tenantId: action.payload };
    case SET_AT_APP_ID:
      return { ...state, atAppId: action.payload };
    case SET_AT_APP_INFO:
      return { ...state, atAppInfo: action.payload };
    case SET_VALIDATE_INFO:
      return { ...state, validateInfo: action.payload };
    case SET_NODE_ID:
      return { ...state, choseNodeId: action.payload };
    case SET_AGENT_INFO:
      return { ...state, agentInfo: action.payload };
    case SET_AIPP_ID:
      return { ...state, aippId: action.payload };
    case SET_APP_VERSION:
      return { ...state, appVersion: action.payload };
    default:
      return state
  }
}

export default appInfoReducers