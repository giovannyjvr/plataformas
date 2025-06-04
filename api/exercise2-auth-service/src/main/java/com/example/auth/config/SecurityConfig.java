package com.example.auth.config;

import com.example.auth.service.UserDetailsServiceImpl;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                // 1) libera todo tráfego para autenticação (login) normal
                .antMatchers("/api/auth/**").permitAll()
                // 2) libera o endpoint de métricas do Prometheus
                .antMatchers("/actuator/prometheus").permitAll()
                // 3) libera health e info sem autenticação (opcional, mas recomendado)
                .antMatchers("/actuator/health", "/actuator/info").permitAll()
                // 4) TODO: se você quiser liberar mais actuator endpoints, coloque aqui
                //    .antMatchers("/actuator/env", "/actuator/loggers").permitAll()
                // 5) tudo o resto: requer autenticação via JWT
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtFilter(userDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

}
