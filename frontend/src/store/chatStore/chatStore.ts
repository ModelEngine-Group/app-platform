import { 
  SET_CHAT_ID,
  SET_CHAT_LIST,
  SET_OPEN_STAR,
  SET_INSPIRATION_OPEN,
  SET_CHAT_RUNNING,
  SET_CHAT_TYPE,
  SET_AT_CHAT_ID,
  SET_FORM_RECEIVED,
  SET_LOGIN_STATUS,
  SET_REFERENCE,
  SET_REFERENCE_LIST,
  SET_READ_ONLY,
  SET_NO_AUTH,
  SET_USER_ROLE,
  SET_PLUGIN_LIST,
  SET_CURRENT_ANSWER,
  SET_IS_CURRENT_ANSWER
} from './action-types';

export const setChatId = (item) => {
  return { type: SET_CHAT_ID, payload: item }
}
export const setChatList = (item) => {
  return { type: SET_CHAT_LIST, payload: item }
}
export const setOpenStar = (item) => {
  return { type: SET_OPEN_STAR, payload: item }
}
export const setInspirationOpen = (item) => {
  return { type: SET_INSPIRATION_OPEN, payload: item }
}
export const setChatRunning = (item) => {
  return { type: SET_CHAT_RUNNING, payload: item }
}
export const setChatType = (item) => {
  return { type: SET_CHAT_TYPE, payload: item }
}
export const setAtChatId = (item) => {
  return { type: SET_AT_CHAT_ID, payload: item }
}
export const setFormReceived = (item) => {
  return { type: SET_FORM_RECEIVED, payload: item }
}
export const setLoginStatus = (item) => {
  return { type: SET_LOGIN_STATUS, payload: item }
}
export const setReference = (item) => {
  return { type: SET_REFERENCE, payload: item }
}
export const setReferenceList = (item) => {
  return { type: SET_REFERENCE_LIST, payload: item }
}
export const setUserRole = (item) => {
  return { type: SET_USER_ROLE, payload: item }
}
export const setReadOnly = (item) => {
  return { type: SET_READ_ONLY, payload: item }
}
export const setNoAuth = (item) => {
  return { type: SET_NO_AUTH, payload: item }
}
export const setPluginList = (item) => {
  return { type: SET_PLUGIN_LIST, payload: item }
}
export const setCurrentAnswer = (item) => {
  return { type: SET_CURRENT_ANSWER, payload: item }
}
export const setIsCurrentAnswer = (item) => {
  return { type: SET_IS_CURRENT_ANSWER, payload: item }
}
