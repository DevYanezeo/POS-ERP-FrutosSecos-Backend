package com.erp.p03.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.Customizer;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(req ->
            req.requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR", "CAJERO")
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/*/agregar-stock", "/api/productos/*/quitar-stock").hasAnyRole("ADMIN", "VENDEDOR", "CAJERO")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN")

                .requestMatchers("/api/categorias/**").hasAnyRole("ADMIN", "VENDEDOR","CAJERO")

                .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "VENDEDOR", "CAJERO")
                .requestMatchers("/api/detalle-ventas/**").hasAnyRole("ADMIN", "VENDEDOR", "CAJERO")

                .requestMatchers("/api/movimientos-stock/**").hasAnyRole("ADMIN", "VENDEDOR")

                // Reglas para endpoints de feriados: lectura permitida a roles comunes,
                // pero crear/editar/eliminar sólo para ADMIN
                .requestMatchers(HttpMethod.GET, "/api/feriados/**").hasAnyRole("ADMIN", "VENDEDOR", "CAJERO")
                .requestMatchers(HttpMethod.POST, "/api/feriados/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/feriados/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/feriados/**").hasAnyRole("ADMIN")

                .anyRequest().authenticated()
        )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001",
        "http://34.44.223.145:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}