/*************************************************请勿修改或删除该文件**************************************************/
import { createContext } from 'react';

export const DataContext = createContext({
  data: {},
  terminateClick: (params) => {},
  resumingClick: (params) => {},
  restartClick: (params) => {},
});