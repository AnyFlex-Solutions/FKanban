package com.fkanban.fkanban.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private final String name; // Имя пользователя
    private final String email; // Email пользователя
    private final String password; // Пароль пользователя
}
