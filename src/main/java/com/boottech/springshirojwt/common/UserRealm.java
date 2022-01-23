package com.boottech.springshirojwt.common;

import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.services.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserService userservice;

    @Autowired
    DefaultPasswordService passwordservice;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTAuthToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.setRoles(new HashSet<>(Arrays.asList("ADMIN", "SUPER")));
        authorizationInfo.setStringPermissions(new HashSet<>(Arrays.asList("user:read", "user:create")));

/*        authorizationInfo.setRoles(userservice.findRoles(username));
        System.out.println(userservice.findRoles(username));
        authorizationInfo.setStringPermissions(userservice.findPermissions(username));
        System.out.println(userservice.findPermissions(username));*/

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();

       // Optional<User> user = userservice.getUserByUsername(username);

        String pwd = "1234567";
     //   String pwd = passwordservice.encryptPassword("1234567");
        Optional<User> user =  Optional.of(User.builder().username("aek").password(pwd).enabled(true).build());

        if(user.isEmpty()) {
            throw new UnknownAccountException();//没找到帐号
        }

        if(!user.get().isEnabled()) {
            throw new LockedAccountException();
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        return new SimpleAuthenticationInfo(
                user.get().getUsername(), //用户名
                user.get().getPassword(),
                getName()  //realm name
        );
    }
}
