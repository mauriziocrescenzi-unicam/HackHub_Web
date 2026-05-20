/*
 * Copyright © 2025 Victoria Coacci, Daniel Duda, Fattori Filippo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package it.unicam.cs.hackhub.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration class responsible for defining authentication and authorization rules.
 *
 * <p>
 * Configures JWT-based stateless authentication, password encoding, security filters,
 * and access control policies for application endpoints.
 * </p>
 *
 * <p>
 * This class defines which endpoints are publicly accessible and which require authentication,
 * and integrates the {@link AuthTokenFilter} to validate JWT tokens for protected resources.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * Constructs the security configuration with the specified authentication entry point.
     *
     * <p>
     * The provided {@link AuthEntryPointJwt} is used to handle unauthorized access attempts
     * by returning an appropriate HTTP 401 Unauthorized response when authentication fails.
     * </p>
     *
     * @param unauthorizedHandler the authentication entry point used to handle unauthorized requests
     */
    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    /**
     * Creates and returns the JWT authentication filter bean.
     *
     * <p>
     * This filter is responsible for intercepting incoming requests, validating JWT tokens,
     * and setting the authentication in the security context.
     * </p>
     *
     * @return the JWT authentication filter instance
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    /**
     * Creates and returns the password encoder used for hashing user passwords.
     *
     * <p>
     * Uses the BCrypt hashing algorithm to securely store and verify passwords.
     * </p>
     *
     * @return the password encoder instance
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Configures the security filter chain and defines authentication and authorization rules.
     *
     * <p>
     * Disables CSRF and CORS protection for stateless JWT authentication, configures
     * exception handling, session management, endpoint access permissions, and registers
     * the JWT authentication filter.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure security settings
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/h2-console/**",
                                        "/api/auth/**",
                                        "/api/test/all"
                                ).permitAll()
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/hackathons",
                                        "/api/hackathons/*"
                                ).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/teams")
                                .permitAll()
                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                .anyRequest().authenticated()
                );

        http.addFilterBefore(
                authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}
