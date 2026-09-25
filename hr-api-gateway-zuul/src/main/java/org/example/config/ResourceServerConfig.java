package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    private static final String[] PUBLIC = {
            "/hr-oauth/oauth/token"
    };

    private static final String[] OPERATOR = {
            "/hr-worker/**"
    };

    private static final String[] ADMIN = {
            "/hr-payroll/**",
            "/hr-user/**",
            "/actuator/**",
            "/hr-worker/actuator/**",
            "/hr-oauth/actuator/**"
    };

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeExchange(exchange -> exchange

                        .pathMatchers(PUBLIC)
                        .permitAll()

                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        .pathMatchers(HttpMethod.GET, OPERATOR)
                        .hasAnyRole("OPERATOR", "ADMIN")

                        .pathMatchers(ADMIN)
                        .hasRole("ADMIN")

                        .anyExchange()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt()
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}