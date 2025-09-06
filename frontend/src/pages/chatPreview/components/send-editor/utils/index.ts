export const messagePaste = (e: any, limit: any) => {
  e.preventDefault();
  let items = e.clipboardData?.items || [];
  for (let i = 0; i < items?.length; i++) {
    const item = items[i];
    if (item.kind === 'string' && item.type === 'text/plain') {
      item.getAsString(function (str) {
        //解决粘贴字数超过limit问题
        if (limit && (e.target.innerText + str).length > limit) {
          return;
        }
        document.execCommand('insertText', true, str);
      });
    }
  }
};
