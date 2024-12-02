/**
 * Скрипт управления функционалом страницы восстановления пароля.
 *
 * Функционал:
 * 1. Скрытие/показ пароля при клике на иконку.
 * 2. Отправка запроса на восстановление пароля по введённому email.
 * 3. Отображение модальных окон с результатом операций.
 */
$(document).ready(function() {
    /**
     * Блок сообщения об ошибке.
     * @type {HTMLDivElement}
     */
    const errorMessageDiv = document.getElementById('error-message');

    // Инициализация состояния сообщения об ошибке
    errorMessageDiv.style.display = 'none';
    errorMessageDiv.style.visibility = 'hidden';
    errorMessageDiv.innerHTML = '';

    /**
     * Таймаут для автоматического скрытия сообщений об ошибке.
     * @type {number|undefined}
     */
    let errorTimeout;

    /**
     * Обработчик клика для отображения/скрытия пароля.
     */
    $('.unmask').on('click', function(){
        if($(this).prev('input').attr('type') === 'password')
            $(this).prev('input').prop('type', 'text');
        else
            $(this).prev('input').prop('type', 'password');
        return false;
    });

    /**
     * Обработчик клика по кнопке восстановления пароля.
     */
    document.getElementById('forgotPasswordBtn').addEventListener('click', function () {
        /**
         * Email пользователя для восстановления пароля.
         * @type {string}
         */
        const email = document.getElementById('username').value;
        if (email) {
            // Отправка POST-запроса на сервер для восстановления пароля
            fetch(`/api/user/forgot-password-with-email?email=${email}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
            }).then(response => {
                /**
                 * Тип содержимого ответа сервера.
                 * @type {string|null}
                 */
                const contentType = response.headers.get('Content-Type');
                if (!response.ok) {
                    if (contentType && contentType.includes('application/json')) {
                        return response.json().then(data => {
                            throw new Error(data.error || 'Ошибка при отправке нового пароля');
                        });
                    } else {
                        return response.text().then(text => {
                            throw new Error(`Ошибка при отправке нового пароля: ${text}`);
                        });
                    }
                }

                if (contentType && contentType.includes('application/json')) {
                    return response.json();
                } else {
                    return response.text();
                }
            })
                .then(data => {
                    if (data === 'Новый пароль отправлен на Вашу почту.') {
                        showModal('Успех', 'Новый пароль отправлен на Вашу почту.', 'success');
                    } else {
                        showModal('Ошибка', 'Ошибка отправки нового пароля на почту. Проверьте её корректность. Данные: ' + data, 'error');
                    }
                })
                .catch(error => {
                    showModal('Ошибка', error.message, 'error');
                });

        } else {
            showModal('Ошибка', 'Пожалуйста, введите Вашу почту.', 'error');
        }
    });

    /**
     * Функция отображения модального окна.
     * @param {string} title - Заголовок модального окна.
     * @param {string} message - Сообщение модального окна.
     * @param {'success'|'error'} type - Тип сообщения (успех или ошибка).
     */
    function showModal(title, message, type) {
        /**
         * Модальное окно.
         * @type {HTMLElement}
         */
        const modalElement = document.getElementById('messageModal');

        /**
         * Заголовок модального окна.
         * @type {HTMLElement}
         */
        const modalLabel = document.getElementById('messageModalLabel');

        /**
         * Текст сообщения в модальном окне.
         * @type {HTMLElement}
         */
        const modalMessage = document.getElementById('modalMessage');

        /**
         * Кнопка в модальном окне.
         * @type {HTMLElement}
         */
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

        // Отображение модального окна
        $('#messageModal').modal('show');
    }
});
