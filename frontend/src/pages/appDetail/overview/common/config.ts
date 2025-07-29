export const apiListData = {
  components: {
    schemas: {
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.fit.jober.common.RangedResultSet_of_modelengine.fit.jober.aipp.dto.chat.AppMetadata':
        {
          type: 'object',
          properties: {
            msg: {
              description: 'north.schema.AppMetadata.msg.description',
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
              description: 'north.schema.AppMetadata.code.description',
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
            description: 'north.schema.AppBuilderAppDto.baselineCreateAt.description',
            examples: [''],
            type: 'string',
          },
          flowGraph: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto',
          },
          configFormProperties: {
            description: 'north.schema.AppBuilderAppDto.configFormProperties.description',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto',
            },
          },
          publishedUpdateLog: {
            description: 'north.schema.AppBuilderAppDto.publishedUpdateLog.description',
            examples: [''],
            type: 'string',
          },
          appCategory: {
            description: 'north.schema.AppBuilderAppDto.appCategory.description',
            examples: [''],
            type: 'string',
          },
          updateAt: {
            format: 'date-time',
            description: 'north.schema.AppBuilderAppDto.updateAt.description',
            examples: [''],
            type: 'string',
          },
          type: {
            description: 'north.schema.AppBuilderAppDto.type.description',
            examples: [''],
            type: 'string',
          },
          publishedDescription: {
            description: 'north.schema.AppBuilderAppDto.publishedDescription.description',
            examples: [''],
            type: 'string',
          },
          version: {
            description: 'north.schema.AppBuilderAppDto.version.description',
            examples: [''],
            type: 'string',
          },
          createAt: {
            format: 'date-time',
            description: 'north.schema.AppBuilderAppDto.createAt.description',
            examples: [''],
            type: 'string',
          },
          publishUrl: {
            description: 'north.schema.AppBuilderAppDto.publishUrl.description',
            examples: [''],
            type: 'string',
          },
          createBy: {
            description: 'north.schema.AppBuilderAppDto.createBy.description',
            examples: [''],
            type: 'string',
          },
          updateBy: {
            description: 'north.schema.AppBuilderAppDto.updateBy.description',
            examples: [''],
            type: 'string',
          },
          name: {
            description: 'north.schema.AppBuilderAppDto.name.description',
            examples: [''],
            type: 'string',
          },
          attributes: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
          },
          chatUrl: {
            description: 'north.schema.AppBuilderAppDto.chatUrl.description',
            examples: [''],
            type: 'string',
          },
          id: {
            description: 'north.schema.AppBuilderAppDto.id.description',
            examples: [''],
            type: 'string',
          },
          state: {
            description: 'north.schema.AppBuilderAppDto.state.description',
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
              description: 'north.schema.FileUploadInfo.msg.description',
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
              description: 'north.schema.FileUploadInfo.code.description',
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
            description: 'north.schema.PromptCategory.parent.description',
            examples: [''],
            type: 'string',
          },
          children: {
            description: 'north.schema.PromptCategory.children.description',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptCategory',
            },
          },
          disable: {
            description: 'north.schema.PromptCategory.disable.description',
            examples: [''],
            type: 'boolean',
          },
          id: {
            description: 'north.schema.PromptCategory.id.description',
            examples: [''],
            type: 'string',
          },
          title: {
            description: 'north.schema.PromptCategory.title.description',
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
            description: 'north.schema.ChatRequest.chatId.description',
            examples: [''],
            type: 'string',
          },
          question: {
            description: 'north.schema.ChatRequest.question.description',
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
            description: 'north.schema.AppMetadata.msg.description',
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
            description: 'north.schema.AppMetadata.code.description',
            examples: ['0'],
            type: 'integer',
          },
          data: {
            description: 'north.schema.String.data.description',
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
            description: 'north.schema.AppMetadata.msg.description',
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
            description: 'north.schema.AppMetadata.code.description',
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
              description: 'north.schema.AppMetadata.msg.description',
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
              description: 'north.schema.AppMetadata.code.description',
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
              description: 'north.schema.AppMetadata.msg.description',
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
              description: 'north.schema.AppMetadata.code.description',
              examples: ['0'],
              type: 'integer',
            },
            data: {
              description: 'north.schema.String.data.description',
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
            description: 'north.schema.AppMetadata.msg.description',
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
            description: 'north.schema.AppMetadata.code.description',
            examples: ['0'],
            type: 'integer',
          },
          data: {
            description: 'north.schema.String.data.description',
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
            description: 'north.schema.ChatRequest$Context.useMemory.description',
            examples: [''],
            type: 'boolean',
          },
          dimensionId: {
            description: 'north.schema.ChatRequest$Context.dimensionId.description',
            examples: [''],
            type: 'string',
          },
          atChatId: {
            description: 'north.schema.ChatRequest$Context.atChatId.description',
            examples: [''],
            type: 'string',
          },
          userContext: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
            description: 'north.schema.ChatRequest$Context.userContext.description',
            type: 'object',
          },
          dimension: {
            description: 'north.schema.ChatRequest$Context.dimension.description',
            examples: [''],
            type: 'string',
          },
          atAppId: {
            description: 'north.schema.ChatRequest$Context.atAppId.description',
            examples: [''],
            type: 'string',
          },
        },
      },
      'modelengine.fit.jober.aipp.dto.chat.PromptInfo$AppBuilderPromptVarDataDtoAdapter': {
        type: 'object',
        properties: {
          varType: {
            description: 'north.schema.PromptInfo$AppBuilderPromptVarDataDtoAdapter.varType.description',
            examples: [''],
            type: 'string',
          },
          sourceInfo: {
            description: 'north.schema.PromptInfo$AppBuilderPromptVarDataDtoAdapter.sourceInfo.description',
            examples: [''],
            type: 'string',
          },
          sourceType: {
            description: 'north.schema.PromptInfo$AppBuilderPromptVarDataDtoAdapter.sourceType.description',
            examples: [''],
            type: 'string',
          },
          var: {
            description: 'north.schema.PromptInfo$AppBuilderPromptVarDataDtoAdapter.var.description',
            examples: [''],
            type: 'string',
          },
          multiple: {
            description: 'north.schema.PromptInfo$AppBuilderPromptVarDataDtoAdapter.multiple.description',
            examples: [''],
            type: 'boolean',
          },
          key: {
            description: 'north.schema.PromptInfo$AppBuilderPromptVarDataDtoAdapter.key.description',
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
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.auto.description',
            examples: [''],
            type: 'boolean',
          },
          promptVarData: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.promptVarData.description',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.PromptInfo$AppBuilderPromptVarDataDtoAdapter',
            },
          },
          name: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.name.description',
            examples: [''],
            type: 'string',
          },
          description: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.description.description',
            examples: [''],
            type: 'string',
          },
          id: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.id.description',
            examples: [''],
            type: 'string',
          },
          category: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.category.description',
            examples: [''],
            type: 'string',
          },
          prompt: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.prompt.description',
            examples: [''],
            type: 'string',
          },
          promptTemplate: {
            description: 'north.schema.PromptInfo$AppBuilderInspirationDtoAdapter.promptTemplate.description',
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
            description: 'north.schema.AppMetadata.msg.description',
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
            description: 'north.schema.AppMetadata.code.description',
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
      'modelengine.fit.jane.common.response.Rsp_of_java.util.List_of_modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData': {
        type: 'object',
        properties: {
          msg: {
            description: 'north.schema.AppMetadata.msg.description',
            examples: [
              'success'
            ],
            type: 'string'
          },
          OK_CODE: {
            format: 'int32',
            type: 'integer'
          },
          OK_MSG: {
            type: 'string'
          },
          code: {
            format: 'int32',
            description: 'north.schema.AppMetadata.code.description',
            examples: [
              '0'
            ],
            type: 'integer'
          },
          data: {
            description: 'north.schema.String.data.description',
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData'
            }
          }
        }
      },
      'modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData': {
        type: 'object',
        properties: {
          instanceLogBodies: {
            type: 'array',
            items: {
              $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData$AippInstLogBody'
            }
          },
          aippId: {
            type: 'string'
          },
          appIcon: {
            type: 'string'
          },
          instanceId: {
            type: 'string'
          },
          question: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData$AippInstLogBody'
          },
          appName: {
            type: 'string'
          },
          version: {
            type: 'string'
          },
          createAt: {
            format: 'date-time',
            type: 'string'
          },
          status: {
            type: 'string'
          }
        }
      },
      'modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData$AippInstLogBody': {
        type: 'object',
        properties: {
          logType: {
            type: 'string'
          },
          createUserAccount: {
            type: 'string'
          },
          logData: {
            type: 'string'
          },
          logId: {
            format: 'int64',
            type: 'integer'
          },
          createAt: {
            format: 'date-time',
            type: 'string'
          }
        }
      },
      'modelengine.jade.app.engine.base.dto.UsrFeedbackDto': {
        type: 'object',
        properties: {
          instanceId: {
            description: 'north.schema.UsrFeedbackDto.instanceId.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          usrFeedback: {
            format: 'int32',
            description: 'north.schema.UsrFeedbackDto.usrFeedback.description',
            examples: [
              ''
            ],
            type: 'integer'
          },
          usrFeedbackText: {
            description: 'north.schema.UsrFeedbackDto.usrFeedbackText.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          id: {
            format: 'int64',
            description: 'north.schema.UsrFeedbackDto.id.description',
            examples: [
              ''
            ],
            type: 'integer'
          }
        }
      },
      'modelengine.fit.jane.common.response.Rsp_of_modelengine.jade.app.engine.base.dto.UsrFeedbackDto': {
        type: 'object',
        properties: {
          msg: {
            description: 'north.schema.AppMetadata.msg.description',
            examples: [
              'success'
            ],
            type: 'string'
          },
          OK_CODE: {
            format: 'int32',
            type: 'integer'
          },
          OK_MSG: {
            type: 'string'
          },
          code: {
            format: 'int32',
            description: 'north.schema.AppMetadata.code.description',
            examples: [
              '0'
            ],
            type: 'integer'
          },
          data: {
            $ref: '#/components/schemas/modelengine.jade.app.engine.base.dto.UsrFeedbackDto'
          }
        }
      },
      'modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest': {
        type: 'object',
        properties: {
          chat_id: {
            description: 'north.schema.CreateAppChatRequest.chat_id.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          question: {
            description: 'north.schema.CreateAppChatRequest.question.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          app_id: {
            description: 'north.schema.CreateAppChatRequest.app_id.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          context: {
            $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest$Context'
          }
        }
      },
      'modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest$Context': {
        type: 'object',
        properties: {
          use_memory: {
            description: 'north.schema.CreateAppChatRequest$Context.use_memory.description',
            examples: [
              ''
            ],
            type: 'boolean'
          },
          dimension_id: {
            description: 'north.schema.CreateAppChatRequest$Context.dimension_id.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          at_chat_id: {
            description: 'north.schema.CreateAppChatRequest$Context.at_chat_id.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          user_context: {
            $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
            description: 'north.schema.CreateAppChatRequest$Context.user_context.description',
            type: 'object',
          },
          dimension: {
            description: 'north.schema.CreateAppChatRequest$Context.dimension.description',
            examples: [
              ''
            ],
            type: 'string'
          },
          at_app_id: {
            description: 'north.schema.CreateAppChatRequest$Context.at_app_id.description',
            examples: [
              ''
            ],
            type: 'string'
          }
        }
      },
    },
  },
  openapi: '3.1.0',
  paths: {
    '/api/app/v1/tenants/{tenantId}/chats/instances/{currentInstanceId}': {
      post: {
        summary: 'north.api.restartChat.summary',
        requestBody: {
          description: 'north.api.restartChat.request.description',
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/java.util.Map_of_java.lang.String_and_java.lang.Object',
              },
            },
          },
        },
        operationId: 'POST /api/app/v1/tenants/{tenantId}/chats/instances/{currentInstanceId}',
        description:
          'north.api.restartChat.description',
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
              description: 'north.api.restartChat.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.restartChat.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.restartChat.parameters.currentInstanceId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'currentInstanceId',
            description: 'north.api.restartChat.parameters.currentInstanceId.description',
            required: true,
          },
        ],
        tags: ['应用对话管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenantId}/apps/{appId}/config': {
      get: {
        summary: 'north.api.appConfig.summary',
        operationId: 'GET /api/app/v1/tenants/{tenantId}/apps/{appId}/config',
        description: 'north.api.appConfig.description',
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
              description: 'north.api.appConfig.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.appConfig.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.appConfig.parameters.appId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: 'north.api.appConfig.parameters.appId.description',
            required: true,
          },
        ],
        tags: ['应用信息管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenantId}/file': {
      post: {
        summary: 'north.api.uploadFile.summary',
        operationId: 'POST /api/app/v1/tenants/{tenantId}/file',
        description: 'north.api.uploadFile.description',
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
    '/api/app/v1/tenants/{tenantId}/chats/apps/{appId}': {
      post: {
        summary: 'north.api.chat.summary',
        requestBody: {
          description: 'north.api.chat.request.description',
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.ChatRequest',
              },
            },
          },
        },
        operationId: 'POST /api/app/v1/tenants/{tenantId}/chats/apps/{appId}',
        description:
          'north.api.chat.description',
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
              description: 'north.api.chat.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.chat.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.chat.parameters.appId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: 'north.api.chat.parameters.appId.description',
            required: true,
          },
        ],
        tags: ['应用对话管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenantId}/apps/{appId}/prompt/categories/{categoryId}/inspirations': {
      get: {
        summary: 'north.api.getInspiration.summary',
        operationId:
          'GET /api/app/v1/tenants/{tenantId}/apps/{appId}/prompt/categories/{categoryId}/inspirations',
        description: 'north.api.getInspiration.description',
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
              description: 'north.api.getInspiration.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.getInspiration.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getInspiration.parameters.appId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: 'north.api.getInspiration.parameters.appId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getInspiration.parameters.categoryId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'categoryId',
            description: 'north.api.getInspiration.parameters.categoryId.description',
            required: true,
          },
        ],
        tags: ['灵感大全管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenantId}/translation/audio': {
      get: {
        summary: '文字转语音',
        operationId: 'GET /api/app/v1/tenants/{tenantId}/translation/audio',
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
    '/api/app/v1/tenants/{tenantId}/translation/text': {
      get: {
        summary: '语音转文字',
        operationId: 'GET /api/app/v1/tenants/{tenantId}/translation/text',
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
    '/api/app/v1/tenants/{tenantId}/chats': {
      get: {
        summary: 'north.api.getChatHistory.summary',
        operationId: 'GET /api/app/v1/tenants/{tenantId}/chats',
        description: 'north.api.getChatHistory.description',
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
              description: 'north.api.getChatHistory.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.getChatHistory.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              format: 'int32',
              description: 'north.api.getChatHistory.parameters.offset.description',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'offset',
            description: 'north.api.getChatHistory.parameters.offset.description',
            required: true,
          },
          {
            schema: {
              format: 'int32',
              description: 'north.api.getChatHistory.parameters.limit.description',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'limit',
            description: 'north.api.getChatHistory.parameters.limit.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getChatHistory.parameters.appId.description',
            },
            in: 'query',
            deprecated: false,
            name: 'appId',
            description: 'north.api.getChatHistory.parameters.appId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getChatHistory.parameters.appState.description',
            },
            in: 'query',
            deprecated: false,
            name: 'appState',
            description: 'north.api.getChatHistory.parameters.appState.description',
            required: true,
          },
        ],
        tags: ['应用对话管理接口'],
      },
      delete: {
        summary: 'north.api.deleteChat.summary',
        operationId: 'DELETE /v1/tenants/{tenantId}/chats',
        description: 'north.api.deleteChat.description',
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
              description: 'north.api.deleteChat.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.deleteChat.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.deleteChat.parameters.app_id.description',
            },
            in: 'query',
            deprecated: false,
            name: 'app_id',
            description: 'north.api.deleteChat.parameters.app_id.description',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.deleteChat.parameters.chat_id.description',
            },
            in: 'query',
            deprecated: false,
            name: 'chat_id',
            description: 'north.api.deleteChat.parameters.chat_id.description',
            required: false,
          },
        ],
        tags: ['应用对话管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenantId}/apps/{appId}/prompt/categories': {
      get: {
        summary: 'north.api.getCategory.summary',
        operationId: 'GET /api/app/v1/tenants/{tenantId}/apps/{appId}/prompt/categories',
        description: 'north.api.getCategory.description',
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
              description: 'north.api.getCategory.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.getCategory.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getCategory.parameters.appId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'appId',
            description: 'north.api.getCategory.parameters.appId.description',
            required: true,
          },
        ],
        tags: ['灵感大全管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenantId}/apps': {
      get: {
        summary: 'north.api.getAppList.summary',
        operationId: 'GET /api/app/v1/tenants/{tenantId}/apps',
        description: 'north.api.getAppList.description',
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
              description: 'north.api.getAppList.parameters.tenantId.description',
            },
            in: 'path',
            deprecated: false,
            name: 'tenantId',
            description: 'north.api.getAppList.parameters.tenantId.description',
            required: true,
          },
          {
            schema: {
              description: 'north.api.getAppList.parameters.ids.description',
              type: 'array',
              items: {
                type: 'string',
                description: 'north.api.getAppList.parameters.ids.description',
              },
            },
            in: 'query',
            deprecated: false,
            name: 'ids',
            description: 'north.api.getAppList.parameters.ids.description',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getAppList.parameters.name.description',
            },
            in: 'query',
            deprecated: false,
            name: 'name',
            description: 'north.api.getAppList.parameters.name.description',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getAppList.parameters.state.description',
            },
            in: 'query',
            deprecated: false,
            name: 'state',
            description: 'north.api.getAppList.parameters.state.description',
            required: false,
          },
          {
            schema: {
              description: 'north.api.getAppList.parameters.excludeNames.description',
              type: 'array',
              items: {
                type: 'string',
                description: 'north.api.getAppList.parameters.excludeNames.description',
              },
            },
            in: 'query',
            deprecated: false,
            name: 'excludeNames',
            description: 'north.api.getAppList.parameters.excludeNames.description',
            required: false,
          },
          {
            schema: {
              format: 'int32',
              description: 'north.api.getAppList.parameters.offset.description',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'offset',
            description: 'north.api.getAppList.parameters.offset.description',
            required: false,
          },
          {
            schema: {
              format: 'int32',
              description: 'north.api.getAppList.parameters.limit.description',
              type: 'integer',
            },
            in: 'query',
            deprecated: false,
            name: 'limit',
            description: 'north.api.getAppList.parameters.limit.description',
            required: false,
          },
          {
            schema: {
              type: 'string',
              description: 'north.api.getAppList.parameters.type.description',
            },
            in: 'query',
            deprecated: false,
            name: 'type',
            description: 'north.api.getAppList.parameters.type.description',
            required: false,
          },
        ],
        tags: ['应用信息管理接口'],
      },
    },
    '/api/app/v1/tenants/{tenant_id}/log/app/{app_id}/chat/{chat_id}': {
      get: {
        summary: 'north.api.getChatHistoryById.summary',
        operationId: 'GET /api/app/v1/tenants/{tenant_id}/log/app/{app_id}/chat/{chat_id}',
        description: 'north.api.getChatHistoryById.description',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.util.List_of_modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData'
                }
              }
            }
          }
        },
        parameters: [
          {
            schema: {
              'type': 'string'
            },
            name: 'tenant_id',
            in: 'path',
            required: true,
            deprecated: false
          },
          {
            schema: {
              'type': 'string'
            },
            name: 'app_id',
            in: 'path',
            required: true,
            deprecated: false
          },
          {
            schema: {
              'type': 'string'
            },
            name: 'chat_id',
            in: 'path',
            required: true,
            deprecated: false
          }
        ],
        tags: [
          'aipp 实例日志管理北向接口'
        ]
      }
    },
    '/api/app/v1/aipp/user/feedback': {
      post: {
        summary: 'north.api.createFeedback.summary',
        operationId: 'POST /api/app/v1/aipp/user/feedback',
        description: 'north.api.createFeedback.description',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.lang.Void'
                }
              }
            }
          }
        },
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/modelengine.jade.app.engine.base.dto.UsrFeedbackDto'
              }
            }
          }
        },
        tags: [
          'modelengine.fit.jade.aipp.northbound.controller.UserFeedbackController'
        ]
      }
    },
    '/api/app/v1/aipp/user/feedback/{instanceId}': {
      patch: {
        summary: 'north.api.updateFeedback.summary',
        operationId: 'PATCH /api/app/v1/aipp/user/feedback/{instanceId}',
        description: 'north.api.updateFeedback.description',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_java.lang.Void'
                }
              }
            }
          }
        },
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/modelengine.jade.app.engine.base.dto.UsrFeedbackDto'
              }
            }
          }
        },
        parameters: [
          {
            schema: {
              'type': 'string'
            },
            name: 'instanceId',
            in: 'path',
            required: true,
            deprecated: false
          }
        ],
        tags: [
          'modelengine.fit.jade.aipp.northbound.controller.UserFeedbackController'
        ]
      },
      get: {
        summary: 'north.api.getFeedback.summary',
        operationId: 'GET /api/app/v1/aipp/user/feedback/{instanceId}',
        description: 'north.api.getFeedback.description',
        responses: {
          '200': {
            description: '',
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fit.jane.common.response.Rsp_of_modelengine.jade.app.engine.base.dto.UsrFeedbackDto'
                }
              }
            }
          }
        },
        parameters: [
          {
            schema: {
              'type': 'string'
            },
            name: 'instanceId',
            in: 'path',
            required: true,
            deprecated: false
          }
        ],
        tags: [
          'modelengine.fit.jade.aipp.northbound.controller.UserFeedbackController'
        ]
      }
    },
    '/agent/v1/api/{tenant_id}/app_chat':{
      post: {
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest'
              }
            }
          }
        },
        operationId: 'POST /agent/v1/api/{tenant_id}/app_chat',
        description: 'north.api.oldChat.description',
        summary: 'north.api.oldChat.summary',
        responses: {
          200: {
            description: "",
            content: {
              'application/json': {
                schema: {
                  $ref: '#/components/schemas/modelengine.fitframework.flowable.Choir_of_java.lang.Object'
                }
              }
            }
          }
        },
        parameters: [
          {
            schema: {
              type: 'string',
            },
            name: 'tenant_id',
            description: 'north.api.oldChat.parameters.tenant_id.description',
            in: 'path',
            required: true,
            deprecated: false
          }
        ],
        tags: [
          'app对话管理接口'
        ]
      }
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
    {
      name: 'aipp实例日志管理北向接口',
    },
    {
      name: 'modelengine.fit.jade.aipp.northbound.controller.UserFeedbackController'
    }
  ],
};

