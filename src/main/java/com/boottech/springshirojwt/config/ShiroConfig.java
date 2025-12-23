package com.boottech.springshirojwt.config;


import com.boottech.springshirojwt.security.JwtRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for Apache Shiro.
 *
 * <p>
 * This configuration sets up:
 * <ul>
 *     <li>Stateless security using JWT</li>
 *     <li>A custom {@link JwtRealm}</li>
 *     <li>Password hashing via {@link PasswordService}</li>
 *     <li>Method-level authorization annotations</li>
 * </ul>
 * </p>
 */
@Configuration
public class ShiroConfig {

    /**
     * Provides the {@link PasswordService} used by Shiro to hash and
     * verify user passwords.
     *
     * @return a default password service implementation
     */
    @Bean
    public PasswordService passwordService() {
        return new DefaultPasswordService();
    }

    /**
     * Enables Shiro security annotations such as {@code @RequiresRoles}
     * and {@code @RequiresPermissions}.
     *
     * @param securityManager the Shiro security manager
     * @return the authorization attribute source advisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            SecurityManager securityManager) {

        AuthorizationAttributeSourceAdvisor advisor =
                new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * Configures the Shiro {@link SecurityManager}.
     *
     * <p>
     * This setup:
     * <ul>
     *     <li>Uses {@link JwtRealm} for authentication and authorization</li>
     *     <li>Disables session creation to support stateless JWT authentication</li>
     *     <li>Disables session cookies and validation</li>
     * </ul>
     * </p>
     *
     * @param realm the custom JWT-enabled realm
     * @return the configured security manager
     */
    @Bean
    public SecurityManager securityManager(JwtRealm realm) {
        DefaultSecurityManager sm = new DefaultSecurityManager();
        sm.setRealm(realm);

        // Disable session creation for stateless JWT authentication
        sm.setSubjectFactory(new DefaultWebSubjectFactory() {
            @Override
            public Subject createSubject(SubjectContext ctx) {
                ctx.setSessionCreationEnabled(false);
                return super.createSubject(ctx);
            }
        });

        // Disable web session management
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookieEnabled(false);
        sessionManager.setSessionValidationSchedulerEnabled(false);
        sm.setSessionManager(sessionManager);

        // Make SecurityManager globally accessible
        SecurityUtils.setSecurityManager(sm);
        return sm;
    }
}
