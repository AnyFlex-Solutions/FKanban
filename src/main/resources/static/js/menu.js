$(document).ready(function() {
    const submitButton = document.querySelector('#submit-button');
    const saveButton = document.querySelector('#saveButton');
    const addUserModal = new bootstrap.Modal(document.getElementById('addUserModal'));
    const errorContainer = document.querySelector('.error-container');

    document.querySelectorAll('.delete-kanban-button').forEach(button => {
        button.addEventListener('click', () => {
            const kanbanId = button.getAttribute('data-kanban-id');
            const kanbanTitle = button.getAttribute('data-kanban-title');
            document.getElementById('kanbanTitle').textContent = kanbanTitle;
            $('#deleteKanbanModal').modal('show');
            document.getElementById('confirmDeleteButton').onclick = () => deleteKanban(kanbanId);
        });
    });

    function deleteKanban(kanbanId) {
        fetch(`/api/kanban/${kanbanId}/deactivate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (response.ok) {
                    location.reload(); // Обновляем страницу после успешного удаления
                } else {
                    console.error('Failed to delete kanban');
                }
            })
            .catch(error => console.error('Error:', error));
    }

    async function fetchKanban() {
        const title = document.getElementById("title").value;
        if (!title) {
            showModal('Ошибка', 'Название не может быть пустым!', 'error');
            return;
        }

        const formData = { title };

        try {
            await fetch('/api/kanban/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Success:', data);
                    // Redirect or update page as needed
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        } catch (error) {
            showError('Failed to sync tasks with the server.');
        }
    }


    function addKanban() {
        fetchKanban();
    }

    submitButton.addEventListener('click', addKanban);

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

    async function inviteUser(kanbanId, inviteeEmail) {
        try {
            const response = await fetch(`/api/kanban/${kanbanId}/invite?inviteeEmail=${inviteeEmail}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Ошибка приглашения. Попробуйте ещё раз!');
            }

            const data = await response.text(); // используем text(), если сервер возвращает строку
            showModal('Success', 'Пользователю отправлено письмо с приглашением!', 'success');
        } catch (error) {
            showModal('Ошибка приглашения. Попробуйте ещё раз!');
        }
    }

    // Функция для открытия модального окна для добавления задачи
    function openAddUserModal() {
        addUserModal.show();
    }

    document.querySelectorAll('.add-user-button').forEach(button => {
        button.addEventListener('click', () => {
            openAddUserModal();
        });
    });

    saveButton.addEventListener('click', async (e) => {
        e.preventDefault();
        const inviteeEmail = document.getElementById('inviteeEmail').value;
        const kanbanId = document.getElementById('kanbanName').value;

        inviteUser(kanbanId, inviteeEmail);
        addUserModal.hide();
    });
});
