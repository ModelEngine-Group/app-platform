package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.annotation.Component;

import java.util.function.Function;

public interface McpClientFactory extends Function<String, LangChain4jMcpClient> {
}
