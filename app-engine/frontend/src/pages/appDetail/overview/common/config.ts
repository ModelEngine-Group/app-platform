export const apiListData = {
  components: {
    schemas: {
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.AppMetadata':
        {
          type: 'object',
          properties: {
            msg: {
              description: '状态信息',
              examples: ['success'],
              type: 'string',
            },
            OK_CODE: {
              format: 'int32',
              type: 'integer',
            },
            OK_MSG: {
              type: 'string',
            },
            code: {
              format: 'int32',
              description: '状态码',
              examples: ['0'],
              type: 'integer',
            },
            data: {
              $ref: '#/components/schemas/modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.AppMetadata',
            },
          },
        },
      'modelengine.fit.jober.aipp.dto.AppBuilderAppDto': {
        type: 'object',
        properties: {
          aippId: {
            description: 'aipp id',
            examples: [''],
            type: 'string',
          },
          baselineCreateAt: {
            format: 'date-time',
            description: '创建时间',
            examples: [''],
            type: 'string',
          },
          flowGraph: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto',
          },
          configFormProperties: {
            description: '应用配置项',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto',
            },
          },
          publishedUpdateLog: {
            description: 'aipp 发布更新日志',
            examples: [''],
            type: 'string',
          },
          appCategory: {
            description: '应用类别',
            examples: [''],
            type: 'string',
          },
          updateAt: {
            format: 'date-time',
            description: '更新时间',
            examples: [''],
            type: 'string',
          },
          type: {
            description: '应用类型',
            examples: [''],
            type: 'string',
          },
          publishedDescription: {
            description: 'aipp 发布描述',
            examples: [''],
            type: 'string',
          },
          version: {
            description: '版本',
            examples: [''],
            type: 'string',
          },
          createAt: {
            format: 'date-time',
            description: '创建时间',
            examples: [''],
            type: 'string',
          },
          publishUrl: {
            description: 'aipp 发布链接',
            examples: [''],
            type: 'string',
          },
          createBy: {
            description: '创建者',
            examples: [''],
            type: 'string',
          },
          updateBy: {
            description: '更新者',
            examples: [''],
            type: 'string',
          },
          name: {
            description: '应用名称',
            examples: [''],
            type: 'string',
          },
          attributes: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
          },
          chatUrl: {
            description: '聊天短链地址',
            examples: [''],
            type: 'string',
          },
          id: {
            description: '应用的唯一标识符',
            examples: [''],
            type: 'string',
          },
          state: {
            description: '应用状态',
            examples: [''],
            type: 'string',
          },
          config: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderConfigDto',
          },
        },
      },
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.aipp.dto.chat.FileUploadInfo':
        {
          type: 'object',
          properties: {
            msg: {
              description: '状态信息',
              examples: ['success'],
              type: 'string',
            },
            OK_CODE: {
              format: 'int32',
              type: 'integer',
            },
            OK_MSG: {
              type: 'string',
            },
            code: {
              format: 'int32',
              description: '状态码',
              examples: ['0'],
              type: 'integer',
            },
            data: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.FileUploadInfo',
            },
          },
        },
      'modelengine.fit.jober.aipp.dto.chat.PromptInfo': {
        type: 'object',
        properties: {
          inspirations: {
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptInfo$AppBuilderInspirationDtoAdapter',
            },
          },
          categories: {
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptCategory',
            },
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.chat.PromptCategory': {
        type: 'object',
        properties: {
          parent: {
            description: '父类别',
            examples: [''],
            type: 'string',
          },
          children: {
            description: '子类别',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptCategory',
            },
          },
          disable: {
            description: '是否可被选择',
            examples: [''],
            type: 'boolean',
          },
          id: {
            description: '灵感类别的唯一标识符',
            examples: [''],
            type: 'string',
          },
          title: {
            description: '灵感类别标题',
            examples: [''],
            type: 'string',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto': {
        type: 'object',
        properties: {
          createBy: {
            type: 'string',
          },
          appearance: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
          },
          updateBy: {
            type: 'string',
          },
          name: {
            type: 'string',
          },
          updateAt: {
            format: 'date-time',
            type: 'string',
          },
          id: {
            type: 'string',
          },
          createAt: {
            format: 'date-time',
            type: 'string',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.chat.ChatRequest': {
        type: 'object',
        properties: {
          chatId: {
            description: '会话id',
            examples: [''],
            type: 'string',
          },
          question: {
            description: '问题',
            examples: [''],
            type: 'string',
          },
          context: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.ChatRequest$Context',
          },
        },
      },
      'modelengine.fit.jane.common.response.Rsp_of_java.lang.String': {
        type: 'object',
        properties: {
          msg: {
            description: '状态信息',
            examples: ['success'],
            type: 'string',
          },
          OK_CODE: {
            format: 'int32',
            type: 'integer',
          },
          OK_MSG: {
            type: 'string',
          },
          code: {
            format: 'int32',
            description: '状态码',
            examples: ['0'],
            type: 'integer',
          },
          data: {
            description: '数据',
            examples: [''],
            type: 'string',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto': {
        type: 'object',
        properties: {
          children: {
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto',
            },
          },
          defaultValue: {
            type: 'object',
          },
          dataType: {
            type: 'string',
          },
          name: {
            type: 'string',
          },
          description: {
            type: 'string',
          },
          from: {
            type: 'string',
          },
          id: {
            type: 'string',
          },
          nodeId: {
            type: 'string',
          },
          group: {
            type: 'string',
          },
        },
      },
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.aipp.dto.chat.PromptInfo': {
        type: 'object',
        properties: {
          msg: {
            description: '状态信息',
            examples: ['success'],
            type: 'string',
          },
          OK_CODE: {
            format: 'int32',
            type: 'integer',
          },
          OK_MSG: {
            type: 'string',
          },
          code: {
            format: 'int32',
            description: '状态码',
            examples: ['0'],
            type: 'integer',
          },
          data: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptInfo',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.AppBuilderConfigFormDto': {
        type: 'object',
        properties: {
          appearance: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
          },
          name: {
            type: 'string',
          },
          id: {
            type: 'string',
          },
        },
      },
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.ChatInfo':
        {
          type: 'object',
          properties: {
            msg: {
              description: '状态信息',
              examples: ['success'],
              type: 'string',
            },
            OK_CODE: {
              format: 'int32',
              type: 'integer',
            },
            OK_MSG: {
              type: 'string',
            },
            code: {
              format: 'int32',
              description: '状态码',
              examples: ['0'],
              type: 'integer',
            },
            data: {
              $ref: '#/components/schemas/modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.ChatInfo',
            },
          },
        },
      'modelengine.fit.jane.common.response.Rsp_of_java.util.List_of_modelengine.fit.jober.aipp.dto.chat.PromptCategory':
        {
          type: 'object',
          properties: {
            msg: {
              description: '状态信息',
              examples: ['success'],
              type: 'string',
            },
            OK_CODE: {
              format: 'int32',
              type: 'integer',
            },
            OK_MSG: {
              type: 'string',
            },
            code: {
              format: 'int32',
              description: '状态码',
              examples: ['0'],
              type: 'integer',
            },
            data: {
              description: '数据',
              type: 'array',
              items: {
                $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptCategory',
              },
            },
          },
        },
      'modelengine.fit.jane.common.response.Rsp_of_java.lang.Void': {
        type: 'object',
        properties: {
          msg: {
            description: '状态信息',
            examples: ['success'],
            type: 'string',
          },
          OK_CODE: {
            format: 'int32',
            type: 'integer',
          },
          OK_MSG: {
            type: 'string',
          },
          code: {
            format: 'int32',
            description: '状态码',
            examples: ['0'],
            type: 'integer',
          },
          data: {
            description: '数据',
            examples: [''],
            type: 'object',
          },
        },
      },
      'modelengine.fit.jober.common.RangeResult': {
        type: 'object',
        properties: {
          total: {
            format: 'int64',
            type: 'integer',
          },
          offset: {
            format: 'int64',
            type: 'integer',
          },
          limit: {
            format: 'int32',
            type: 'integer',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.chat.ChatRequest$Context': {
        type: 'object',
        properties: {
          useMemory: {
            description: '是否使用历史记录',
            examples: [''],
            type: 'boolean',
          },
          dimensionId: {
            description: 'id信息',
            examples: [''],
            type: 'string',
          },
          atChatId: {
            description: 'at其它应用的对话',
            examples: [''],
            type: 'string',
          },
          userContext: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
          },
          dimension: {
            description: '信息',
            examples: [''],
            type: 'string',
          },
          atAppId: {
            description: 'at其它应用',
            examples: [''],
            type: 'string',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.chat.PromptInfo$AppBuilderPromptVarDataDtoAdapter': {
        type: 'object',
        properties: {
          varType: {
            description: '提示词变量值的展示形式',
            examples: [''],
            type: 'string',
          },
          sourceInfo: {
            description: '提示词变量值的来源信息',
            examples: [''],
            type: 'string',
          },
          sourceType: {
            description: '提示词变量值的来源类型',
            examples: [''],
            type: 'string',
          },
          var: {
            description: '提示词变量',
            examples: [''],
            type: 'string',
          },
          multiple: {
            description: '是否多选',
            examples: [''],
            type: 'boolean',
          },
          key: {
            description: '提示词变量的唯一key',
            examples: [''],
            type: 'string',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.AppBuilderConfigDto': {
        type: 'object',
        properties: {
          createBy: {
            type: 'string',
          },
          form: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderConfigFormDto',
          },
          updateBy: {
            type: 'string',
          },
          tenantId: {
            type: 'string',
          },
          updateAt: {
            format: 'date-time',
            type: 'string',
          },
          id: {
            type: 'string',
          },
          createAt: {
            format: 'date-time',
            type: 'string',
          },
        },
      },
      'modelengine.fitframework.flowable.Choir_of_java.lang.Object': {
        type: 'object',
      },
      'modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.AppMetadata':
        {
          type: 'object',
          properties: {
            range: {
              $ref: '#/components/schemas/modelengine.fit.jober.common.RangeResult',
            },
            results: {
              type: 'array',
              items: {
                type: 'object',
              },
            },
          },
        },
      'modelengine.fit.jober.aipp.dto.chat.PromptInfo$AppBuilderInspirationDtoAdapter': {
        type: 'object',
        properties: {
          auto: {
            description: '是否自动执行',
            examples: [''],
            type: 'boolean',
          },
          promptVarData: {
            description: '提示词变量',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptInfo$AppBuilderPromptVarDataDtoAdapter',
            },
          },
          name: {
            description: '灵感名称',
            examples: [''],
            type: 'string',
          },
          description: {
            description: '简介',
            examples: [''],
            type: 'string',
          },
          id: {
            description: '灵感的唯一标识符',
            examples: [''],
            type: 'string',
          },
          category: {
            description: '分类',
            examples: [''],
            type: 'string',
          },
          prompt: {
            description: '提示词',
            examples: [''],
            type: 'string',
          },
          promptTemplate: {
            description: '提示词模板',
            examples: [''],
            type: 'string',
          },
        },
      },
      'java.util.Map_of_java.lang.String_and_java.lang.Object': {
        type: 'object',
      },
      'modelengine.fit.jober.aipp.dto.chat.FileUploadInfo': {
        type: 'object',
        properties: {
          fileName: {
            examples: [''],
            type: 'string',
          },
          filePath: {
            examples: [''],
            type: 'string',
          },
          fileType: {
            examples: [''],
            type: 'string',
          },
        },
      },
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.aipp.dto.AppBuilderAppDto': {
        type: 'object',
        properties: {
          msg: {
            description: '状态信息',
            examples: ['success'],
            type: 'string',
          },
          OK_CODE: {
            format: 'int32',
            type: 'integer',
          },
          OK_MSG: {
            type: 'string',
          },
          code: {
            format: 'int32',
            description: '状态码',
            examples: ['0'],
            type: 'integer',
          },
          data: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderAppDto',
          },
        },
      },
      'modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.ChatInfo':
        {
          type: 'object',
          properties: {
            range: {
              $ref: '#/components/schemas/modelengine.fit.jober.common.RangeResult',
            },
            results: {
              type: 'array',
              items: {
                type: 'object',
              },
            },
          },
        },
    },
  },
  openapi: '3.1.0',
  paths: {
    '/v1/tenants/{tenantId}/chats/instances/{currentInstanceId}': {
      post: {
        summary: '重新对话API',
        requestBody: {
          description: '重新会话所需的附加信息，如是否使用多轮对话等',
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
              },
            },
          },
        },
        operationId: 'POST /v1/tenants/{tenantId}/chats/instances/{currentInstanceId}',
        description:
          '该接口可以重新发起指定会话，需要指定需要重新发起会话的实例id，同时可添加附加信息',
        responses: {
          '205': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fitframework.flowable.Choir_of_java.lang.Object',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '需要重新发起会话的实例的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'currentInstanceId',
            description: '需要重新发起会话的实例的唯一标识符',
            required: true,
          },
        ],
        tags: ['应用对话管理接口'],
      },
    },
    '/v1/tenants/{tenantId}/apps/{appId}/config': {
      get: {
        summary: '查询应用配置详情',
        operationId: 'GET /v1/tenants/{tenantId}/apps/{appId}/config',
        description: '该接口可以通过待查询应用的唯一标识符来查询指定应用的配置详情。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.aipp.dto.AppBuilderAppDto',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '待查询 app 的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: '待查询 app 的唯一标识符',
            required: true,
          },
        ],
        tags: ['应用信息管理接口'],
      },
    },
    '/v1/tenants/{tenantId}/file': {
      post: {
        summary: '上传文件',
        operationId: 'POST /v1/tenants/{tenantId}/file',
        description: '该接口可以往指定应用上传文件。',
        responses: {
          '201': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.aipp.dto.chat.FileUploadInfo',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
            },
            name: 'tenantId',
            in: 'path',
            required: true,
            deprecated: false,
          },
          {
            schema: {
              type: 'string',
            },
            name: 'app_id',
            in: 'query',
            required: false,
            deprecated: false,
          },
        ],
        tags: ['文件上传接口'],
      },
    },
    '/v1/tenants/{tenantId}/chats/apps/{appId}': {
      post: {
        summary: '新开会话API',
        requestBody: {
          description: '会话信息，包含创建会话所需的数据',
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.ChatRequest',
              },
            },
          },
        },
        operationId: 'POST /v1/tenants/{tenantId}/chats/apps/{appId}',
        description:
          '该接口向大模型发送一个问题信息，并开启一个对话。支持 SSE 和 Websocket 两种流式调用方式。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fitframework.flowable.Choir_of_java.lang.Object',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '应用的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: '应用的唯一标识符',
            required: true,
          },
        ],
        tags: ['应用对话管理接口'],
      },
    },
    '/v1/tenants/{tenantId}/apps/{appId}/prompt/categories/{categoryId}/inspirations': {
      get: {
        summary: '获取灵感',
        operationId:
          'GET /v1/tenants/{tenantId}/apps/{appId}/prompt/categories/{categoryId}/inspirations',
        description: '该接口可以获取指定应用的某一灵感类别下的所有灵感。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.aipp.dto.chat.PromptInfo',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '应用的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: '应用的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '灵感类别的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'categoryId',
            description: '灵感类别的唯一标识符',
            required: true,
          },
        ],
        tags: ['灵感大全管理接口'],
      },
    },
    '/v1/tenants/{tenantId}/translation/audio': {
      get: {
        summary: '文字转语音',
        operationId: 'GET /v1/tenants/{tenantId}/translation/audio',
        description: '该接口可以将输入的文本转换为指定音色的语音。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.lang.String',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '待转换文本',
            },
            in: 'query',
            deprecated: false,
            name: 'text',
            description: '待转换文本',
            required: false,
          },
          {
            schema: {
              format: 'int32',
              description: '目标音色',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'tone',
            description: '目标音色',
            required: false,
          },
        ],
        tags: ['语音文字互转接口'],
      },
    },
    '/v1/tenants/{tenantId}/translation/text': {
      get: {
        summary: '语音转文字',
        operationId: 'GET /v1/tenants/{tenantId}/translation/text',
        description: '该接口可以将输入的语音文件转换为文字。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.lang.String',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '语音文件的路径',
            },
            in: 'query',
            deprecated: false,
            name: 'voicePath',
            description: '语音文件的路径',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: '语音文件的名称',
            },
            in: 'query',
            deprecated: false,
            name: 'fileName',
            description: '语音文件的名称',
            required: false,
          },
        ],
        tags: ['语音文字互转接口'],
      },
    },
    '/v1/tenants/{tenantId}/chats': {
      get: {
        summary: '查询会话历史',
        operationId: 'GET /v1/tenants/{tenantId}/chats',
        description: '该接口用于查询指定租户的会话历史，并通过指定条件进行筛选。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.ChatInfo',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              format: 'int32',
              description: '偏移量',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'offset',
            description: '偏移量',
            required: true,
          },
          {
            schema: {
              format: 'int32',
              description: '每页条数限制',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'limit',
            description: '每页条数限制',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '应用的唯一标识符',
            },
            in: 'query',
            deprecated: false,
            name: 'appId',
            description: '应用的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '应用状态',
            },
            in: 'query',
            deprecated: false,
            name: 'appState',
            description: '应用状态',
            required: true,
          },
        ],
        tags: ['应用对话管理接口'],
      },
      delete: {
        summary: '删除对话API',
        operationId: 'DELETE /v1/tenants/{tenantId}/chats',
        description: '该接口用于删除一个指定应用下一个或多个对话。',
        responses: {
          '204': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.lang.Void',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '应用的唯一标识符',
            },
            in: 'query',
            deprecated: false,
            name: 'app_id',
            description: '应用的唯一标识符',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: '要删除的聊天会话的唯一标识符，若没有指定，则全部删除',
            },
            in: 'query',
            deprecated: false,
            name: 'chat_id',
            description: '要删除的聊天会话的唯一标识符，若没有指定，则全部删除',
            required: false,
          },
        ],
        tags: ['应用对话管理接口'],
      },
    },
    '/v1/tenants/{tenantId}/apps/{appId}/prompt/categories': {
      get: {
        summary: '获取灵感类别',
        operationId: 'GET /v1/tenants/{tenantId}/apps/{appId}/prompt/categories',
        description: '该接口可以通过应用的唯一标识符获取该应用下的所有灵感类别。',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.util.List_of_modelengine.fit.jober.aipp.dto.chat.PromptCategory',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: '应用的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: '应用的唯一标识符',
            required: true,
          },
        ],
        tags: ['灵感大全管理接口'],
      },
    },
    '/v1/tenants/{tenantId}/apps': {
      get: {
        summary: '查询用户应用列表',
        operationId: 'GET /v1/tenants/{tenantId}/apps',
        description: '该接口可以使用指定条件筛选用户应用列表，如应用id、查询的应用名字和状态等.',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.AppMetadata',
                },
              },
            },
          },
        },
        parameters: [
          {
            schema: {
              type: 'string',
              description: '租户的唯一标识符',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: '租户的唯一标识符',
            required: true,
          },
          {
            schema: {
              description: '查询的id列表',
              type: 'array',
              items: {
                type: 'string',
                description: '查询的id列表',
              },
            },
            in: 'query',
            deprecated: false,
            name: 'appIds',
            description: '查询的id列表',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: '查询的名字',
            },
            in: 'query',
            deprecated: false,
            name: 'name',
            description: '查询的名字',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: '查询的状态',
            },
            in: 'query',
            deprecated: false,
            name: 'state',
            description: '查询的状态',
            required: false,
          },
          {
            schema: {
              description: '排除的名字',
              type: 'array',
              items: {
                type: 'string',
                description: '排除的名字',
              },
            },
            in: 'query',
            deprecated: false,
            name: 'excludeNames',
            description: '排除的名字',
            required: false,
          },
          {
            schema: {
              format: 'int32',
              description: '偏移量',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'offset',
            description: '偏移量',
            required: false,
          },
          {
            schema: {
              format: 'int32',
              description: '每页查询条数',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'limit',
            description: '每页查询条数',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: '查询类型',
            },
            in: 'query',
            deprecated: false,
            name: 'type',
            description: '查询类型',
            required: false,
          },
        ],
        tags: ['应用信息管理接口'],
      },
    },
  },
  info: {
    summary: '该文档由 FIT for Java 进行构建',
    description:
      '- 默认显示的 `OpenAPI` 文档地址为 `/v3/openapi`，如果需要修改，可以在顶端搜索栏自定义修改。\n- 如果需要去除某一个 `API` 的文档显示，可以在对应的方法上增加 `@DocumentIgnored` 注解。',
    title: 'OpenAPI 3.0 for application',
    version: 'FIT:3.5.0-SNAPSHOT Swagger-UI:v5.17.12',
  },
  tags: [
    {
      name: 'modelengine.jade.store.repository.pgsql.controller.TaskController',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.AppBuilderAppController',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.FetchModelController',
    },
    {
      name: '应用信息管理接口',
    },
    {
      name: '评估实例管理接口',
    },
    {
      name: 'aipp 代码运行接口',
    },
    {
      name: 'fitable管理接口',
    },
    {
      name: 'modelengine.jade.store.repository.pgsql.controller.PluginController',
    },
    {
      name: 'modelengine.jade.app.engine.base.controller.UsrFeedbackController',
    },
    {
      name: 'modelengine.jade.app.engine.base.controller.AppBuilderRecommendController',
    },
    {
      name: 'modelengine.jade.store.repository.pgsql.controller.ToolController',
    },
    {
      name: 'aipp运行时管理接口',
    },
    {
      name: '知识库相关操作',
    },
    {
      name: 'aipp组件管理接口',
    },
    {
      name: 'aipp对话管理接口',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.StoreController',
    },
    {
      name: 'aipp编排管理接口',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.AppBuilderPromptController',
    },
    {
      name: '文件上传接口',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.AgentController',
    },
    {
      name: 'modelengine.jade.app.engine.base.controller.UsrCollectionController',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.AppBuilderFormController',
    },
    {
      name: '应用对话管理接口',
    },
    {
      name: '评估任务管理接口',
    },
    {
      name: 'modelengine.fit.jober.aipp.controller.AppBuilderUrlController',
    },
    {
      name: '评估数据集管理接口',
    },
    {
      name: 'modelengine.jade.knowledge.controller.KnowledgeController',
    },
    {
      name: '灵感大全管理接口',
    },
    {
      name: '分析与反馈看板相关接口',
    },
    {
      name: 'aipp实例log管理接口',
    },
    {
      name: 'modelengine.jade.store.repository.pgsql.controller.AppController',
    },
    {
      name: 'modelengine.jade.store.tool.upload.controller.UploadPluginController',
    },
    {
      name: 'modelengine.jade.app.engine.base.controller.UserInfoController',
    },
    {
      name: '文件相关操作',
    },
    {
      name: '语音文字互转接口',
    },
    {
      name: '评估数据管理接口',
    },
    {
      name: 'modelengine.jade.carver.tool.execution.controller.ToolExecutionController',
    },
    {
      name: '评估任务用例管理接口',
    },
    {
      name: 'modelengine.jade.store.tool.deploy.controller.DeployPluginController',
    },
    {
      name: '评估任务用例结果管理接口',
    },
    {
      name: '公告信息相关操作接口',
    },
    {
      name: 'modelengine.jade.store.repository.pgsql.controller.ModelController',
    },
    {
      name: '评估数据文件解析接口',
    },
    {
      name: '评估任务报告管理接口',
    },
    {
      name: 'app对话管理接口',
    },
  ],
};

