package com.dental.config;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 配置类
 * 注意：当前配置仅引入依赖，未启用权限拦截
 */
@Configuration
public class ShiroConfig {

    /**
     * 配置 Shiro 过滤器工厂
     * 设置所有请求通过，不进行权限拦截
     */
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        
        // 设置登录页面，如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 设置登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/");
        // 设置未授权页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        
        // 添加允许OPTIONS请求通过，解决CORS问题
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("corsFilter", new CORSFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        
        // 拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/user/register", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        // OPTIONS请求不拦截
        filterChainDefinitionMap.put("/**", "corsFilter,anon");
        
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
    
    /**
     * 安全管理器配置
     */
    @Bean
    public SecurityManager securityManager(Realm realm, ModularRealmAuthenticator authenticator, 
                                          ModularRealmAuthorizer authorizer, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setAuthenticator(authenticator);
        securityManager.setAuthorizer(authorizer);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }
    
    /**
     * 创建Realm
     */
    @Bean
    public Realm realm(CredentialsMatcher credentialsMatcher) {
        CustomRealm realm = new CustomRealm();
        realm.setCredentialsMatcher(credentialsMatcher);
        return realm;
    }
    
    /**
     * 凭证匹配器
     */
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        // 设置加密算法
        credentialsMatcher.setHashAlgorithmName("SHA-256");
        // 设置加密次数
        credentialsMatcher.setHashIterations(1024);
        // 存储凭证在数据库中是否含有Hex编码
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }
    
    /**
     * 认证器
     */
    @Bean(name = "authenticator")
    public ModularRealmAuthenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(authenticationStrategy());
        return authenticator;
    }
    
    /**
     * 认证策略
     */
    @Bean
    public AuthenticationStrategy authenticationStrategy() {
        return new AtLeastOneSuccessfulStrategy();
    }
    
    /**
     * 授权器
     */
    @Bean(name = "authorizer")
    public ModularRealmAuthorizer authorizer() {
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        // 设置权限解析器
        authorizer.setPermissionResolver(new WildcardPermissionResolver());
        return authorizer;
    }
    
    /**
     * 会话管理器
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置会话过期时间为1小时
        sessionManager.setGlobalSessionTimeout(60 * 60 * 1000);
        // 会话过期时是否删除会话
        sessionManager.setDeleteInvalidSessions(true);
        // 是否启用/禁用会话验证调度器
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 禁用URL重写 URL中不会添加JSESSIONID参数
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }
    
    // 添加支持CORS的过滤器
    public class CORSFilter extends AuthenticationFilter {
        @Override
        protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
                return true;
            }
            return false;
        }

        @Override
        protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
            return true;
        }
    }
}
