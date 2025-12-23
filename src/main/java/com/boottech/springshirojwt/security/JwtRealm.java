package com.boottech.springshirojwt.security;

import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.services.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Apache Shiro {@link AuthorizingRealm} implementation that supports both
 * username/password authentication and JWT-based stateless authentication.
 *
 * <p>
 * This realm:
 * <ul>
 *     <li>Authenticates users using {@link UsernamePasswordToken}</li>
 *     <li>Validates and authenticates JWTs using {@link JwtToken}</li>
 *     <li>Loads user roles and permissions from {@link UserService}</li>
 *     <li>Disables authorization caching to support stateless JWT usage</li>
 * </ul>
 * </p>
 */
@Component
public class JwtRealm extends AuthorizingRealm {

    private final UserService userService;

    /**
     * Creates a new {@code JwtRealm} with the required services.
     *
     * @param userService     service used to load user details, roles, and permissions
     * @param passwordService service used to verify user passwords
     */
    public JwtRealm(UserService userService, PasswordService passwordService) {
        this.userService = userService;

        PasswordMatcher matcher = new PasswordMatcher();
        matcher.setPasswordService(passwordService);
        this.setCredentialsMatcher(matcher);

        // Disable authorization cache for stateless JWT authentication
        this.setAuthorizationCachingEnabled(false);
    }

    /**
     * Determines whether this realm supports the given authentication token.
     *
     * @param token the authentication token
     * @return {@code true} if the token is a {@link JwtToken} or {@link UsernamePasswordToken}
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken || token instanceof UsernamePasswordToken;
    }

    /**
     * Performs authentication based on the provided authentication token.
     *
     * <p>
     * Supports:
     * <ul>
     *     <li>{@link UsernamePasswordToken} for standard login</li>
     *     <li>{@link JwtToken} for JWT-based authentication</li>
     * </ul>
     * </p>
     *
     * @param authToken the authentication token
     * @return authentication info containing the authenticated principal
     * @throws AuthenticationException if authentication fails
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken)
            throws AuthenticationException {

        if (authToken instanceof UsernamePasswordToken upToken) {
            String username = upToken.getUsername();

            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            if (!user.isEnabled()) {
                throw new DisabledAccountException();
            }

            return new SimpleAuthenticationInfo(
                    user.getUsername(),
                    user.getPassword(),
                    getName()
            );
        }

        if (authToken instanceof JwtToken jwtToken) {
            String jwt = (String) jwtToken.getCredentials();

            if (!JwtUtil.validate(jwt)) {
                throw new AuthenticationException("Invalid JWT");
            }

            String username = JwtUtil.getUsername(jwt);

            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new AuthenticationException("Invalid token"));

            // Password is not required for JWT-based authentication
            return new SimpleAuthenticationInfo(user.getUsername(), null, getName());
        }

        throw new AuthenticationException("Unsupported token type");
    }

    /**
     * Asserts credentials match for the given authentication token.
     *
     * <p>
     * Credential matching is skipped for {@link JwtToken} since JWTs
     * are already validated cryptographically.
     * </p>
     *
     * @param token the authentication token
     * @param info  the authentication info
     * @throws AuthenticationException if credentials do not match
     */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
            throws AuthenticationException {

        if (token instanceof JwtToken) {
            // Skip credentials check for JWT
            return;
        }
        super.assertCredentialsMatch(token, info);
    }

    /**
     * Retrieves authorization info for the given principals.
     *
     * <p>
     * Authorization caching is bypassed to ensure up-to-date role and
     * permission resolution for stateless JWT usage.
     * </p>
     *
     * @param principals the authenticated principals
     * @return authorization info containing roles and permissions
     */
    @Override
    protected AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        return doGetAuthorizationInfo(principals);
    }

    /**
     * Loads roles and permissions for the authenticated user.
     *
     * @param principals the authenticated principals
     * @return populated {@link AuthorizationInfo}
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();

        User user = userService.getUserByUsername(username)
                .orElseThrow();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        user.getRoles().forEach(role -> {
            info.addRole(role.getCode());
            Arrays.stream(role.getPermissions().split(","))
                    .forEach(info::addStringPermission);
        });

        return info;
    }
}


