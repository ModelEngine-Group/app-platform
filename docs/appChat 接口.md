# appChat 接口

## 功能介绍

会话接口，用于传递会话信息。

## 调用方法

- **HTTP 方法**：POST
- **URI**：`/v1/api/{tenant_id}/app_chat`

## 请求参数

### 1. 请求 Path 参数

**表 5-1 请求 path 参数列表**

| 名称      | 类型   | 是否必选 | 描述   |
| --------- | ------ | -------- | ------ |
| tenant_id | String | 是       | 租户id |

### 2. 请求 Header 参数

**表 5-2 请求 header 参数列表**

| 名称          | 类型   | 是否必选 | 描述           |
| ------------- | ------ | -------- | -------------- |
| Content-Type  | String | 是       | 请求体的格式   |
| Authorization | String | 是       | API 鉴权的秘钥 |

### 3. 请求 Body 参数

#### 3.1 请求 Body 总览

**表 5-3 请求 body 参数列表**

| 名称                  | 类型             | 是否必选 | 描述         |
| --------------------- | ---------------- | -------- | ------------ |
| app_chat_request_body | App_chat_request | 是       | 对话请求参数 |

#### 3.2 App_chat_request 参数

**表 5-4 App_chat_request**

| 名称     | 类型    | 是否必选 | 描述                                                         |
| -------- | ------- | -------- | ------------------------------------------------------------ |
| app_id   | String  | 是       | 用于对话的 app 的 id                                         |
| chat_id  | String  | 否       | 当前对话的 id；第一次发起对话时生成，后续可使用该 id 连续对话 |
| question | String  | 是       | 向大模型询问的问题                                           |
| context  | Context | 是       | 初始化上下文                                                 |

#### 3.3 Context 参数

**表 5-5 Context**

| 名称         | 类型    | 是否必选 | 描述                                                         |
| ------------ | ------- | -------- | ------------------------------------------------------------ |
| use_memory   | Boolean | 是       | 对话时是否使用历史对话记录                                   |
| user_context | Object  | 否       | 当开始节点配置了多输入时，可在此字段增加对应的多输入字段；该字段可为空 |

## 请求示例

> **示例：记忆功能开启**
>
> ```
> http复制Host: localhost.com
> Content-Type: application/json
> Accept: application/json
> POST "https://{server-endpoint}/angent/v1/api/{tenant_id}/app_chat"
> 
> {
>     "app_id": "490ec09536e040898ed7497a9cf9faa5",
>     "question": "你叫潇A请记住",
>     "context": {
>         "use_memory": true,
>         "user_context": {}
>     },
>     "chat_id": "287cb0ba6d6d4569a3d46f610b9e6b66"
> }
> ```
>
> 此时 `use_memory` 为 `true`，表示开启记忆功能，保留历史会话记录。
>
> ------
>
> **示例：继续对话（使用返回的 chat_id）**
>
> ```
> json复制{
>     "app_id": "490ec09536e040898ed7497a9cf9faa5",
>     "question": "你叫什么名字",
>     "context": {
>         "use_memory": true,
>         "user_context": {}
>     },
>     "chat_id": "287cb0ba6d6d4569a3d46f610b9e6b66"
> }
> ```
>
> 补充返回的 `chat_id` 后，后续对话可以基于之前的内容继续提问。

## 响应参数

返回状态码为 **200** 时，调用成功。

**表 5-6 响应 Body 参数列表**

| 名称        | 类型   | 是否必选 | 描述                                                         |
| ----------- | ------ | -------- | ------------------------------------------------------------ |
| status      | String | 是       | 应用流式输出的状态，可能的值：`RUNNING`、`READY`、`ARCHIVED`、`ERROR` |
| answer      | Answer | 是       | 大模型回答                                                   |
| chat_id     | String | 是       | 大模型对话 id                                                |
| at_chat_id  | String | 否       | 大模型引用的对话 id                                          |
| instance_id | String | 是       | 当前问答的实例 id                                            |
| log_id      | String | 否       | 大模型对话记录 id                                            |

根据 `answer.type` 的不同，返回的 `answer` 参数结构如下：

### 1. 当 `answer.type` 为 `"QUESTION"` 时

**表 5-7 Answer**

| 名称    | 类型             | 是否必选 | 描述                                                         |
| ------- | ---------------- | -------- | ------------------------------------------------------------ |
| content | Content (Object) | 是       | 大模型回答内容                                               |
| type    | String           | 是       | 大模型回答类型；可能的值：`QUESTION`、`MSG`、`FORM`、`KNOWLEDGE`、`META_MSG`、`ERROR` |
| msgId   | String           | 否       | 大模型回答的 id                                              |

**表 5-8 Content**

