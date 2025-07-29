// 日期方法
export const formatDateTime = (dateString: Date) => {
    let date = dateString;
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();
    const second = date.getSeconds();
    return `${year}-${pad(month)}-${pad(day)} ${pad(hour)}:${pad(minute)}:${pad(
        second
    )}`;
}
function pad(num: number) {
    return num.toString().padStart(2, '0');
}
