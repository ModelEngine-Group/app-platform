const baseSize = 16;
const setRem = () => {
  const scale = document.documentElement.clientWidth / 1920;

  if (window.innerWidth > 1920) {
    document.documentElement.style.fontSize = `${baseSize * Math.min(scale, 1.2)}px`;
  } else if (window.innerWidth <= 1600) {
    document.documentElement.style.fontSize = `14px`;
  } else {
    document.documentElement.style.fontSize = `${baseSize}px`;
  }
};
setRem();
window.onresize = () => {
  setRem();
};
