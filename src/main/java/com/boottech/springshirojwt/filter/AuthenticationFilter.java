package com.boottech.springshirojwt.filter;

import com.boottech.springshirojwt.common.JWTAuthToken;
import com.boottech.springshirojwt.common.SecurityConstants;
import com.boottech.springshirojwt.services.TokenManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthenticationFilter extends AuthenticatingFilter {


    private final TokenManagerService tokenManagerService;

    public AuthenticationFilter(TokenManagerService tokenManagerService) {
        this.tokenManagerService = tokenManagerService;
    }

    /**
     * Wrap JWT Token into Authentication Token
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }

        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isBlank(jwt) || !jwt.startsWith(SecurityConstants.TOKEN_PREFIXE)) {
            throw new AuthenticationException("token not valid");
        }

        String token = jwt.replace(SecurityConstants.TOKEN_PREFIXE, "");
        if (tokenManagerService.isTokenExpired(token)) {
            throw new AuthenticationException("JWT Token Expired,token:" + token);
        }

        String newToken = tokenManagerService.createTokenForUser(token);
        return new JWTAuthToken(newToken);
    }

    /**
     * Access Failure Handling
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // Return to 401
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Set the response code to 401 or output the message directly
        String url = httpServletRequest.getRequestURI();
        log.error("onAccessDenied url: {}", url);
       // ApiResult apiResult = ApiResult.fail(ApiCode.UNAUTHORIZED);
       // HttpServletResponseUtil.printJSON(httpServletResponse, apiResult);
        return false;
    }

    /**
     * Determine whether access is allowed
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("isAccessAllowed url:{}", url);
        if (this.isLoginRequest(request, response)) {
            return true;
        }
        boolean allowed = false;
        try {
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) { //not found any token
            log.error("Token Can not be empty", e);
        } catch (Exception e) {
            log.error("Access error", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * Landing Success Processing
     *
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("Authentication success,token:{},url:{}", token, url);
        // Refresh token
        JWTAuthToken jwtToken = (JWTAuthToken) token;
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        //loginService.refreshToken(jwtToken, httpServletResponse);
        return true;
    }

    /**
     * Landing Failure Handling
     *
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.error("The landing failed. token:" + token + ",error:" + e.getMessage(), e);
        return false;
    }

}
