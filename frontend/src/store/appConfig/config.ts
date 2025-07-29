import { SET_CONFIG_DATA, SET_CONFIG_ITEM } from './config-types';

export const setConfigData = (item) => {
  return { type: SET_CONFIG_DATA, payload: item }
};

export const setConfigItem = (item) => {
  return { type: SET_CONFIG_ITEM, payload: item }
};
