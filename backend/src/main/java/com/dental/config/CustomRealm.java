package com.dental.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

/**
 * 自定义Realm实现，用于认证和授权
 * 这是一个简化版本，实际项目中会与数据库集成
 */
@Component
public class CustomRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 开发环境下，给所有用户admin角色和所有权限
        authorizationInfo.addRole("admin");
        authorizationInfo.addStringPermission("*");
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 开发环境下，任何用户名密码都可以通过
        String username = (String) token.getPrincipal();
        // 使用简单的身份验证信息
        return new SimpleAuthenticationInfo(username, token.getCredentials(), getName());
    }
}
