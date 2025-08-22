/*************************************************请勿修改或删除该文件**************************************************/
export default function customScriptPlugin() {
  return {
    name: 'custom-script-plugin',
    transformIndexHtml(html) {
      const scriptRegex = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi;
      let match;
      while ((match = scriptRegex.exec(html)) !== null) {
        const scriptTag = match[0];
        const newScriptTag = scriptTag.replace(
          /crossorigin\b/,
          'crossorigin="use-credentials"'
        );
        html = html.replace(scriptTag, newScriptTag);
      }
      return html;
    },
  };
}