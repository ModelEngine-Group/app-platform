/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.authentication.context;

/**
 *  表示 http 请求上下文。
 *
 * @author 陈潇文
 * @since 2024-07-30
 */
public class UserContext {
    private String name;
    private String ip;
    private String language;
    private String authToken;
    private String csrfToken;

    public UserContext(String name, String ip, String language) {
        this(name, ip, language, null, null);
    }

    public UserContext(String name, String ip, String language, String authToken, String csrfToken) {
        this.name = name;
        this.ip = ip;
        this.language = language;
        this.authToken = authToken;
        this.csrfToken = csrfToken;
    }

    /**
     * 设置发起请求的客户端ip。
     *
     * @param ip 表示客户端 ip 的{@link String}
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取发起请求的客户端ip。
     *
     * @return 表示客户端 ip 的{@link String}
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置发起请求的客户端用户名。
     *
     * @param name 表示客户端用户名的{@link String}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取发起请求的客户端用户名。
     *
     * @return 表示客户端用户名的{@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * 设置发起请求的语言 language。
     *
     * @param language 表示客户端 language 的{@link String}
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 获取发起请求的语言 language。
     *
     * @return 表示客户端 language 的{@link String}
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 设置认证令牌。
     *
     * @param authToken 表示认证令牌的 {@link String}。
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * 获取认证令牌。
     *
     * @return 表示认证令牌的 {@link String}。
     */
    public String getAuthToken() {
        return this.authToken;
    }

    /**
     * 设置 CSRF 令牌。
     *
     * @param csrfToken 表示 CSRF 令牌的 {@link String}。
     */
    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    /**
     * 获取 CSRF 令牌。
     *
     * @return 表示 CSRF 令牌的 {@link String}。
     */
    public String getCsrfToken() {
        return this.csrfToken;
    }
}
