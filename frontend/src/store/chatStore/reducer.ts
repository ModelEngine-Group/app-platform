import { 
  SET_CHAT_ID,
  SET_CHAT_LIST,
  SET_OPEN_STAR,
  SET_INSPIRATION_OPEN,
  SET_CHAT_RUNNING,
  SET_CHAT_TYPE,
  SET_AT_CHAT_ID,
  SET_FORM_RECEIVED,
  SET_REFERENCE,
  SET_LOGIN_STATUS,
  SET_REFERENCE_LIST,
  SET_USER_ROLE,
  SET_READ_ONLY,
  SET_NO_AUTH,
  SET_PLUGIN_LIST,
  SET_CURRENT_ANSWER,
  SET_KNOWLEDGE_CONFIG,
  SET_SHOW_CHAT_HISTORY,
  SET_IS_CURRENT_ANSWER
} from './action-types';

const initialState = {
  chatRunning: false,
  chatList: [],
  inspirationOpen: false,
  openStar: false,
  chatType: '',
  chatId: '',
  atChatId: '',
  formReceived: false,
  loginStatus: true,
  chatReference: [],
  referenceList: {},
  userRole: 'READ_ONLY',
  readOnly: false,
  noAuth: false,
  pluginList: [],
  currentAnswer: '',
  knowledgeConfig: null,
  showChatHistory: false,
  isCurrentAnswer: false
}

const chatReducers = (state = initialState, action) => {
  switch (action.type) {
    case SET_CHAT_ID:
      return { ...state, chatId: action.payload };
    case SET_CHAT_LIST:
      return { ...state, chatList: action.payload };
    case SET_OPEN_STAR:
      return { ...state, openStar: action.payload };
    case SET_INSPIRATION_OPEN:
      return { ...state, inspirationOpen: action.payload };
    case SET_CHAT_RUNNING:
      return { ...state, chatRunning: action.payload };
    case SET_CHAT_TYPE:
      return { ...state, chatType: action.payload };
    case SET_AT_CHAT_ID:
      return { ...state, atChatId: action.payload };
    case SET_FORM_RECEIVED:
      return { ...state, formReceived: action.payload };
    case SET_LOGIN_STATUS:
      return { ...state, loginStatus: action.payload };
    case SET_REFERENCE:
      return { ...state, chatReference: action.payload };
    case SET_REFERENCE_LIST:
      return { ...state, referenceList: action.payload };
    case SET_USER_ROLE:
      return { ...state, userRole: action.payload };
    case SET_READ_ONLY:
      return { ...state, readOnly: action.payload };
    case SET_NO_AUTH:
      return { ...state, noAuth: action.payload };
    case SET_PLUGIN_LIST:
      return { ...state, pluginList: action.payload };
    case SET_CURRENT_ANSWER:
      return { ...state, currentAnswer: action.payload };
    case SET_IS_CURRENT_ANSWER:
      return { ...state, isCurrentAnswer: action.payload };
    case SET_KNOWLEDGE_CONFIG:
      return { ...state, knowledgeConfig: action.payload };
    case SET_SHOW_CHAT_HISTORY:
      return { ...state, showChatHistory: action.payload };
    default:
      return state
  }
}

export default chatReducers
