import { createStore, combineReducers } from 'redux';
import chatReducers from './chatStore/reducer';
import appInfoReducers from './appInfo/reducer';
import collectionReducers from './collection/reducer';
import commonReducers from './common/reducer';
import flowTestReducers from './flowTest/reducer';
import toolHttpReducers from './toolHttp/reducer';
import appConfigReducers from './appConfig/reducer';

const rootReducer = combineReducers({
  chatCommonStore: chatReducers,
  appStore: appInfoReducers,
  collectionStore: collectionReducers,
  commonStore: commonReducers,
  flowTestStore: flowTestReducers,
  toolHttpStore: toolHttpReducers,
  appConfigStore: appConfigReducers,
});

const store = createStore(rootReducer);
export default store