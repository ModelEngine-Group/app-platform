# AppBuilder 开发规范

## 数据对象命名规范

+ PO（持久对象）：跟数据库表是一一对应的，一个 PO/DO 数据是表的一条记录。
+ DTO（数据传输对象）：前后端之间的传输对象。
+ VO（视图对象）：返回给前端用于展示的数据。
+ Query（数据查询对象）：超过 2 个参数的数据库查询封装，禁止使用 map。
+ BO（业务对象）：在数据库访问层由框架直接生成或在服务层组装不同的 PO。

## controller 对外接口规范

对外 http 接口整体遵从 restful 接口规范。

### http 请求方式

| 请求方式     | 含义   |
|:---------|:-----|
| `GET`    | 读取   |
| `POST`   | 新建   |
| `PUT`    | 全部更新 |
| `PATCH`  | 部分更新 |
| `DELETE` | 删除   |

### http 路由

+ 每个⽹址中不能有动词，只能有名词。并且应该使⽤复数，除⾮没有合适的复数形式，如：weather。
  https://api.example.com/v1/topics/ — 所有帖⼦
+ 对于个体或个类名下资源，可以直接在路径上添加具体的id来表现，如下：
  https://api.example.com/v1/topics/100001/info — id 为 100001 的帖⼦的详情
  https://api.example.com/v1/users/12345/topics/ — id 为 12345 的⽤户名下所有帖⼦
  更多例子[带请求方法]
+ [GET] https://api.example.com/v1/topics/ — 获取所有帖⼦(列表)
+ [POST] https://api.example.com/v1/topics/ — 新建帖⼦
+ [PUT] https://api.example.com/v1/topics/100001 — 更新完整帖⼦
+ [PATCH] https://api.example.com/v1/topics/100001 — 更新帖⼦部分信息
+ [DELETE] https://api.example.com/v1/topics/100001 — 删除帖⼦
+ [GET] https://api.example.com/v1/groups/1/topics/ — 获取某组所有帖⼦(列表)
+ [GET] https://api.example.com/v1/users/12345/profile — 获取某⽤户资料
+ [PUT] https://api.example.com/v1/users/12345/profile — 更新某⽤户资料
+ [GET] https://api.example.com/v1/users/12345/labels — 获取某⽤户所有标签

`NOTE`
避免使用多级 URL，不利于扩展，语义也不明确，理解困难；
多级 URL：
```markup
GET /authors/{aid}/categories/{cid}
```
正确的做法是：
```markup
GET /authors/{aid}?categories={cid}
```
