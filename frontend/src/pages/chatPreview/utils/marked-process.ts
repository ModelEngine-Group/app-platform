import { marked } from 'marked';
import xss from 'xss';
import hljs from 'highlight.js';

export const markedProcess = (content) => {
  const config = {
    whiteList: {
      img: ['src', 'alt'],
      a: ['href', 'title', 'id'],
      div: ['class'],
      span: ['class', 'data-reference'],
      br:['class'],
      font: ['color', 'id'],
      think: [],
      final: [],
      reasoning: [],
      tool: [],
      step: []
    },
    escapeHtml: (html: string) => {
      return html;
    },
  };
  const clean = xss(content, config);
  return marked(clean, {
    highlight: (code, lang) => {
      if (code) {
        const validLanguage = hljs.getLanguage(lang) ? lang : 'javascript';
        return hljs.highlight(code, { language: validLanguage }).value;
      }
    }
  })
};
