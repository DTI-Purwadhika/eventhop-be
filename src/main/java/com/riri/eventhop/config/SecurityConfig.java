    package com.riri.eventhop.config;

    import com.nimbusds.jose.jwk.JWK;
    import com.nimbusds.jose.jwk.JWKSet;
    import com.nimbusds.jose.jwk.RSAKey;
    import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
    import com.nimbusds.jose.jwk.source.JWKSource;
    import com.nimbusds.jose.proc.SecurityContext;
    import com.riri.eventhop.feature2.users.UserDetailsServiceImpl;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.java.Log;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.ProviderManager;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.oauth2.jwt.JwtDecoder;
    import org.springframework.security.oauth2.jwt.JwtEncoder;
    import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
    import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
    import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
    import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import java.util.Arrays;
    import java.util.List;

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    @RequiredArgsConstructor
    @Log
    public class SecurityConfig {
        private final UserDetailsServiceImpl userDetailsService;
        private final RsaKeyConfigProperties rsaKeyConfigProperties;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/api/v1/events/**").permitAll();
                        auth.requestMatchers("/api/v1/categories/**").permitAll();
                        auth.requestMatchers("/api/v1/auth/**").permitAll();
                        auth.requestMatchers("/api/v1/dashboard/**").hasRole("ORGANIZER");
                        auth.anyRequest().authenticated();
                    })
                    .oauth2Login(oauth2 -> oauth2
                            .authorizationEndpoint(authorization -> authorization
                                    .baseUri("/api/v1/auth/oauth2/authorization"))
                            .redirectionEndpoint(redirection -> redirection
                                    .baseUri("/api/v1/auth/oauth2/callback/*"))
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt
                                    .decoder(jwtDecoder())
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                            )
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Add your front-end URL
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Bean
        public JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withPublicKey(rsaKeyConfigProperties.publicKey()).build();
        }

        @Bean
        JwtEncoder jwtEncoder() {
            JWK jwk = new RSAKey.Builder(rsaKeyConfigProperties.publicKey()).privateKey(rsaKeyConfigProperties.privateKey()).build();
            JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
            return new NimbusJwtEncoder(jwks);
        }
//        @Bean
//        public JwtDecoder jwtDecoder() {
//            return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
//        }
//
//        private SecretKey getSecretKey() {
//            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
//            return Keys.hmacShaKeyFor(keyBytes);
//        }
        @Bean
        public AuthenticationManager authManager() {
            var authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());
            return new ProviderManager(authProvider);
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            grantedAuthoritiesConverter.setAuthoritiesClaimName("user_role");
            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
            return jwtAuthenticationConverter;
        }
    }
