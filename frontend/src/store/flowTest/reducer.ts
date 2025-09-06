import { SET_TEST_STATUS, SET_TEST_TIME } from './action-types';

const initialState = {
  testStatus: null,
  testTime: null
}

const flowTestReducers = (state = initialState, action) => {
  switch (action.type) {
    case SET_TEST_STATUS:
      return { ...state, testStatus: action.payload };
    case SET_TEST_TIME:
      return { ...state, testTime: action.payload };
    default:
      return state
  }
}

export default flowTestReducers