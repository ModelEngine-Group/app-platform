import { v4 as uuidv4 } from 'uuid';
import i18n from '@/locale/i18n';

export const stepItems = [
  {
    title: i18n.t('basicInfo'),
  },
  {
    title: i18n.t('methodDefinition'),
  },
  {
    title: i18n.t('informationConfiguration'),
  },
]
export const initParams = {
  description: '',
  name: '',
  reference: '',
  required: false,
  mapping: '',
  rowKey: uuidv4(),
  deep: 1,
  type: 'string',
  requestType: 'PATH_VARIABLE',
  children: []
}
export const inputTypeOption = [
  {
    value: 'PATH_VARIABLE',
    label: 'Path',
  },
  {
    value: 'HEADER',
    label: 'Header',
  },
  {
    value: 'OBJECT_ENTITY',
    label: 'Body',
    children: [{ label: 'JSON', value: 'JSON' }],
    disabled: true,
  },
  {
    value: 'QUERY',
    label: 'Query',
  },
];

export const inputObjOption = [
  {
    value: 'OBJECT_ENTITY',
    label: 'Body',
    children: [{ label: 'JSON', value: 'JSON' }],
    disabled: true,
  },
  {
    value: 'NONE',
    label: 'None',
  },
];

export const inputArrOption = [
  {
    value: 'HEADER',
    label: 'Header',
  },
  {
    value: 'QUERY',
    label: 'Query',
  },
];

export const paramsTypeOption = [
  {
    value: 'string',
    label: 'string',
  },
  {
    value: 'object',
    label: 'object',
  },
  {
    value: 'array',
    label: 'array',
  },
  {
    value: 'boolean',
    label: 'boolean',
  },
  {
    value: 'integer',
    label: 'integer',
  }
]
export const paramsArrayOption = [
  {
    value: 'array<string>',
    label: 'string',
  },
  {
    value: 'array<object>',
    label: 'object',
  },
  {
    value: 'array<integer>',
    label: 'integer',
  },
  {
    value: 'array<boolean>',
    label: 'boolean',
  }
]

export const initHttpData =  {
  "schema": {
    "name": "",
    "description": "",
    "parameters": {
      "properties": {},
      "required": []
    },
    "order": [],
    "return": {
      "properties": {
        "output": {
          "type": 'string',
          "description": '',
        },
      }
    }
  },
  "extensions": {
    "tags": [
      "HTTP"
    ]
  },
  "runnables": {
    "HTTP": {
      "method": "GET",
      "protocol": "",
      "domain": "",
      "pathPattern": "",
      "mappings": {}
    }
  }
}

