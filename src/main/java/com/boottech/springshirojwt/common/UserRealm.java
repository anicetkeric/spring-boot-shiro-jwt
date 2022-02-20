package com.boottech.springshirojwt.common;

import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.services.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userservice;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();

       Optional<User> user = userservice.getUserByUsername(username);

        if(user.isEmpty()) {
            throw new UnknownAccountException("Unknown Account");
        }

        if(!user.get().isEnabled()) {
            throw new LockedAccountException("Blocked account");
        }

        return new SimpleAuthenticationInfo(user.get().getUsername(), user.get().getPassword(),getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }
}