| 名称           | 类型   | 是否必选 | 描述                     |
| -------------- | ------ | -------- | ------------------------ |
| formId         | String | 否       | 大模型表单节点的 id      |
| formVersion    | String | 否       | 大模型表单节点的版本     |
| formArgs       | String | 否       | 大模型表单节点的参数     |
| msg            | String | 是       | 大模型对话提示信息       |
| formAppearance | String | 否       | 大模型表单节点的渲染数据 |
| formData       | String | 否       | 大模型表单节点的填充数据 |
| infos          | Infos  | 是       | 日志额外信息             |

### 2. 当 `answer.type` 为 `"MSG"` 时

**表 5-10 Answer**

| 名称    | 类型   | 是否必选 | 描述                         |
| ------- | ------ | -------- | ---------------------------- |
| content | Object | 是       | 大模型回答内容               |
| type    | String | 是       | 大模型回答类型；可能的值同上 |
| msgId   | String | 否       | 大模型回答的 id              |

### 3. 当 `answer.type` 为 `"FORM"` 时

**表 5-11 Answer**

| 名称    | 类型             | 是否必选 | 描述            |
| ------- | ---------------- | -------- | --------------- |
| content | Content (Object) | 是       | 大模型回答内容  |
| type    | String           | 是       | 大模型回答类型  |
| msgId   | String           | 否       | 大模型回答的 id |

**表 5-12 Content**

| 名称             | 类型                    | 是否必选 | 描述                      |
| ---------------- | ----------------------- | -------- | ------------------------- |
| formData         | Map                     | 否       | 大模型表单的初始化入参    |
| formAppearance   | FormAppearance (Object) | 否       | 大模型表单节点的样式      |
| parentInstanceId | String                  | 否       | 大模型表单节点父实例的 id |

**表 5-13 FormAppearance**

