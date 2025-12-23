package org.example.studyassistant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启跨域支持 (CORS)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 关闭 CSRF 攻击防御
                .csrf(csrf -> csrf.disable())

                // 3. 配置请求权限
                .authorizeHttpRequests(auth -> auth
                        // 允许 登录 和 注册 接口匿名访问 (无需 Token 即可访问)
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll()

                        // 允许所有的 OPTIONS 请求 (浏览器预检请求，解决 CORS 的关键)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 为了开发方便，暂时允许所有请求 (正式上线建议改为 .authenticated())
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * 配置全局跨域设置
     * 这会让 Spring Security 在拦截请求之前，先加上 CORS 头
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许前端带上凭证 (如 Cookie，虽然我们用 Token 但开启无妨)
        config.setAllowCredentials(true);

        // 允许所有域名访问 (Spring Security 6+ 建议用 addAllowedOriginPattern)
        config.addAllowedOriginPattern("*");

        // 允许所有的请求头 (Header)
        config.addAllowedHeader("*");

        // 允许所有的请求方法 (GET, POST, PUT, DELETE...)
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 将这个配置应用到所有路径 /**
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}