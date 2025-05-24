package com.dbs.movieservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // JWT 인증이 필요 없는 URL 패턴 목록
    private final List<String> excludeUrlPatterns = Arrays.asList(
            "/api/auth/**",
            "/api/movies",
            "/api/movies/*/details",
            "/test"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 제외할 URL 패턴 확인
        String path = request.getRequestURI();
        return excludeUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = jwtUtils.resolveToken(request);
        
        if (token != null && jwtUtils.validateToken(token)) {
            Authentication auth = jwtUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Set Authentication to security context for '{}', uri: {}", auth.getName(), request.getRequestURI());
        } else {
            log.debug("No valid JWT token found, uri: {}", request.getRequestURI());
        }
        
        filterChain.doFilter(request, response);
    }
} 