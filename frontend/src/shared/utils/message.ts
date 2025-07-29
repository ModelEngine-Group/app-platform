import { message } from 'antd';

let isNesting = window.self === window.top;
export const Message = ({ type, content }) => {
  const style = {
    fontSize: '12px'
  }
  message.destroy();
  switch (type) {
    case 'info':
      message.info({
        content,
        className: 'message-notice-antd4',
        key: 'message',
        style
      });
      break
    case 'success':
      message.success({
        content,
        className: 'message-notice-antd4',
        key: 'message',
        style
      });
      break
    case 'error':
      message.error({
        content,
        className: 'message-notice-antd4',
        key: 'message',
        style
      });
      break
    case 'warning':
      message.warning({
        content,
        className: 'message-notice-antd4',
        key: 'message',
        style
      });
      break
    case 'loading':
      message.loading({
        content,
        className: 'message-notice-antd4',
        key: 'message',
        style
      });
      break
    default:
  }
}