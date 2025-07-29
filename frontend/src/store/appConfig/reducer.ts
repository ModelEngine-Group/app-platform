import { SET_CONFIG_DATA, SET_CONFIG_ITEM } from './config-types';

const initialState = {
  inputConfigData: {}
};

const appConfigReducers = (state = initialState, action) => {
  switch (action.type) {
    case SET_CONFIG_DATA:
      return { inputConfigData: action.payload };
    case SET_CONFIG_ITEM:
      const { key, value } = action.payload;
      if(state.inputConfigData && state.inputConfigData[key]) {
        state.inputConfigData[key].defaultValue = value;
      }
      return state;
    default:
      return state;
  }
};

export default appConfigReducers;