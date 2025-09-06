import { configureStore } from '@reduxjs/toolkit';
import collectionStore from './collection/collection';
import appStore from './appInfo/appInfo';
import chatCommonStore from './chatStore/chatStore';
import commonStore from './common/common';
import flowTestStore from "./flowTest/flowTest";
import toolHttpStore from "./toolHttp/toolHttp";

export const store = configureStore({
  reducer: {
    collectionStore,
    appStore,
    chatCommonStore,
    commonStore,
    flowTestStore,
    toolHttpStore
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
