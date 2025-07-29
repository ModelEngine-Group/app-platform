import { SET_CUR_APPID } from './action-types';

export const setCurAppId = (item) => {
  return { type: SET_CUR_APPID, payload: item }
}