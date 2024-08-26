$(document).ready(function() {
    const errorMessageDiv = document.getElementById('error-message');

    errorMessageDiv.style.display = 'none';
    errorMessageDiv.style.visibility = 'hidden';
    errorMessageDiv.innerHTML = '';

    let errorTimeout;

    $('.unmask').on('click', function(){
        if($(this).prev('input').attr('type') === 'password')
            $(this).prev('input').prop('type', 'text');
        else
            $(this).prev('input').prop('type', 'password');
        return false;
    });

    document.getElementById('forgotPasswordBtn').addEventListener('click', function () {
        const email = document.getElementById('username').value;
        if (email) {
            fetch(`/api/user/forgot-password-with-email?email=${email}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
            }).then(response => {
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
});
