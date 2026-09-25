package org.hruser.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import org.springframework.security.oauth2.jwt.JwtDecoder;

import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration
        .OAuth2AuthorizationServerConfiguration;

import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;


@Configuration
public class AuthorizationServerConfig {

    @Value("${oauth.client.name}")
    private String clientName;

    @Value("${oauth.client.secret}")
    private String clientSecret;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .oauth2ResourceServer(authorizationServer -> {

                    http.securityMatcher(
                            String.valueOf(authorizationServer.disable())
                    );

                    authorizationServer
                            .jwt(Customizer.withDefaults());
                })

                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest()
                                .authenticated()
                )

                .exceptionHandling(exceptions ->
                        exceptions.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(
                                        MediaType.TEXT_HTML
                                )
                        )
                );

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient registeredClient =
                RegisteredClient
                        .withId(UUID.randomUUID().toString())

                        .clientId(clientName)

                        .clientSecret("{noop}" + clientSecret)

                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                        )

                        .authorizationGrantType(
                                AuthorizationGrantType.CLIENT_CREDENTIALS
                        )

                        .scope("read")
                        .scope("write")

                        .clientSettings(
                                ClientSettings.builder()
                                        .requireAuthorizationConsent(false)
                                        .build()
                        )

                        .build();

        return new InMemoryRegisteredClientRepository(
                registeredClient
        );
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource() {

        RSAKey rsaKey = generateRsa();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }


    @Bean
    public JwtDecoder jwtDecoder(
            JWKSource<SecurityContext> jwkSource) {

        return OAuth2AuthorizationServerConfiguration
                .jwtDecoder(jwkSource);
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {

        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }


    private static RSAKey generateRsa() {

        try {

            KeyPairGenerator keyPairGenerator =
                    KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048);

            KeyPair keyPair =
                    keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey =
                    (RSAPublicKey) keyPair.getPublic();

            RSAPrivateKey privateKey =
                    (RSAPrivateKey) keyPair.getPrivate();

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Erro ao gerar chave RSA",
                    e
            );
        }
    }
}