package com.fkanban.fkanban.appuser;

import lombok.*;

// DTO для запроса на восстановление пароля
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForgotPasswordRequest {
    private String email; // Email пользователя
}