export const wssAPIData = {
  info: {
    title: 'north.wssApiData.info.title',
    description: 'north.wssApiData.info.description',
    version: '1.0.0',
  },
  servers: [
    {
      url: '/api/app/v1/chat',
      description: 'north.wssApiData.servers.description',
    },
  ],
  paths: {
    '/chat': {
      summary: 'north.wssApiData.paths./chat.summary',
      description:
        'north.wssApiData.paths./chat.description',
      'First Request': {
        summary: 'north.wssApiData.paths./chat.First Request.summary',
      },
      'Websocket Request': {
        summary: 'north.wssApiData.paths./chat.Websocket Request.summary',
        description: 'north.wssApiData.paths./chat.Websocket Request.description',
        requestBody: {
          description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.description',
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  requestId: {
                    type: 'string',
                    description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.requestId.description',
                    required: true,
                  },
                  params: {
                    type: 'object',
                    properties: {
                      tenantId: {
                        type: 'string',
                        description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.params.tenantId.description',
                        required: true,
                      },
                      data: {
                        type: 'object',
                        properties: {
                          app_id: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.params.data.app_id.description',
                            required: true,
                          },
                          question: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.params.data.question.description',
                            required: true,
                          },
                          context: {
                            type: 'object',
                            description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.description',
                            properties: {
                              use_memory: {
                                type: 'boolean',
                                description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.use_memory.description',
                                required: false,
                              },
                              user_context: {
                                type: 'object',
                                description: 'north.wssApiData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.user_context.description',
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
          'north.wssApiData.paths./chat.Websocket responses.description',
        content: {
          'application/json': {
            schema: {
              type: 'object',
              properties: {
                status: {
                  type: 'string',
                  description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.status.description',
                },
                answer: {
                  type: 'array',
                  description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.description',
                  items: {
                    type: 'object',
                    properties: {
                      content: {
                        type: 'object',
                        description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.description',
                        properties: {
                          formId: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.formId.description',
                          },
                          formVersion: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.formVersion.description',
                          },
                          formArgs: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.formArgs.description',
                          },
                          msg: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.msg.description',
                          },
                          formAppearance: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.formAppearance.description',
                          },
                          formData: {
                            type: 'string',
                            description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.content.formData.description',
                          },
                        },
                      },
                      type: {
                        type: 'string',
                        description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.type.description',
                      },
                      msgId: {
                        type: 'string',
                        description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.answer.items.msgId.description',
                      },
                    },
                  },
                },
                chat_id: {
                  type: 'string',
                  description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.chat_id.description',
                },
                at_chat_id: {
                  type: 'string',
                  description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.at_chat_id.description',
                },
                instance_id: {
                  type: 'string',
                  description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.instance_id.description',
                },
                log_id: {
                  type: 'string',
                  description: 'north.wssApiData.paths./chat.Websocket responses.content.schema.log_id.description',
                },
              },
            },
          },
        },
      },
    },
  },
};

export const oldWssAPIData = {
  info: {
    title: 'north.oldWssAPIData.info.title',
    description: 'north.oldWssAPIData.info.description',
    version: '1.0.0',
  },
  servers: [
    {
      url: '/agent/v1/api/{tenant_id}/ws',
      description: 'north.oldWssAPIData.servers.description',
    },
  ],
  paths: {
    '/chat': {
      summary: 'north.oldWssAPIData.paths./chat.summary',
      description:
        'north.oldWssAPIData.paths./chat.description',
      'First Request': {
        summary: 'north.oldWssAPIData.paths./chat.First Request.summary',
      },
      'Websocket Request': {
        summary: 'north.oldWssAPIData.paths./chat.Websocket Request.summary',
        description: 'north.oldWssAPIData.paths./chat.Websocket Request.description',
        requestBody: {
          description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.description',
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  requestId: {
                    type: 'string',
                    description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.requestId.description',
                    required: true,
                  },
                  method: {
                    type: 'string',
                    description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.method.description',
                    required: true,
                  },
                  params: {
                    type: 'object',
                    properties: {
                      data: {
                        type: 'object',
                        properties: {
                          app_id: {
                            type: 'string',
                            description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.app_id.description',
                            required: true,
                          },
                          chat_id: {
                            type: 'string',
                            description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.chat_id.description',
                            required: false,
                          },
                          question: {
                            type: 'string',
                            description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.question.description',
                            required: true,
                          },
                          context: {
                            type: 'object',
                            description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.description',
                            properties: {
                              use_memory: {
                                type: 'boolean',
                                description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.use_memory.description',
                                required: false,
                              },
                              user_context: {
                                type: 'object',
                                description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.user_context.description',
                                additionalProperties: true,
                                required: false,
                              },
                              at_app_id: {
                                type: 'string',
                                description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.at_app_id.description',
                                required: false,
                              },
                              at_chat_id: {
                                type: 'string',
                                description: 'north.oldWssAPIData.paths./chat.Websocket Request.requestBody.content.schema.params.data.context.at_chat_id.description',
                                required: false,
                              }
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
          'north.oldWssAPIData.paths./chat.Websocket responses.description',
        content: {
          'application/json': {
            schema: {
              type: 'object',
              properties: {
                requestId: {
                  type: 'string',
                  description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.requestId.description',
                },
                code: {
                  type: 'number',
                  description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.code.description'
                },
                msg: {
                  type: 'string',
                  description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.msg.description'
                },
                data: {
                  type: 'object',
                  description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.description',
                  properties: {
                    status: {
                      type: 'string',
                      description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.status.description',
                    },
                    answer: {
                      type: 'array',
                      description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.answer.description',
                      items: {
                        type: 'object',
                        properties: {
                          content: {
                            type: 'object',
                            description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.answer.items.content.description',
                            properties: {
                              content: {
                                type: 'string',
                                description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.answer.items.content.content.description',
                              },
                              type: {
                                type: 'string',
                                description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.answer.items.content.type.description',
                              },
                              msgId: {
                                type: 'string',
                                description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.answer.items.content.msgId.description',
                              },
                            },
                          },
                        },
                      },
                    },
                    chat_id: {
                      type: 'string',
                      description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.chat_id.description',
                    },
                    at_chat_id: {
                      type: 'string',
                      description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.at_chat_id.description',
                    },
                    instance_id: {
                      type: 'string',
                      description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.instance_id.description',
                    },
                    log_id: {
                      type: 'string',
                      description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.data.log_id.description',
                    },
                  }
                },
                completed: {
                  type: 'boolean',
                  description: 'north.oldWssAPIData.paths./chat.Websocket responses.content.schema.completed.description'
                }
              },
            },
          },
        },
      },
      parameters: [
        {
          schema: {
            type: 'string',
            description: 'north.oldWssAPIData.paths./chat.parameters.tenant_id.description',
          },
          in: 'path',
          deprecated: false,
          name: 'tenant_id',
          description: 'north.oldWssAPIData.paths./chat.parameters.tenant_id.description',
          required: true,
        }
      ],
    },
  },
};

export const oldSseAPIData = {
  properties: {
    status: {
      type: 'string',
      description: 'north.oldSseApiData.status.description',
    },
    answer: {
      type: 'array',
      description: 'north.oldSseApiData.answer.description',
      items: {
        type: 'object',
        properties: {
          content: {
            type: 'string',
            description: 'north.oldSseApiData.answer.items.content.description',
          },
          type: {
            type: 'string',
            description: 'north.oldSseApiData.answer.items.type.description',
          },
          msgId: {
            type: 'string',
            description: 'north.oldSseApiData.answer.items.msgId.description',
          },
        },
      },
    },
    chat_id: {
      type: 'string',
      description: 'north.oldSseApiData.chat_id.description',
    },
    at_chat_id: {
      type: 'string',
      description: 'north.oldSseApiData.at_chat_id.description',
    },
    instance_id: {
      type: 'string',
      description: 'north.oldSseApiData.instance_id.description',
    },
    log_id: {
      type: 'string',
      description: 'north.oldSseApiData.log_id.description',
    },
  }
};

export const errorCodeData = [
  {
    code: 0,
    message: 'north.errorCodeData.0',
  },
  {
    code: 90000000,
    message: 'north.errorCodeData.90000000',
  },
  {
    code: 90000001,
    message: 'north.errorCodeData.90000001',
  },
  {
    code: 90000002,
    message: 'north.errorCodeData.90000002',
  },
  {
    code: 90000003,
    message: 'north.errorCodeData.90000003',
  },
  {
    code: 90001001,
    message: 'north.errorCodeData.90001001',
  },
  {
    code: 90001002,
    message: 'north.errorCodeData.90001002',
  },
  {
    code: 90001003,
    message: 'north.errorCodeData.90001003',
  },
  {
    code: 90001011,
    message: 'north.errorCodeData.90001011',
  },
  {
    code: 90001012,
    message: 'north.errorCodeData.90001012',
  },
  {
    code: 90001013,
    message: 'north.errorCodeData.90001013',
  },
  {
    code: 90001014,
    message: 'north.errorCodeData.90001014',
  },
  {
    code: 90001015,
    message: 'north.errorCodeData.90001015',
  },
  {
    code: 90002000,
    message: 'north.errorCodeData.90002000',
  },
  {
    code: 90002001,
    message: 'north.errorCodeData.90002001',
  },
  {
    code: 90002002,
    message: 'north.errorCodeData.90002002',
  },
  {
    code: 90002003,
    message: 'north.errorCodeData.90002003',
  },
  {
    code: 90002004,
    message: '调用 MCP 服务失败，原因：{错误信息}。',
  },
  {
    code: 90002900,
    message: 'north.errorCodeData.90002900',
  },
  {
    code: 90002901,
    message: 'north.errorCodeData.90002901',
  },
  {
    code: 90002902,
    message: 'north.errorCodeData.90002902',
  },
  {
    code: 90002903,
    message: 'north.errorCodeData.90002903',
  },
  {
    code: 90002904,
    message: 'north.errorCodeData.90002904',
  },
  {
    code: 90002905,
    message: 'north.errorCodeData.90002905',
  },
  {
    code: 90002906,
    message: 'north.errorCodeData.90002906',
  },
  {
    code: 90002908,
    message: 'north.errorCodeData.90002908',
  },
  {
    code: 90002909,
    message: 'north.errorCodeData.90002909',
  },
  {
    code: 90002918,
    message: 'north.errorCodeData.90002918',
  },
  {
    code: 90002919,
    message: 'north.errorCodeData.90002919',
  },
  {
    code: 90002920,
    message: 'north.errorCodeData.90002920',
  },
  {
    code: 90002921,
    message: 'north.errorCodeData.90002921',
  },
  {
    code: 90002922,
    message: 'north.errorCodeData.90002922',
  },
  {
    code: 90002924,
    message: 'north.errorCodeData.90002924',
  },
  {
    code: 90002925,
    message: 'north.errorCodeData.90002925',
  },
  {
    code: 90002927,
    message: 'north.errorCodeData.90002927',
  },
  {
    code: 90002928,
    message: 'north.errorCodeData.90002928',
  },
  {
    code: 90002929,
    message: 'north.errorCodeData.90002929',
  },
  {
    code: 90002930,
    message: 'north.errorCodeData.90002930',
  },
  {
    code: 90002933,
    message: 'north.errorCodeData.90002933',
  },
  {
    code: 90002935,
    message: 'north.errorCodeData.90002935',
  },
  {
    code: 90002936,
    message: 'north.errorCodeData.90002936',
  },
  {
    code: 90002938,
    message: 'north.errorCodeData.90002938',
  },
  {
    code: 90002939,
    message: 'north.errorCodeData.90002939',
  },
  {
    code: 90002940,
    message: 'north.errorCodeData.90002940',
  },
  {
    code: 90002946,
    message: 'north.errorCodeData.90002946',
  },
  {
    code: 90002947,
    message: 'north.errorCodeData.90002947',
  },
  {
    code: 90002948,
    message: 'north.errorCodeData.90002948',
  },
  {
    code: 90002950,
    message: 'north.errorCodeData.90002950',
  },
  {
    code: 90002951,
    message: 'north.errorCodeData.90002951',
  },
  {
    code: 90002952,
    message: 'north.errorCodeData.90002952',
  },
  {
    code: 90002953,
    message: 'north.errorCodeData.90002953',
  },
  {
    code: 90002954,
    message: 'north.errorCodeData.90002954',
  },
  {
    code: 90002955,
    message: 'north.errorCodeData.90002955',
  },
  {
    code: 10007503,
    message: 'north.errorCodeData.10007503',
  },
  {
    code: 10000003,
    message: 'north.errorCodeData.10000003',
  },
  {
    code: 10007511,
    message: 'north.errorCodeData.10007511',
  },
  {
    code: 10007521,
    message: 'north.errorCodeData.10007521',
  },
  {
    code: 90002956,
    message: 'north.errorCodeData.90002956',
  },
  {
    code: 90002957,
    message: 'north.errorCodeData.90002957',
  },
  {
    code: 90002958,
    message: 'north.errorCodeData.90002958',
  },
  {
    code: 90002959,
    message: 'north.errorCodeData.90002959',
  },
  {
    code: 90002960,
    message: 'north.errorCodeData.90002960',
  },
  {
    code: 90002961,
    message: 'north.errorCodeData.90002961',
  },
  {
    code: 90002962,
    message: 'north.errorCodeData.90002962',
  },
  {
    code: 90002963,
    message: 'north.errorCodeData.90002963',
  },
  {
    code: 90002965,
    message: 'north.errorCodeData.90002965',
  },
  {
    code: 90002973,
    message: 'north.errorCodeData.90002973',
  },
  {
    code: 90002998,
    message: 'north.errorCodeData.90002998',
  },
];

export const resCode = [
  {
    code: 400,
    message: 'north.resCode.400',
  },
  {
    code: 403,
    message: 'north.resCode.403',
  },
  {
    code: 404,
    message: 'north.resCode.404',
  },
  {
    code: 500,
    message: 'north.resCode.500',
  },
];

export const resOKCode = [
  {
    code: 200,
    message: 'north.resOkCode.200',
  },
  {
    code: 201,
    message: 'north.resOkCode.201',
  },
  {
    code: 204,
    message: 'north.resOkCode.204',
  },
  {
    code: 205,
    message: 'north.resOkCode.205',
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

export const reqOldWssData = [
  {
    requestId: '8b7135e902ed45009c95c7880e8a2e66',
    method: 'appChat',
    params: {
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

export const resOldWssData = [
  {
    requestId: '8b7135e902ed45009c95c7880e8a2e67',
    code: 0,
    msg: null,
    data: {
      status: 'ARCHIVED',
      answer: [
        {
          content: '中国四大名著指的是《红楼梦》、《西游记》、《水浒传》和《三国演义》。它们是中国古代文学的经典之作，被广泛地传颂和阅读。这四部小说都具有深刻的思想内涵和丰富的人物形象，是中国文学史上的重要里程碑。',
            type: 'MSG',
            msgId: null
        }
      ],
      chat_id: 'e4cff2d2eb1949efb43b64802c8750ce',
      at_chat_id: null,
      instance_id: 'a302c335a50148c2b44460ba183a07b5',
      log_id: '72'
    },
    completed: false
  }
];

export const resOldSseData = [
  {
    status: 'RUNNING',
    answer: [{
      content: {
        formId: null,
        formVersion: null,
        formArgs: null,
        msg: '你好',
        formAppearance: null,
        formData: null
      },
      type: 'QUESTION',
      msgId: null
    }
    ],
    chat_id: '287cb0ba6d6d4569a3d46f610b9e6b66',
    at_chat_id: null,
    instance_id: '908931206040447989f605eccad62919',
    log_id: '1334'
  },
  {
    status: 'READY',
    answer: null,
    chat_id: '97c2ceb8099648bbbf2d61d44dbf1000',
    at_chat_id: null,
    instance_id: '1e18331c3bfd4a68aec21ea2c2d65037',
    log_id: null
  },
  {
    status: 'ARCHIVED',
    answer: [{
      content: '你好！很高兴为你提供帮助。请告诉我你有什么问题或需要什么信息，我会尽力协助你。',
      type: 'MSG',
      msgId: null
    }
    ],
    chat_id: '287cb0ba6d6d4569a3d46f610b9e6b66',
    at_chat_id: null,
    instance_id: '908931206040447989f605eccad62919',
    log_id: '1335'
  }
];
export const urlMap = {
  chatsApps: '/api/app/v1/tenants/{tenantId}/chats/apps/{appId}',
  chats: '/api/app/v1/tenants/{tenantId}/chats',
  instances: '/api/app/v1/tenants/{tenantId}/chats/instances/{currentInstanceId}',
  categories: '/api/app/v1/tenants/{tenantId}/apps/{appId}/prompt/categories',
  inspirations: '/api/app/v1/tenants/{tenantId}/apps/{appId}/prompt/categories/{categoryId}/inspirations',
  // translationAudio: '/api/app/v1/tenants/{tenantId}/translation/audio',
  file: '/api/app/v1/tenants/{tenantId}/file',
  // translationText: '/api/app/v1/tenants/{tenantId}/translation/text',
  apps: '/api/app/v1/tenants/{tenantId}/apps',
  appsConfig: '/api/app/v1/tenants/{tenantId}/apps/{appId}/config',
  chatLogs: '/api/app/v1/tenants/{tenant_id}/log/app/{app_id}/chat/{chat_id}',
  createFeedback: '/api/app/v1/aipp/user/feedback',
  updateFeedback: '/api/app/v1/aipp/user/feedback/{instanceId}',
};

export const oldUrlMap = {
  chatsOld: '/agent/v1/api/{tenant_id}/app_chat'
}

export const HTTPMap = {
  http: 'http',
};
