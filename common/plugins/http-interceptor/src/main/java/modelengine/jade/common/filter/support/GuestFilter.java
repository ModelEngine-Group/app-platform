package modelengine.jade.common.filter.support;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import java.util.Collections;
import java.util.List;

/**
 * 表示游客访问 http 请求过滤器。
 *
 * @author 鲁为
 * @since 2025-08-28
 */
@Component
public class GuestFilter implements HttpServerFilter {
    public static final String X_GUEST_USERNAME = "X-Guest-Username";

    @Override
    public String name() {
        return "GuestFilter";
    }

    @Override
    public int priority() {
        return Order.HIGHEST;
    }

    @Override
    public List<String> matchPatterns() {
        return Collections.singletonList("/v1/api/guest/**");
    }

    @Override
    public List<String> mismatchPatterns() {
        return Collections.singletonList("");
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) {
        String guestName = request.headers().first(X_GUEST_USERNAME).orElse(StringUtils.EMPTY);
        UserContext operationContext = new UserContext(guestName,
                HttpRequestUtils.getUserIp(request),
                HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> chain.doFilter(request, response));
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }
}