export const wssAPIData = {
  info: {
    title: 'WebSocket 聊天',
    description: '一个实时 WebSocket 聊天服务，允许用户发送和接收消息。',
    version: '1.0.0',
  },
  servers: [
    {
      url: '/ws',
      description: '用于聊天应用的 WebSocket 服务器',
    },
  ],
  paths: {
    '/chat': {
      summary: '发送对话消息。',
      description:
        '该接口向大模型发送一个问题消息，并开启一个对话。支持 SSE 和 Websocket 两种流式调用方式。',
      'First Request': {
        summary: '建立WebSocket连接',
      },
      'Websocket Request': {
        summary: '发送聊天消息请求',
        description: '用于发送聊天消息请求到大模型服务，包含问题、上下文以及内存使用参数。',
        requestBody: {
          description: '请求消息内容，包含聊天的参数、问题以及上下文，格式为json字符串。',
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  requestId: {
                    type: 'string',
                    description: '请求的唯一标识符',
                    required: true,
                  },
                  params: {
                    type: 'object',
                    properties: {
                      tenantId: {
                        type: 'string',
                        description: '租户 ID，用于标识请求的来源',
                        required: true,
                      },
                      data: {
                        type: 'object',
                        properties: {
                          app_id: {
                            type: 'string',
                            description: '与请求关联的应用 ID',
                            required: true,
                          },
                          question: {
                            type: 'string',
                            description: '用户提问的问题',
                            required: true,
                          },
                          context: {
                            type: 'object',
                            description: '会话信息，包含创建会话所需的数据',
                            properties: {
                              use_memory: {
                                type: 'boolean',
                                description: '是否使用记忆功能，可能影响生成内容的方式',
                                required: false,
                              },
                              user_context: {
                                type: 'object',
                                description: '用户的上下文信息，例如用户偏好或历史记录',
                                additionalProperties: true,
                                required: false,
                              },
                            },
                          },
                        },
                      },
                    },
                  },
                },
              },
            },
          },
        },
      },
      'Websocket responses': {
        description:
          '请求成功，返回大模型处理后的结果，该结果是多个数据流消息，最后生成完整的数据流内容。',
        content: {
          'application/json': {
            schema: {
              type: 'object',
              properties: {
                status: {
                  type: 'string',
                  description: '当前消息的状态',
                },
                answer: {
                  type: 'array',
                  description: '消息的数组',
                  items: {
                    type: 'object',
                    properties: {
                      content: {
                        type: 'object',
                        description: '消息的内容',
                        properties: {
                          formId: {
                            type: 'string',
                            description: '表单 ID，如果有的话',
                          },
                          formVersion: {
                            type: 'string',
                            description: '表单版本',
                          },
                          formArgs: {
                            type: 'string',
                            description: '表单参数',
                          },
                          msg: {
                            type: 'string',
                            description: '消息文本内容',
                          },
                          formAppearance: {
                            type: 'string',
                            description: '表单外观设置',
                          },
                          formData: {
                            type: 'string',
                            description: '表单数据',
                          },
                        },
                      },
                      type: {
                        type: 'string',
                        description: '消息类型',
                      },
                      msgId: {
                        type: 'string',
                        description: '消息 ID',
                      },
                    },
                  },
                },
                chat_id: {
                  type: 'string',
                  description: '当前聊天的唯一标识符',
                },
                at_chat_id: {
                  type: 'string',
                  description: '被 @ 的聊天 ID，如果有的话',
                },
                instance_id: {
                  type: 'string',
                  description: '当前聊天的实例 ID',
                },
                log_id: {
                  type: 'string',
                  description: '当前日志的唯一标识符',
                },
              },
            },
          },
        },
      },
    },
  },
};

