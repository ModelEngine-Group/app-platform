import { getCookie } from "@/shared/utils/common";

export function bytesToSize(bytes) {
  if (!bytes) return '--';
  if (bytes === 0) return '0 B';
  let k = 1024,
    sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
    i = Math.floor(Math.log(bytes) / Math.log(k));
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

export function convertImgPath(path: string) {
  return new Promise((resolve, reject) => {
    fetch(path, {
      headers: {
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token')
      },
    })
    .then(response => response.blob())
    .then(blob => {
      const url = URL.createObjectURL(blob);
      resolve(url);
    });
  });
}

export function uuid(isLong) {
  if (isLong) {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  } else {
    let firstPart = (Math.random() * 46656) | 0;
    let secondPart = (Math.random() * 46656) | 0;
    firstPart = ("000" + firstPart.toString(36)).slice(-3);
    secondPart = ("000" + secondPart.toString(36)).slice(-3);
    return firstPart + secondPart;
  }
};
export const listFormate = (arr) => {
  let map: any = {};
  let res: any = [];
  for (let i = 0; i < arr.length; i++) {
    let ai = arr[i];
    if (!map[ai.repoId]) {
      map[ai.repoId] = [ai];
    } else {
      map[ai.repoId].push(ai);
    }
  }
  Object.keys(map).forEach(key => {
    res.push({
      repoId: key,
      data: map[key]
    });
  });
  return res;
}