package ru.clevertec.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.GET, "/users/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/news").permitAll()
                        .requestMatchers(HttpMethod.GET, "/news/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/news/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/news/*/comments/*").hasAnyAuthority("ADMIN", "SUBSCRIBER")
                        .requestMatchers(HttpMethod.POST, "/news").hasAnyAuthority("ADMIN", "JOURNALIST")
                        .requestMatchers(HttpMethod.PUT, "/news/*").hasAnyAuthority("ADMIN", "JOURNALIST")
                        .requestMatchers(HttpMethod.DELETE, "/news/*").hasAnyAuthority("ADMIN", "JOURNALIST")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
