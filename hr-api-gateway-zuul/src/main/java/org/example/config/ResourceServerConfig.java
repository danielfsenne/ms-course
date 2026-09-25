package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

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

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange

                        .pathMatchers(PUBLIC)
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
}