| 名称        | 类型    | 是否必选 | 描述                                                         |
| ----------- | ------- | -------- | ------------------------------------------------------------ |
| schema      | Object  | 否       | 大模型表单节点的 JSON Schema，描述参数类型、描述、是否必填、顺序等，格式参考 [JSON Schema](https://json-schema.apifox.cn/) |
| imgUrl      | String  | 否       | 大模型表单节点的预览图访问地址                               |
| fileSize    | Integer | 否       | 大模型表单节点压缩包的大小                                   |
| fileName    | String  | 否       | 大模型表单节点压缩包的名称                                   |
| fileUuid    | String  | 否       | 大模型表单节点文件的 uuid                                    |
| iframeUrl   | String  | 否       | 大模型表单节点的 iframe 访问地址                             |
| description | String  | 否       | 大模型表单节点的描述                                         |

### 4. 当 `answer.type` 为 `"KNOWLEDGE"` 时

**表 5-14 Answer**

| 名称    | 类型             | 是否必选 | 描述            |
| ------- | ---------------- | -------- | --------------- |
| content | Content (Object) | 是       | 大模型回答内容  |
| type    | String           | 是       | 大模型回答类型  |
| msgId   | String           | 否       | 大模型回答的 id |

**表 5-15 Content**

| 名称 | 类型        | 是否必选 | 描述                                               |
| ---- | ----------- | -------- | -------------------------------------------------- |
| id   | Id (String) | 是       | 知识片段的标识，用于构造溯源标签及大模型输入提示词 |

### 5. 当 `answer.type` 为 `"META_MSG"` 时

**表 5-17 Answer**

| 名称    | 类型             | 是否必选 | 描述            |
| ------- | ---------------- | -------- | --------------- |
| content | Content (String) | 是       | 大模型回答内容  |
| type    | String           | 是       | 大模型回答类型  |
| msgId   | String           | 否       | 大模型回答的 id |

**表 5-18 Content**

| 名称      | 类型              | 是否必选 | 描述                                   |
| --------- | ----------------- | -------- | -------------------------------------- |
| type      | String            | 是       | 大模型知识检索节点信息类型             |
| data      | String            | 是       | 应用最终返回的结果                     |
| reference | reference(String) | 是       | 大模型知识检索节点引用信息，信息可为空 |

**表 5-19 Reference**

| 名称 | 类型        | 是否必选 | 描述                                               |
| ---- | ----------- | -------- | -------------------------------------------------- |
| id   | Id (String) | 是       | 知识片段的标识，用于构造溯源标签及大模型输入提示词 |

### 6. 当 `answer.type` 为 `"ERROR"` 时

**表 5-21 Answer**

| 名称    | 类型             | 是否必选 | 描述                         |
| ------- | ---------------- | -------- | ---------------------------- |
| content | Content (Object) | 是       | 大模型回答报错信息           |
| type    | String           | 是       | 大模型回答类型；可能的值同上 |
| msgId   | String           | 否       | 大模型回答的 id              |

## 响应示例

### 示例 1: `answer.type = "QUESTION"`

```
json复制{
    "status": "RUNNING",
    "answer": [{
        "content": {
            "formId": null,
            "formVersion": null,
            "formArgs": null,
            "msg": "你好",
            "formAppearance": null,
            "formData": null,
            "infos": {
                "input": {
                    "q": "你好",
                    "Question": "你好"
                }
            }
        },
        "type": "QUESTION",
        "msgId": null
    }],
    "chat_id": "96820749483e40bb8d7dcbe7b223cef1",
    "at_chat_id": null,
    "instance_id": "1672193c3ad84524b7774738a6a59ede",
    "log_id": "63"
}
```

### 示例 2: `answer.type = "MSG"`

```
json复制{
    "status": "ARCHIVED",
    "answer": [{
        "content": "你好！有什么可以帮助你的吗？",
        "type": "MSG",
        "msgId": null
    }],
    "chat_id": "e50feebeb3fb49d0a3a89fd33ef55c61",
    "at_chat_id": null,
    "instance_id": "bb94545e98fc4b4997c442cd22f0b453",
    "log_id": "188"
}
```

### 示例 3: `answer.type = "FORM"`

```
json复制{
    "status": "ARCHIVED",
    "answer": [{
        "content": {
            "formData": {
                "forma": "form",
                "formb": "form"
            },
            "formAppearance": {
                "schema": {
                    "return": {
                        "type": "object",
                        "properties": {
                            "forma": {
                                "type": "string"
                            },
                            "formb": {
                                "type": "string"
                            }
                        }
                    },
                    "parameters": {
                        "type": "object",
                        "required": ["a", "b"],
                        "properties": {
                            "forma": {
                                "type": "string",
                                "default": "haha"
                            },
                            "b": {
                                "type": "string",
                                "default": "heihei"
                            }
                        }
                    }
                },
                "imgUrl": "smart_form/b10a28e7-9be6-4ff1-94cc-f2041eeb3c05/form.png",
                "fileSize": 238534,
                "fileName": "example.zip",
                "fileUuid": "d0d5208242ad4edeb282523203871597",
                "iframeUrl": "smart_form/b10a28e7-9be6-4ff1-94cc-f2041eeb3c05/build/index.html",
                "description": ""
            },
            "parentInstanceId": null
        },
        "type": "FORM",
        "msgId": null
    }],
    "chat_id": "561cdb554d3e45c18fe3465976b1db00",
    "at_chat_id": null,
    "instance_id": "3ee55db870d242fca7c2039054efaa07",
    "log_id": "196"
}
```

### 示例 4: `answer.type = "KNOWLEDGE"`

```
json复制{
    "status": "RUNNING",
    "answer": [{
        "content": "{\"f77c5d\":{\"score\":0.8659812211990356,\"id\":\"455297306354254618\",\"text\":\"xx 公司创建于1987年。\"},\"15fca2\":{\"score\":0.8659812211990356,\"id\":\"455297306354254617\",\"text\":\"xx公司创建于1835年。\"},\"a36965\":{\"score\":0.6902795433998108,\"id\":\"455297306354254619\",\"text\":\"1000人\"}}",
        "type": "KNOWLEDGE",
        "msgId": "39a6ee01-9b73-417f-b859-1367e62df3d1"
    }],
    "chat_id": "1daa49d484cc4bc48eff0edd0591f0a9",
    "at_chat_id": null,
    "instance_id": "15c5f5ed1c4344f59e26901b24b505e3",
    "log_id": null
}
```

### 示例 5: `answer.type = "META_MSG"`

```
json复制{
    "status": "ARCHIVED",
    "answer": [{
        "content": {
            "formId": null,
            "formVersion": null,
            "formArgs": null,
            "msg": "[{\"type\":\"TEXT_WITH_REFERENCE\",\"data\":\"It seems like you've started to type a web address but it's incomplete. If you need information from a specific website, please provide the full URL. Or, if you're asking about the World Wide Web (www), it's a system of interlinked hypertext documents accessed via the Internet. It was invented by Tim Berners-Lee in 1989. Can I assist you further?\",\"reference\":[]}]",
            "formAppearance": null,
            "formData": null,
            "infos": null
        },
        "type": "META_MSG",
        "msgId": null
    }],
    "chat_id": "dfea10af08f34f0696e33f72ce458dbc",
    "at_chat_id": null,
    "instance_id": "96a4f6e50cfe4fceae33e4ba84c77764",
    "log_id": "8"
}
```

### 示例 6: `answer.type = "ERROR"`

```
json复制{
    "status": "ERROR",
    "answer": [{
        "content": "运行失败，表单不存在或已经被删除。",
        "type": "ERROR",
        "msgId": null
    }],
    "chat_id": null,
    "at_chat_id": null,
    "instance_id": "e37ed0611d6149b3a1f46627e2694bc4",
    "log_id": "79"
}
```

