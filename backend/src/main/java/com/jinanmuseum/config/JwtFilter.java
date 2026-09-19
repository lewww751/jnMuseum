package com.jinanmuseum.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 后台鉴权过滤器。不做全局注册，由 FilterConfig 注册到 /api/admin/*；
 * 放行 /api/admin/login。
 */
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.startsWith("/api/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }
        }

        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.getWriter().write("{\"code\":401,\"message\":\"未登录或token无效\",\"data\":null}");
    }
}
