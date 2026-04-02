package com.summat.summat.config.security;

import com.summat.summat.config.security.jwt.JwtAuthenticationFilter;
import com.summat.summat.users.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // 밑에 CorsFilter bean 사용
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/summatUsers/id-check").permitAll()
                        .requestMatchers(HttpMethod.POST, "/summatUsers/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/summatUsers/pw-check").permitAll()
                        .requestMatchers(HttpMethod.GET, "/summatUsers/nick-name-check").permitAll()
                        .requestMatchers(HttpMethod.GET, "/places/list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/places/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/places/detail/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/places/view/**").permitAll()
                        // 댓글 목록은 비로그인도 조회 가능
                        .requestMatchers(HttpMethod.GET, "/reply/**").permitAll()
                        // 관리자 전용
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 정적 리소스
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        // .requestMatchers("/", "/index.html", "/static/**").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                .userDetailsService(customUserDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("[SECURITY] 401 발생 URI = {}", request.getRequestURI());
                            response.setStatus(UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"status\":401,\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"status\":403,\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\"}"
                            );
                        })
                );

        return http.build();
    }

    // CORS 설정: React localhost:3000 허용 예시
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000", "http://127.0.0.1:3000",
                "http://localhost:5173", "http://127.0.0.1:5173",
                "https://summat.site",          // TODO: 실제 운영 도메인으로 교체
                "https://www.summat.site"        // TODO: www 서브도메인 필요 시 유지
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setExposedHeaders(List.of("Authorization"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * JwtAuthenticationFilter가 @Component이기 때문에 Spring Boot가 서블릿 필터로 자동 등록함.
     * 그러면 Spring Security 필터 체인 밖에서 선실행 → SecurityContextHolderFilter가 컨텍스트를 리셋할 때 인증 소실.
     * setEnabled(false)로 서블릿 자동 등록을 막고 Security 체인 내에서만 동작하도록 한다.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationFilter> bean = new FilterRegistrationBean<>(jwtAuthenticationFilter);
        bean.setEnabled(false);
        return bean;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
