/**
 * Скрипт для обработки формы регистрации, отображения ошибок и управления паролями.
 *
 * @requires jQuery - Для обработки кликов на элементы и управления DOM.
 */
$(document).ready(function() {
    /**
     * Элемент для отображения сообщений об ошибках.
     * @type {HTMLElement}
     */
    const errorMessageDiv = document.getElementById('error-message');

    // Скрытие сообщения об ошибке по умолчанию
    errorMessageDiv.style.display = 'none';
    errorMessageDiv.style.visibility = 'hidden';
    errorMessageDiv.innerHTML = '';

    /**
     * Таймер для автоматического скрытия сообщения об ошибке.
     * @type {number | undefined}
     */
    let errorTimeout;

    /**
     * Обработчик события отправки формы регистрации.
     * @param {Event} event - Событие отправки формы.
     */
    document.getElementById('registrationForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const name = event.target.name.value;
        const email = event.target.email.value;
        const password = event.target.password.value;
        const confPassword = event.target['conf-password'].value;

        // Проверка длины пароля
        if (password.length < 5) {
            displayError('Пароль должен содержать не менее 5 символов.');
            return;
        }

        // Проверка пароля на допустимые символы
        const validPasswordPattern = /^[A-Za-z0-9!@%#&]+$/;
        if (!validPasswordPattern.test(password)) {
            displayError('Пароль может содержать только латинские буквы, цифры и символы !@%#&.');
            return;
        }

        // Проверка совпадения паролей
        if (password !== confPassword) {
            displayError('Пароли не совпадают.');
            return;
        }

        const formData = {
            name: name,
            email: email,
            password: password
        };

        fetch('http://localhost:8090/api/v1/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                // Проверка, если ответ не успешен
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.error || 'Ошибка регистрации');
                    });
                }
                return response.json();
            })
            .then(data => {
                window.location.href = '/api/page/registration/success'; // Перенаправление после успешной регистрации
            })
            .catch(error => {
                displayError(error.message);
            });
    });

    /**
     * Обработчик события клика на элемент для переключения видимости пароля.
     */
    $('.unmask').on('click', function(){
        if($(this).prev('input').attr('type') === 'password')
            $(this).prev('input').prop('type', 'text');
        else
            $(this).prev('input').prop('type', 'password');
        return false;
    });

    /**
     * Функция для отображения сообщения об ошибке.
     *
     * @param {string} message - Текст сообщения об ошибке.
     */
    function displayError(message) {
        // Сброс предыдущего таймера, если он существует
        if (errorTimeout) {
            clearTimeout(errorTimeout);
        }

        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.innerHTML = message;
        errorMessageDiv.style.display = 'block';
        errorMessageDiv.style.visibility = 'visible';

        // Автоматическое скрытие ошибки через 10 секунд
        errorTimeout = setTimeout(() => {
            errorMessageDiv.style.display = 'none';
            errorMessageDiv.style.visibility = 'hidden';
            errorMessageDiv.innerHTML = '';
        }, 10000);
    }
});