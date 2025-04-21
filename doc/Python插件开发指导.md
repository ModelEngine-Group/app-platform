## 插件的基本信息

**插件上传需要首先将插件打包**，当前插件打包**包含三个部分**：

- 工具包（python: `zip`/`tar`）。
- 工具的元数据（`tools.json`）。
- 插件的完整性校验与唯一性校验以及插件的基本信息（`plugin.json`）。

> 注意：
> 
> * 上传插件必须包含且只能包含以上三个文件；
> * 上传插件的文件命名不可以包含中文；
> * 上传插件不可以解压之后超过 100M；

## 插件的示例

定义一个python的乘法器，代码如下：

```python
from fitframework.api.decorators import fitable

@fitable("genericable_id_multiplier", "fitable_id_multiplier")
def multiplier(a: int, b: int) -> int:
    return a * b
```

> 注意：
> 
> 1. `from fitframework.api.decorators import fitable` 为 FIT-python 框架自定义的装饰器，定义 genericableId（"genericable_id_multiplier"）以及 fitableId（"fitable_id_multiplier"）支持插件动态加载至框架中，在流程编排调用中，根据 genericableId&fitableId 可以唯一的定位到插件的实现。
> 2. 虽然 python 代码可以自动识别入参类型，但是被 `@fitable` 注解下的方法需要添加入参的类型以及返回参数的类型，否则会导致 http 调用过程中因为序列化问题导致调用失败或者插件部署失败。

## 插件打包

​       在插件基本信息中有详细说明，上传插件包含三个文件分别是 `zip`/`tar` 包（即插件源码目录或文件的打包，例如将上述python源码打包为 `multiplier.zip`），工具元数据信息 `tools.json` 以及插件的配置信息 `plugin.json` 数据，在上述的插件示例中，可以直接将源码打包成 `zip`/`tar` 文件，打包方式可以直接使用本机 PC 的压缩工具即可。

对于`tools.json`文件，下边根据上述的 python 源码给定以下的元数据信息：

```json
{
  "version" : "1.0.0",
  "definitionGroups" : [ {
    "name" : "Python-Plugin",
    "summary" : "",
    "description" : "",
    "extensions" : { },
    "definitions" : [ {
      "schema" : {
        "name" : "PythonTool",
        "description" : "用于示例的python工具",
        "parameters" : {
          "type" : "object",
          "properties" : {
            "a" : {
              "defaultValue" : "",
              "description" : "乘数1",
              "name" : "a",
              "type" : "integer",
              "examples" : "",
              "required" : true
            },
            "b" : {
              "defaultValue" : "",
              "description" : "乘数2",
              "name" : "b",
              "type" : "integer",
              "examples" : "",
              "required" : true
            }
          },
          "required" : [ "a", "b" ]
        },
        "order" : [ "a", "b" ],
        "return" : {
          "type" : "integer",
          "convertor" : ""
        }
      }
    } ]
  } ],
  "toolGroups" : [ {
    "name" : "Python-Plugin-Impl",
    "summary" : "",
    "description" : "",
    "extensions" : { },
    "definitionGroupName" : "Python-Plugin",
    "tools" : [ {
      "schema" : {
        "name" : "乘法器",
        "description" : "Python示例插件工具",
        "parameters" : {
          "type" : "object",
          "properties" : {
            "a" : {
              "name" : "a",
              "type" : "integer",
              "required" : false
            },
            "b" : {
              "name" : "b",
              "type" : "integer",
              "required" : false
            }
          },
          "required" : [ ]
        },
        "order" : [ "a", "b" ],
        "return" : {
          "name" : "",
          "description" : "Python乘法器的结果",
          "type" : "integer",
          "convertor" : "",
          "examples" : ""
        }
      },
      "runnables" : {
        "FIT" : {
          "genericableId" : "genericable_id_multiplier",
          "fitableId" : "fitable_id_multiplier"
        }
      },
      "extensions" : {
        "tags" : [ "FIT" ]
      },
      "definitionName" : "PythonTool"
    } ]
  } ]
}
```

