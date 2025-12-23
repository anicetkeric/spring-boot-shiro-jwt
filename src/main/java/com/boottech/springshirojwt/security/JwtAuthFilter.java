package com.boottech.springshirojwt.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Servlet filter responsible for authenticating requests using JWT.
 *
 * <p>
 * This filter:
 * <ul>
 *     <li>Intercepts every incoming HTTP request</li>
 *     <li>Skips authentication for public authentication endpoints</li>
 *     <li>Extracts and validates JWTs from the {@code Authorization} header</li>
 *     <li>Delegates authentication to Apache Shiro using {@link JwtToken}</li>
 * </ul>
 * </p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Performs JWT-based authentication for each HTTP request.
     *
     * <p>
     * Expected header format:
     * <pre>{@code
     * Authorization: Bearer <jwt-token>
     * }</pre>
     * </p>
     *
     * <p>
     * Requests to paths starting with {@code /auth/} are treated as public
     * and bypass authentication.
     * </p>
     *
     * @param req   the HTTP servlet request
     * @param res   the HTTP servlet response
     * @param chain the filter chain
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        if (path.startsWith("/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = header.substring(7);
            Subject subject = SecurityUtils.getSubject();
            subject.login(new JwtToken(token));
            chain.doFilter(req, res);
        } catch (AuthenticationException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
