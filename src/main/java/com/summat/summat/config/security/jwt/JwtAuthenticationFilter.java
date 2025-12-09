package com.summat.summat.config.security.jwt;

import com.summat.summat.users.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.info("[JWT FILTER] URI = " + uri);

        if (uri.startsWith("/auth")
                || uri.equals("/summatUsers/signup")
                || uri.equals("/places/list")) {
            log.info("[JWT FILTER] 화이트리스트 → 그냥 통과");
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        log.info("[JWT FILTER] Authorization = " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.info("[JWT FILTER] token = " + token);

            if (jwtTokenProvider.validateToken(token)) {
                log.info("[JWT FILTER] 토큰 유효 ✅");

                String username = jwtTokenProvider.getUsername(token);
                log.info("[JWT FILTER] username = " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("[JWT FILTER] userDetails = " + userDetails.getUsername());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.info("[JWT FILTER] 토큰 유효하지 않음 ❌");
            }
        } else {
            log.info("[JWT FILTER] Authorization 헤더 없음 또는 Bearer 아님");
        }

        filterChain.doFilter(request, response);
    }

}
