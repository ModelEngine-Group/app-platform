package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.annotation.Component;

@Component
public class DefaultMcpClientFactory implements McpClientFactory {
    @Override
    public LangChain4jMcpClient apply(String url) {
        return new LangChain4jMcpClient(url);
    }
}
