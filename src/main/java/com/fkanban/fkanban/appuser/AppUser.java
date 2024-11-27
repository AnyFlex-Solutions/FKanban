package com.fkanban.fkanban.appuser;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import java.util.Collection;
import java.util.Collections;

// Класс, представляющий пользователя приложения
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails {
    // Последовательность для генерации уникальных идентификаторов пользователей
    @SequenceGenerator(
            name = "websiteuser_sequence",
            sequenceName = "websiteuser_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "websiteuser_sequence"
    )
    private Long id;
    private String name; // Имя пользователя
    private String email; // Email пользователя
    private String password; // Пароль пользователя
    // Роль пользователя (USER или ADMIN)
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private Boolean locked = false; // Флаг блокировки аккаунта
    private Boolean enabled = false; // Флаг подтверждения аккаунта

    // Конструктор с параметрами
    public AppUser(String username,
                   String email,
                   String password,
                   AppUserRole appUserRole) {
        this.name = username;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
    }

    // Возвращает права пользователя
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
