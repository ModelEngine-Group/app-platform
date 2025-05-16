import { SET_HISTORY_SWITCH, SET_DIMENSION, SET_USE_MEMORY, SET_IS_DEBUG, SET_ALL_FIELDS, SET_IS_AUTO_OPEN, SET_IS_READ_ONLY } from './action-types';

const initialState = {
  historySwitch: false,
  dimension: {
    id: '',
    name: '',
    value: ''
  },
  useMemory: true,
  isDebug: false,
  allFields: [],
  isAutoOpen: false,
  isReadOnly: false
}

const commonReducers = (state = initialState, action) => {
  switch (action.type) {
    case SET_HISTORY_SWITCH:
      return { ...state, historySwitch: action.payload };
    case SET_DIMENSION:
      return { ...state, dimension: action.payload };
    case SET_USE_MEMORY:
      return { ...state, useMemory: action.payload };
    case SET_IS_DEBUG:
      return { ...state, isDebug: action.payload };
    case SET_ALL_FIELDS:
      return { ...state, allFields: action.payload };
    case SET_IS_AUTO_OPEN:
      return { ...state, isAutoOpen: action.payload };
    case SET_IS_READ_ONLY:
      return { ...state, isReadOnly: action.payload };
    default:
      return state;
  }
}

export default commonReducers;
