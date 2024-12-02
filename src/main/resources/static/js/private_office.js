/**
 * Скрипт для работы с формами и модальными окнами на странице.
 *
 * Функционал:
 * - Отправка запроса на сброс пароля.
 * - Сохранение данных пользователя с отправкой формы.
 * - Отображение сообщений об успехе и ошибках.
 * - Управление модальным окном для вывода сообщений.
 * - Переключение видимости пароля.
 */
$(document).ready(function() {
    /**
     * Обработчик кнопки "Забыли пароль".
     * Отправляет запрос на сброс пароля и отображает результат в модальном окне.
     */
    document.getElementById('forgotPasswordBtn').addEventListener('click', function () {
        fetch(`/api/user/forgot-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (response.ok) {
                showModal('Успех', 'Новый пароль отправлен на Вашу почту.', 'success');
            } else {
                showModal('Ошибка', 'Ошибка при отправке нового пароля.', 'error');
            }
        });
    });

    // Элемент для отображения ошибок
    const errorMessageDiv = document.getElementById('error-message');

    errorMessageDiv.style.display = 'none';
    errorMessageDiv.style.visibility = 'hidden';
    errorMessageDiv.innerHTML = '';

    let errorTimeout;

    /**
     * Обработчик кнопки "Сохранить".
     * Отправляет данные формы для обновления профиля пользователя и обрабатывает ответ.
     * @param {Event} event Событие отправки формы.
     */
    document.getElementById('save').addEventListener('click', function (event) {
        event.preventDefault(); // Останавливаем отправку формы

        const formData = {
            nickname: document.getElementById('nickname').value,
            Password: document.getElementById('password').value,
            SuccessPassword: document.getElementById('conf-password').value
        };

        fetch('/api/user/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams(formData)
        })
            .then(response => {
                return response.text().then(data => ({status: response.status, message: data}));
            })
            .then(({status, message}) => {
                if (status === 200) {
                    showSuccess(message);
                } else {
                    displayError(message);
                }
            })
            .catch(error => {
                displayError('Произошла ошибка при обновлении данных.');
                console.error('Ошибка:', error);
            });
    });

    /**
     * Отображает сообщение об успешной операции.
     * @param {string} message Текст сообщения.
     */
    function showSuccess(message) {
        // Сброс предыдущего таймера, если он существует
        if (errorTimeout) {
            clearTimeout(errorTimeout);
        }

        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.innerHTML = message;
        errorMessageDiv.style.display = 'block';
        errorMessageDiv.style.visibility = 'visible';

        errorMessageDiv.classList.replace('alert-danger', 'alert-success');

        // Автоматическое скрытие ошибки через 10 секунд
        errorTimeout = setTimeout(() => {
            errorMessageDiv.style.display = 'none';
            errorMessageDiv.style.visibility = 'hidden';
            errorMessageDiv.innerHTML = '';
        }, 10000);
    }

    /**
     * Отображает сообщение об ошибке.
     * @param {string} message Текст сообщения.
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

        errorMessageDiv.classList.replace('alert-success', 'alert-danger');

        // Автоматическое скрытие ошибки через 10 секунд
        errorTimeout = setTimeout(() => {
            errorMessageDiv.style.display = 'none';
            errorMessageDiv.style.visibility = 'hidden';
            errorMessageDiv.innerHTML = '';
        }, 10000);
    }

    /**
     * Отображает модальное окно с сообщением.
     * @param {string} title Заголовок модального окна.
     * @param {string} message Текст сообщения.
     * @param {string} type Тип сообщения: 'success' или 'error'.
     */
    function showModal(title, message, type) {
        const modalElement = document.getElementById('messageModal');
        const modalLabel = document.getElementById('messageModalLabel');
        const modalMessage = document.getElementById('modalMessage');
        const modalButton = document.getElementById('modal-button');

        // Очистка классов для модального окна
        modalElement.classList.remove('modal-success', 'modal-error');

        if (type === 'success') {
            modalElement.classList.add('modal-success'); // Добавление класса для успеха
            try {
                modalButton.classList.replace('btn-primary', 'btn-outline-success')
                modalButton.classList.replace('btn-outline-danger', 'btn-outline-success')
            } catch (error) {
                console.log(error);
            }
        } else if (type === 'error') {
            modalElement.classList.add('modal-error'); // Добавление класса для ошибки
            try {
                modalButton.classList.replace('btn-primary', 'btn-outline-danger')
                modalButton.classList.replace('btn-outline-success', 'btn-outline-danger')
            } catch (error) {
                console.log(error);
            }
        }

        modalLabel.textContent = title;
        modalMessage.textContent = message;
        $('#messageModal').modal('show');
    }

    /**
     * Обработчик для показа/скрытия пароля.
     */
    $('.unmask').on('click', function(){
        if($(this).prev('input').attr('type') === 'password')
            $(this).prev('input').prop('type', 'text');
        else
            $(this).prev('input').prop('type', 'password');
        return false;
    });
});