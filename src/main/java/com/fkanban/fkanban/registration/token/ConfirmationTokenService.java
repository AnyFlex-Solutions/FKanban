package com.fkanban.fkanban.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    // Репозиторий для работы с токенами подтверждения
    private final ConfirmationTokenRepository confirmationTokenRepository;

    // Метод для сохранения нового токена подтверждения
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    // Метод для получения токена по его значению
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    // Метод для обновления времени подтверждения токена
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
