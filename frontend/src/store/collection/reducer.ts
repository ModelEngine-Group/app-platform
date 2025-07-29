import { SET_CUR_APPID } from './action-types';

const initialState = {
  AppId: ''
}

const collectionReducers = (state = initialState, action) => {
  switch (action.type) {
    case SET_CUR_APPID:
      return { ...state, AppId: action.payload };
    default:
      return state
  }
}

export default collectionReducers