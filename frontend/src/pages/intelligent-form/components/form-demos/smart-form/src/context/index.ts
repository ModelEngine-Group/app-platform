/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/*************************************************请勿修改或删除该文件**************************************************/
import { createContext } from 'react';

interface ContextProps {
  data: object,
  terminateClick: () => {},
  resumingClick: () => {},
  restartClick: () => {},
}

export const DataContext = createContext({
  data: null,
  terminateClick: null,
  resumingClick: null,
  restartClick: null,
});
