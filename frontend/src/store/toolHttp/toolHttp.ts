import { SET_INPUT_DATA } from './action-types';

export const setInputData = (item: any) => {
  return { type: SET_INPUT_DATA, payload: item };
};
