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

export const inIframe = () => {
  try {
      return window.self !== window.top;
  } catch (e) {
      return true;
  }
}
