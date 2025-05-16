import {
  SET_HISTORY_SWITCH,
  SET_DIMENSION,
  SET_USE_MEMORY,
  SET_IS_DEBUG,
  SET_ALL_FIELDS,
  SET_IS_AUTO_OPEN,
  SET_IS_READ_ONLY,
} from './action-types';

export const setHistorySwitch = (item) => {
  return { type: SET_HISTORY_SWITCH, payload: item }
}
export const setDimension = (item) => {
  return { type: SET_DIMENSION, payload: item }
}
export const setUseMemory = (item) => {
  return { type: SET_USE_MEMORY, payload: item }
}
export const setIsDebug = (item) => {
  return { type: SET_IS_DEBUG, payload: item }
}
export const setAllFields = (item) => {
  return { type: SET_ALL_FIELDS, payload: item }
}
export const setIsAutoOpen = (item) => {
  return { type: SET_IS_AUTO_OPEN, payload: item }
}
export const setIsReadOnly = (item) => {
  return { type: SET_IS_READ_ONLY, payload: item }
}
