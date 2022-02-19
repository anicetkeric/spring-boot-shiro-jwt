package com.boottech.springshirojwt.config;

import com.boottech.springshirojwt.common.UserRealm;
import com.boottech.springshirojwt.filter.AuthenticationFilter;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {


    @Bean
    DefaultPasswordService pwdService(){
        return new DefaultPasswordService();
    }

    @Bean("userRealm")
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        PasswordMatcher pwdMatcher = new PasswordMatcher();
        pwdMatcher.setPasswordService(pwdService());
        userRealm.setCredentialsMatcher(pwdMatcher);
        return userRealm;
    }


    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager")
                                                                     DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        Map<String, Filter> filterMap = new HashMap<>();
        // Add custom filter
        filterMap.put("api", new AuthenticationFilter(null));
        factoryBean.setFilters(filterMap);

        // Config security manager
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setUnauthorizedUrl("/401");


        /*
         * 自定义url规则
         * http://shiro.apache.org/web.html#urls-
         */
        Map<String, String> filterRuleMap = new HashMap<>();
        filterRuleMap.put("/api/user/sign-up","anon");
        // 所有请求通过我们自己的JWT Filter
        filterRuleMap.put("/**", "api");
        // 访问401和404页面不通过我们的Filter
        filterRuleMap.put("/401", "anon");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }


    /**
     * 注入 securityManager
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        return securityManager;
    }
}
