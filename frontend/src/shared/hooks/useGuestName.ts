import { useState, useEffect } from 'react';

export const useGuestName = () => {
  const [guestName, setGuestName] = useState(() => {
    return localStorage.getItem('guest-name');
  });

  useEffect(() => {
    // 监听 localStorage 变化
    const handleStorageChange = (event) => {
      if (event.key === 'guest-name') {
        setGuestName(event.newValue || '');
      }
    };

    // 监听其他标签页的变化
    window.addEventListener('storage', handleStorageChange);

    // 定时检查本地变化（每1秒）
    const interval = setInterval(() => {
      const storedUser = localStorage.getItem('guest-name');
      if (storedUser !== guestName) {
        setGuestName(storedUser || '');
      }
    }, 1000);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      clearInterval(interval);
    };
  }, [guestName]);

  return guestName;
};