export const errorCodeData = [
  {
    code: 0,
    message: 'success',
  },
  {
    code: 90000000,
    message: '非法参数: {参数信息}。',
  },
  {
    code: 90000001,
    message: '资源不存在: {资源信息}。',
  },
  {
    code: 90000002,
    message: '服务器内部错误，请联系管理员。',
  },
  {
    code: 90000003,
    message: '禁止操作。',
  },
  {
    code: 90001001,
    message: '系统错误，创建流程失败，请重试或联系管理员。',
  },
  {
    code: 90001002,
    message: '没有正在运行的对话，无需终止。',
  },
  {
    code: 90001003,
    message: '对话正在进行中，无法删除。',
  },
  {
    code: 90001011,
    message: '配置有误，请查看工作流编排是否正确，错误原因：{错误信息}。',
  },
  {
    code: 90001012,
    message: '存在相同的应用属性。',
  },
  {
    code: 90001013,
    message: '应用名称不能为空。',
  },
  {
    code: 90001014,
    message: '应用名称已存在。',
  },
  {
    code: 90001015,
    message: '应用实例日志为空。',
  },
  {
    code: 90002000,
    message: '文件上传失败。',
  },
  {
    code: 90002001,
    message: '文件过期或损坏。',
  },
  {
    code: 90002002,
    message: '解析文件内容失败。',
  },
  {
    code: 90002003,
    message: '无效文件路径。',
  },
  {
    code: 90002004,
    message: '调用 MCP 服务失败，原因：{错误信息}。',
  },
  {
    code: 90002900,
    message: 'Json解析失败，原因：{错误信息}。',
  },
  {
    code: 90002901,
    message: 'Json编码失败，原因：{错误信息}。',
  },
  {
    code: 90002902,
    message: '获取历史记录失败。',
  },
  {
    code: 90002903,
    message: '系统错误，应用配置项获取失败，请联系管理员。',
  },
  {
    code: 90002904,
    message: '灵感大全提示词变量获取失败，请联系管理员。',
  },
  {
    code: 90002905,
    message: '解析历史记录配置失败。',
  },
  {
    code: 90002906,
    message: '请检查提示词模板中的变量。',
  },
  {
    code: 90002908,
    message: '未支持的数据类型 [类型：{数据类型}]。',
  },
  {
    code: 90002909,
    message: '任务不存在。',
  },
  {
    code: 90002918,
    message: '会话请求结构有误。',
  },
  {
    code: 90002919,
    message: '该应用未发布，无法进行对话。',
  },
  {
    code: 90002920,
    message: '@应用会话出错，请清理缓存后重新对话。',
  },
  {
    code: 90002921,
    message: '调试对话失败，请重试。',
  },
  {
    code: 90002922,
    message: '会话响应出错，请重试。',
  },
  {
    code: 90002924,
    message: '应用不存在，或者已经被删除。',
  },
  {
    code: 90002925,
    message: '对话失败：应用不存在，或者已经被删除。',
  },
  {
    code: 90002927,
    message: '请输入您的问题。',
  },
  {
    code: 90002928,
    message: '实例id“{具体实例}”无法匹配任意父实例id。',
  },
  {
    code: 90002929,
    message: '实例id“{具体实例}”无法匹配任意对话。',
  },
  {
    code: 90002930,
    message: '不好意思，当前用户排队较多，请稍后重试，谢谢。',
  },
  {
    code: 90002933,
    message: '系统错误，获取应用编排信息失败，请联系管理员。',
  },
  {
    code: 90002935,
    message: '系统错误，文件格式校验失败，请联系管理员。',
  },
  {
    code: 90002936,
    message: '系统错误，模型节点解析文件失败，请联系管理员。',
  },
  {
    code: 90002938,
    message: '系统错误，获取历史对话失败，请联系管理员。',
  },
  {
    code: 90002939,
    message: '系统错误，重新对话失败，请联系管理员。',
  },
  {
    code: 90002940,
    message: '系统错误，获取应用信息失败，请联系管理员。',
  },
  {
    code: 90002946,
    message: '音频文件切分失败，请更换音频文件或重试处理。',
  },
  {
    code: 90002947,
    message: '音频文件总结内容为空，请更换音频文件或重试处理。',
  },
  {
    code: 90002948,
    message: '音频文件内容提取失败，请更换音频文件或重试处理。',
  },
  {
    code: 90002950,
    message: '系统错误，模型服务不可用，请检查模型状态或联系管理员。',
  },
  {
    code: 90002951,
    message: '系统错误，调用大模型参数错误，请联系管理员。',
  },
  {
    code: 90002952,
    message: '系统错误，调用大模型服务失败，请联系管理员。',
  },
  {
    code: 90002953,
    message: '对话不存在，或者已经被删除。',
  },
  {
    code: 90002954,
    message: '系统错误，终止会话失败，请联系管理员。',
  },
  {
    code: 90002955,
    message: '系统错误，继续会话失败，请重试或联系管理员。',
  },
  {
    code: 10007503,
    message: '系统错误，工具流编排运行失败，请联系管理员。',
  },
  {
    code: 10000003,
    message: '入参不合法，不合法参数是{错误信息}。',
  },
  {
    code: 10007511,
    message: '条件节点执行出错。',
  },
  {
    code: 10007521,
    message: '流程执行异常，请重试。',
  },
  {
    code: 90002956,
    message: '工具调用异常，请检查工具后重试。',
  },
  {
    code: 90002957,
    message: '查找工具异常，请检查工具后重试',
  },
  {
    code: 90002958,
    message: '网络连接出现问题，请检查网络连接后重试。',
  },
  {
    code: 90002959,
    message: '插件重复，请更换插件后重试。',
  },
  {
    code: 90002960,
    message: '{错误节点}节点执行出错，出错原因：{错误工具}工具执行出错，{错误信息}',
  },
  {
    code: 90002961,
    message: '{错误节点}节点执行出错，出错原因：{错误信息}',
  },
  {
    code: 90002962,
    message: '执行出错，出错原因：{错误工具}工具执行出错，{错误信息}',
  },
  {
    code: 90002963,
    message: '执行出错，出错原因：{错误信息}',
  },
  {
    code: 90002965,
    message: '表单属性父节点不存在',
  },
  {
    code: 90002973,
    message: '路径格式无效',
  },
  {
    code: 90002998,
    message: '许可证过期。',
  },
];

