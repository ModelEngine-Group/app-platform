import moment from 'moment';
import i18n from '../locale/i18n';

export function formatLocalDate(date: number | string | Date): string {
  const isNil = date === undefined || date === null || date === '';
  if (isNil || date === '--') {
    return '--';
  }
  let curDate = date;
  if (typeof date === 'number' && date.toString().length === 10) {
    curDate *= 1000;
  }
  const res = moment(curDate).format('YYYY-MM-DD HH:mm:ss');
  return res;
}
// 返回格式为xx小时xx分钟xx秒
export function getExecTime(startTime, endTime): string {
  const stime = new Date(startTime).getTime();
  const etime = new Date(endTime).getTime();
  // 两个时间戳相差的毫秒数
  const usedTime = etime - stime;

  if (usedTime < 0) {
    return i18n.t('zeroCostTime');
  }

  // 计算出小时数
  const hours = Math.floor(usedTime / (3600 * 1000));
  // 计算小时数后剩余的毫秒数
  const leave1 = usedTime % (3600 * 1000);
  // 计算相差分钟数
  const minutes = Math.floor(leave1 / (60 * 1000));
  // 计算分钟数后剩余的毫秒数
  const leave2 = leave1 % (60 * 1000);
  // 计算相差秒数
  const seconds = Math.floor(leave2 / 1000);

  const time = `${hours}${i18n.t('hour')}${minutes}${i18n.t('minute')}${seconds}${i18n.t('second')}`;
  return time;
}

// 计算两个时间戳之间相差的天数
export function getDaysAndHours(startTime, endTime): string {
  const stime = new Date(startTime).getTime();
  const etime = new Date(endTime).getTime();
  // 两个时间戳相差的毫秒数
  const usedTime = etime - stime;

  if (usedTime < 0) {
    return i18n.t('justNow');
  }

  let days = Math.floor(usedTime / (24 * 3600 * 1000)); // 计算出天数
  if (days > 0) {
    return `${days}${i18n.t('daysAgo')}`;
  }
  // 计算天数后剩余的毫秒数
  const leave0 = usedTime % (24 * 3600 * 1000);
  // 计算出小时数
  const hours = Math.floor(leave0 / (3600 * 1000));
  if (hours > 0) {
    return `${hours}${i18n.t('hoursAgo')}`;
  }
  // 计算小时数后剩余的毫秒数
  const leave1 = leave0 % (3600 * 1000);
  // 计算相差分钟数
  const minutes = Math.floor(leave1 / (60 * 1000));
  if (minutes > 0) {
    return `${minutes}${i18n.t('minutesAgo')}`;
  }
  // 计算分钟数后剩余的毫秒数
  const leave2 = leave1 % (60 * 1000);
  // 计算相差秒数
  const seconds = Math.floor(leave2 / 1000);

  return `${seconds}${i18n.t('secondsAgo')}`;
}
