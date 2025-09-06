/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/*************************************************请勿修改或删除getQueryParams方法**************************************************/
export const getQueryParams = (url) => {
  const regex = /uniqueId=([a-zA-Z0-9-]+)/;
  const match = url.match(regex);
  if (match && match.length > 1) {
    return match[1];
  } else {
    return null;
  } 
}
