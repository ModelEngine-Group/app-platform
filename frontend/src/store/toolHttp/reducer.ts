import { SET_INPUT_DATA } from './action-types';

const initialState = {
  inputData: null,
};

const toolHttpReducers = (state = initialState, action: any) => {
  switch (action.type) {
    case SET_INPUT_DATA:
      return { ...state, inputData: action.payload };
    default:
      return state;
  }
};

export default toolHttpReducers;
