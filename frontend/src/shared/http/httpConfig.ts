const serviceConfig = {
  common : {
    development: {
      TOOL_URL: '/aippApi',
      AIPP_URL: '/aippApi',
      APP_URL: '/api/jober',
      PLUGIN_URL: '/api/jober',
      EVALUATE_URL: '/evaluate',
      COLLECT_URL: '/api/jober/aipp',
      LOGIN_URL: '/loginApi',
    },
    production: {
      TOOL_URL: '/api',
      AIPP_URL: '/api/jober/v1/api',
      PLUGIN_URL: '/api/jober',
      COLLECT_URL: '/api/jober/aipp',
      APP_URL: '/api/jober',
      LOGIN_URL: '/fce',
    },
  },
  spa : {
    development: {
      TOOL_URL: '/api',
      AIPP_URL: '/aippApi',
      MODEL_URL: '/api',
      PLUGIN_URL: '/api/jober',
      COLLECT_URL: '/api/jober/aipp',
      APP_URL: '/api/jober',
      LOGIN_URL: '/loginApi',
    },
    production: {
      TOOL_URL: '/appbuilder/api',
      AIPP_URL: '/appbuilder/v1/api',
      MODEL_URL: '/appbuilder/api',
      PLUGIN_URL: '/appbuilder',
      COLLECT_URL: '/appbuilder/aipp',
      APP_URL: '/appbuilder',
      LOGIN_URL: '/fce',
    },
  }
};
let mode = process.env.PACKAGE_MODE;
let env = process.env.NODE_ENV;
let map = serviceConfig[mode][env]
export default map;
