package com.fkanban.fkanban.appuser;

import com.fkanban.fkanban.registration.token.ConfirmationToken;
import com.fkanban.fkanban.registration.token.ConfirmationTokenService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// Сервис для работы с пользователями
@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final MeterRegistry meterRegistry; // Метрики для мониторинга

    private final static String USER_NOT_FOUND_MSG = "Пользователь с почтой %s не найден.";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    // Загрузка пользователя по email для аутентификации
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    // Регистрация нового пользователя
    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();

        if (userExists) {
            throw new IllegalStateException("Почта уже используется");
        }

        // Хэширование пароля
        String encodedPassword = bCryptPasswordEncoder.encode((appUser.getPassword()));

        appUser.setPassword((encodedPassword));

        appUserRepository.save(appUser);

        // Генерация токена подтверждения
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationtoken = new ConfirmationToken(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser);

        confirmationTokenService.saveConfirmationToken(confirmationtoken);

        return token;
    }

    // Включение учетной записи пользователя
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    // Проверка корректности пароля
    private void validatePassword(String password) {
        // Минимальная длина пароля
        if (password.length() < 5) {
            throw new IllegalStateException("Пароль должен состоять не менее чем из 5 символов.");
        }

        // Регулярное выражение для проверки допустимых символов
        String passwordPattern = "^[A-Za-z0-9!@%#&]+$";
        if (!password.matches(passwordPattern)) {
            throw new IllegalStateException("Пароль может содержать только латинские буквы, цифры и символы !@%#&");
        }
    }

    // Обновление данных пользователя
    public void updateUser(UpdateUserRequest request) {
        meterRegistry.counter("appuser.update.attempts").increment();
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

            Optional.ofNullable(request.getNickname())
                    .filter(nickname -> !nickname.isEmpty())
                    .ifPresent(user::setName);

            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                if (request.getSuccessPassword() != null && !request.getSuccessPassword().isEmpty()) {
                    if (request.getPassword().equals(request.getSuccessPassword())) {
                        validatePassword(request.getSuccessPassword());

                        user.setPassword(bCryptPasswordEncoder.encode(request.getSuccessPassword()));
                    } else {
                        throw new IllegalStateException("Пароли не совпадают.");
                    }
                } else {
                    throw new IllegalStateException("Повторите ввод пароля для проверки коректности.");
                }
            }

            appUserRepository.save(user);

            meterRegistry.counter("appuser.update.success").increment();
        } catch (IllegalStateException e) {
            meterRegistry.counter("appuser.update.failures").increment();
            throw e;
        }
    }

    // Поиск пользователя по email
    public AppUser findUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " not found"));
    }

    // Поиск пользователя по ID
    public AppUser findById(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with ID " + userId + " not found"));
    }

    // Проверка существования пользователя по email
    public Boolean checkUserByEmail(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }
}
