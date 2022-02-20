package com.boottech.springshirojwt.filter;

import com.boottech.springshirojwt.common.JWTAuthToken;
import com.boottech.springshirojwt.common.SecurityConstants;
import com.boottech.springshirojwt.services.TokenManagerService;
import com.boottech.springshirojwt.web.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends AuthenticatingFilter {


    private final TokenManagerService tokenManagerService;

    public AuthenticationFilter(TokenManagerService tokenManagerService) {
        this.tokenManagerService = tokenManagerService;
    }

    /**
     * Check JWT token
     *
     * @param servletRequest request
     * @param servletResponse response
     * @return AuthenticationToken
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be empty");
        }

        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isBlank(jwt) || !jwt.startsWith(SecurityConstants.TOKEN_PREFIXE)) {
            throw new AuthenticationException("JWT Token is not valid");
        }

        String token = jwt.replace(SecurityConstants.TOKEN_PREFIXE, "");
        if (Boolean.TRUE.equals(tokenManagerService.isTokenExpired(token))) {
            throw new AuthenticationException("JWT Token is Expired :" + token);
        }

        return new JWTAuthToken(token);
    }

    /**
     * Access Failure Handling
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // Return to 401
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");

        String error =new ObjectMapper().writeValueAsString(new ErrorResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()),HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Access Denied"));
        httpServletResponse.getWriter().append(error);

        log.error("access Denied url: {}", httpServletRequest.getRequestURI());
        return false;
    }

    /**
     * Determine whether access is allowed
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("Access Allowed url:{}", url);
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



}
