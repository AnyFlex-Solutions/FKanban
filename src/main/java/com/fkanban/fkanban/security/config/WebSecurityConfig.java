package com.fkanban.fkanban.security.config;

import com.fkanban.fkanban.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Используем новый способ отключения CSRF
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/registration/**").permitAll()
                                .requestMatchers("/api/page/registration").permitAll()
                                .requestMatchers("/api/v1/login").permitAll()
                                .requestMatchers("/api/pdf/user_agreement").permitAll()
                                .requestMatchers("/api/pdf/personal_data_processing_policy").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/api/kanban/menu", true)
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/api/logout")
                                .logoutSuccessUrl("/api/page/login?logout")
                                .permitAll()
                );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/resources/**",
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/pdf/**"
        );
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
