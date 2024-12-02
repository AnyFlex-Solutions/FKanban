package com.fkanban.fkanban.email;

// Интерфейс отправки писем
public interface EmailSender {
    void send(String to, String email); // Метод для отправки письма
}
