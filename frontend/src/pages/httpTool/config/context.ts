import { createContext } from 'react';

interface httpInfoObj {
  httpInfo: any;
  setHttpInfo(httpInfo: any): void;
}

const httpInfoMap: httpInfoObj = {
  httpInfo: {
    schema: {
      name: '',
      description: '',
      parameters: {
        type: 'object',
        properties: {
          location: {
            name: '',
            type: '',
            required: false,
          },
          date: {
            name: '',
            type: '',
            required: false,
          },
        },
        required: [],
      },
      order: [],
      return: {
        type: 'object',
        properties: {},
      },
    },
    extensions: {
      tags: ['HTTP'],
    },
    runnables: {
      HTTP: {
        method: 'GET',
        protocol: '',
        domain: '',
        pathPattern: '',
        mappings: {},
      },
    },
    definitionName: '',
  },
  setHttpInfo: () => {},
};

export const HttpContext = createContext(httpInfoMap);
