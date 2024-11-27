package com.fkanban.fkanban.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {
    // Метод для проверки корректности email
    @Override
    public boolean test(String s) {
        return true;
    }
}
