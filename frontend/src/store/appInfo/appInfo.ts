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
  SET_APP_VERSION 
} from './action-types';

export const setAppId = (item) => {
  return { type: SET_APP_ID, payload: item }
}
export const setAppInfo = (item) => {
  return { type: SET_APP_INFO, payload: item }
}
export const setTenantId = (item) => {
  return { type: SET_TENANT_ID, payload: item }
}
export const setAtAppId = (item) => {
  return { type: SET_AT_APP_ID, payload: item }
}
export const setAtAppInfo = (item) => {
  return { type: SET_AT_APP_INFO, payload: item }
}
export const setValidateInfo = (item) => {
  return { type: SET_VALIDATE_INFO, payload: item }
}
export const setChoseNodeId = (item) => {
  return { type: SET_NODE_ID, payload: item }
}
export const setAgentInfo = (item) => {
  return { type: SET_AGENT_INFO, payload: item }
}
export const setAippId = (item) => {
  return { type: SET_AIPP_ID, payload: item }
}
export const setAppVersion = (item) => {
  return { type: SET_APP_VERSION, payload: item }
}