export const resCode = [
  {
    code: 400,
    message: '请求错误，客户端发送的数据或请求格式不正确。',
  },
  {
    code: 403,
    message: '请求合法，但客户端没有权限访问该资源。',
  },
  {
    code: 404,
    message: '请求的资源不存在，可能是 URL 错误或资源已被删除。',
  },
  {
    code: 500,
    message: '服务器内部错误,请联系管理员。',
  },
];

export const resOKCode = [
  {
    code: 200,
    message: '请求已成功处理并返回响应数据。',
  },
  {
    code: 201,
    message: '请求已成功处理并导致了新的资源创建。',
  },
  {
    code: 204,
    message: '请求已成功处理，但没有返回任何内容。',
  },
  {
    code: 205,
    message: '请求已成功处理，请客户端重置当前文档视图。',
  },
];

export const reqWssData = [
  {
    requestId: '8b7135e902ed45009c95c7880e8a2e66',
    method: 'appChat',
    params: {
      tenantId: '6ad82f0507524447b90aa71ccda015ae',
      data: {
        app_id: '1bcc1c297c434691832ee0e91af9a793',
        question: '中国四大名著是什么？',
        context: {
          use_memory: true,
          user_context: {},
        },
      },
    },
  },
];

export const resWssData = [
  {
    status: 'RUNNING',
    answer: [
      {
        content: '中国',
        type: 'MSG',
        msgId: 'f8ce72c0-6c35-4d4f-9226-82d151367d74',
      },
    ],
    chat_id: '8b7135e902ed45009c95c7880e8a2e66',
    at_chat_id: null,
    instance_id: '6ad82f0507524447b90aa71ccda015ae',
    log_id: null,
  },
  {
    status: 'ARCHIVED',
    answer: [
      {
        content: {
          formId: null,
          formVersion: null,
          formArgs: null,
          msg: '中国四大名著指的是《红楼梦》、《西游记》、《水浒传》和《三国演义》。它们是中国古代文学的经典之作，被广泛地传颂和阅读。这四部小说都具有深刻的思想内涵和丰富的人物形象，是中国文学史上的重要里程碑。',
          formAppearance: null,
          formData: null,
        },
        type: 'META_MSG',
        msgId: null,
      },
    ],
    chat_id: '8b7135e902ed45009c95c7880e8a2e66',
    at_chat_id: null,
    instance_id: '6ad82f0507524447b90aa71ccda015ae',
    log_id: '16',
  },
];

export const urlMap = {
  chatsApps: '/v1/tenants/{tenantId}/chats/apps/{appId}',
  chats: '/v1/tenants/{tenantId}/chats',
  instances: '/v1/tenants/{tenantId}/chats/instances/{currentInstanceId}',
  categories: '/v1/tenants/{tenantId}/apps/{appId}/prompt/categories',
  inspirations: '/v1/tenants/{tenantId}/apps/{appId}/prompt/categories/{categoryId}/inspirations',
  translationAudio: '/v1/tenants/{tenantId}/translation/audio',
  file: '/v1/tenants/{tenantId}/file',
  translationText: '/v1/tenants/{tenantId}/translation/text',
  apps: '/v1/tenants/{tenantId}/apps',
  appsConfig: '/v1/tenants/{tenantId}/apps/{appId}/config',
};

export const HTTPMap = {
  http: 'http',
};
