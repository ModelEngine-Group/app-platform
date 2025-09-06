import { SET_TEST_STATUS, SET_TEST_TIME } from './action-types';

export const setTestStatus = (item) => {
  return { type: SET_TEST_STATUS, payload: item }
}
export const setTestTime = (item) => {
  return { type: SET_TEST_TIME, payload: item }
}