> **注意**：由于 python 不支持编译，当前未开发生成 python 插件规范的 `tools.json` 的工具，因此可以参考 java 插件开发指导，配置 pom 规范
> 
> ```xml
> <?xml version="1.0" encoding="UTF-8"?>
> <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>          xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
>     <modelVersion>4.0.0</modelVersion>
>
>     <groupId>modelengine.fit</groupId>
>     <artifactId>multiplier</artifactId>
>     <version>1.0-SNAPSHOT</version>
>
>     <dependencies>
>         <dependency>
>             <groupId>org.fitframework</groupId>
>             <artifactId>fit-api</artifactId>
>             <version>3.6.0-SNAPSHOT</version>
>         </dependency>
>         <dependency>
>             <groupId>modelengine.fit.jade.service</groupId>
>             <artifactId>tool-service</artifactId>
>             <version>1.0.0-SNAPSHOT</version>
>         </dependency>
>     </dependencies>
>
>     <build>
>         <plugins>
>             <plugin>
>                 <groupId>org.fitframework</groupId>
>                 <artifactId>fit-build-maven-plugin</artifactId>
>                 <version>3.6.0-SNAPSHOT</version>
>                 <executions>
>                     <execution>
>                         <id>build-plugin</id>
>                         <goals>
>                             <goal>build-plugin</goal>
>                         </goals>
>                     </execution>
>                 </executions>
>             </plugin>
>             <plugin>
>                 <groupId>modelengine.fit.jade.maven.plugin</groupId>
>                 <artifactId>tool-maven-plugin</artifactId>
>                 <version>1.0.0-SNAPSHOT</version>
>                 <executions>
>                     <execution>
>                         <id>build-tool</id>
>                         <goals>
>                             <goal>build-tool</goal>
>                         </goals>
>                     </execution>
>                 </executions>
>             </plugin>
>         </plugins>
>     </build>
> </project>
> ```  
>    
> 编写接口如下：
>    
> ```java
> @Group(name = "Python-Plugin")
> public interface LoopToolService {
>  @ToolMethod(name = "PythonTool", description = "用于示例的python工具")
>  @Genericable("genericable_id_multiplier")
>  int Multi(@Property(description = "乘数1", required = true) int a,
>          @Property(description = "乘数2", required = true) int b);
> }
> ```
>    
> 编写实现如下：
>    
> ```java
> @Group(name = "Python-Plugin-Impl")
> public class LoopToolServiceImpl implements LoopToolService {
>  @Override
>  @Fitable("fitable_id_multiplier")
>  @ToolMethod(name = "乘法器", description = "Python示例插件工具", extensions = {
>          @Attribute(key = "tags", value = "FIT")
>  })
>     @Property(description = "Python乘法器的结果")
>     public int Multi(int a, int b) {
>         return a * b;
>     }
> }
> ```
> 
> 执行 `mvn clean install` 即可在生成 `tools.json` 文件，位置在 `/target/classes/` 目录下。

* definitionGroups：表示各定义组，包括定义组名字和定义组下各定义。
  * [definitionGroup]
    * name：定义组名字【**不允许为空及空白，不允许重复**】
    * definitions：该定义组下各个定义
      * [definition]
        * schema：表示定义的结构，包括参数的输入，类型以及描述，返回值的描述与类型以及参数传入的顺序，这部分需要符合《json schema规范》。
          * name：方法名【**不允许为空及空白，定义组下的定义名不允许重复**】
          * description：方法描述【**不允许为空及空白，不允许重复**】
          * parameters：方法入参
            * type
            * properties
            * required【**其数量必须小于等于入参数量，且必须是入参中参数**】
          * order：参数顺序【**需要与参数数量一致，且必须是入参中参数**】
          * return：方法出参
* toolGroups:
  * [toolGroup]
    * definitionGroupName：该实现组实现的定义组的名字【**参考定义组名约束**】
    * name：实现组名字【**不允许为空及空白，定义组下的实现组名不允许重复**】
    * tools：该实现组下的方法
      * [tool]
        * definitionName：该方法实现的定义的名字【**参考定义名约束**】
        * schema：方法结构，字段与定义组 schema 一致。
        * runnables：表示对运行规范的描述，包含 FIT 框架信息，来自 `@fitable` 注解。
        * extensions：扩展信息，需要添加标签信息。

> 约束情况：
> 
> schema：必须要符合 json schema 规范（网上可以搜索得到，通用的 json 规范）
> 
> * 必须包含 name，该值支持中文、英文、数字、空格、中划线、下划线组合。
> * 必须包含 description，该值被大模型识别，必填项。
> * 必须包含 parameters，第一层type类型必须为 object。
> * 必须包含 required，内容不可以为 properties 下参数名之外的参数名。
> * 可以包含 order，若写必须为 properties 下的所有参数名的列表，若不写，则默认 properties 下的所有参数名列表（按顺序）。
> * 必须包含 return，符合 json schema 格式，其中 converter 暂时未做处理，可写可不写，默认为空。
> 
> runnables：应包含 FIT 字段，该字段下包含 fitableId 与 genericableId，这里两个值支持 FIT 调用，可以实现在前端界面准确的调用对应的实现。【fitableId 与 genericableId 仅支持数字、大小写字母以及 '-'、'_'、'*'、'.' 字符且长度在128以内】
> 
> extension：必须包含 tags 字段，该字段下包含一个数组，里边的值为对应工具的标签信息

插件元数据信息 plugin.json

```json
{
  "checksum": "dc2306935b8dfd838cd877ca618fb68791779876a1737cba2d89c1e404344e55",
  "name": "Python乘法器",
  "description": "这是一个Python乘法器示例工具",
   "type": "python",
   "uniqueness": {
       "name": "python-demo-plugin"
   }
}
```

| 参数名      | 说明                                                                                                          |
| ----------- |-------------------------------------------------------------------------------------------------------------|
| checksum    | 表示用户打包的源码插件文件的SHA-256的值，这里作为完整性校验的标识。**这里建议使用 git bash 执行 `sha256sum multiplier.zip` 生成该值**           |
| description | 插件的描述信息。                                                                                                    |
| name        | 插件名称。                                                                                                       |
| type        | 插件的类型。                                                                                                      |
| uniqueness  | 插件的唯一性标识。uniqueness 包含一个参数：`name`。这些需要用户自定义，保证新增插件与之前插件的不同。**注意这些字段不可以使用空格以及中文，请符合标准的命名规范，使用英文或数字，中间以中划线分割** |

> 注意上述自字段均为必填字段。


