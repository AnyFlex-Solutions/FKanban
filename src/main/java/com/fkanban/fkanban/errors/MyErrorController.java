package com.fkanban.fkanban.errors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// Контроллер для обработки ошибок
@Controller
public class MyErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Получение статуса ошибки из атрибутов запроса
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // Обработка различных кодов ошибок и возврат соответствующих страниц
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errors/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/500";
            } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return "errors/405";
            } else if (statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
                return "errors/415";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "errors/400";
            }
        }

        // Возврат общей страницы ошибки, если код неизвестен
        return "error";
    }
}
