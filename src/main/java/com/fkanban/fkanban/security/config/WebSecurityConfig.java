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
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Метод для настройки цепочки фильтров безопасности
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Используем новый способ отключения CSRF
                .cors(cors -> cors.configurationSource(request -> {
                    // Настройка CORS для разрешения междоменных запросов
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*")); // Настройте по вашему усмотрению
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/registration/**").permitAll()
                                .requestMatchers("/api/page/registration").permitAll()
                                .requestMatchers("/api/page/registration/success").permitAll()
                                .requestMatchers("/api/v1/login").permitAll()
                                .requestMatchers("/api/pdf/user_agreement").permitAll()
                                .requestMatchers("/api/pdf/personal_data_processing_policy").permitAll()
                                .requestMatchers("/api/about-us").permitAll()
                                .requestMatchers("/api/kontakts").permitAll()
                                .requestMatchers("/img/logo.jpg").permitAll()
                                .requestMatchers("/img/favicon.ico").permitAll()
                                .requestMatchers("/api/user/forgot-password-with-email").permitAll()
                                .requestMatchers("/metrics").permitAll()
                                .requestMatchers("/metrics/custom").permitAll()
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

    // Метод для настройки исключений из фильтрации безопасности (например, для статичных ресурсов)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/resources/**",
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/pdf/**",
                "/v2/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v2/api-docs/**"
        );
    }

    // Метод для настройки провайдера аутентификации (пользователь и пароль)
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder); // Устанавливаем шифровальщик паролей
        provider.setUserDetailsService(appUserService); // Устанавливаем сервис для получения данных пользователя
        return provider;
    }

    // Метод для настройки менеджера аутентификации
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
