/**
 * Скрипт управления Kanban-досками на странице.
 *
 * Функционал:
 * 1. Удаление Kanban-досок.
 * 2. Создание Kanban-досок.
 * 3. Приглашение пользователей на доску.
 * 4. Редактирование заголовков Kanban-досок.
 * 5. Управление отображением модальных окон.
 */
$(document).ready(function() {
    /**
     * Кнопка отправки данных для создания Kanban-доски.
     * @type {HTMLButtonElement}
     */
    const submitButton = document.querySelector('#submit-button');

    /**
     * Кнопка сохранения приглашённого пользователя.
     * @type {HTMLButtonElement}
     */
    const saveButton = document.querySelector('#saveButton');

    /**
     * Экземпляр модального окна для добавления пользователя.
     * @type {bootstrap.Modal}
     */
    const addUserModal = new bootstrap.Modal(document.getElementById('addUserModal'));

    /**
     * Контейнер для отображения ошибок.
     * @type {HTMLElement}
     */
    const errorContainer = document.querySelector('.error-container');

    // Удаление Kanban-доски
    document.querySelectorAll('.delete-kanban-button').forEach(button => {
        button.addEventListener('click', () => {
            const kanbanId = button.getAttribute('data-kanban-id');
            const kanbanTitle = button.getAttribute('data-kanban-title');
            document.getElementById('kanbanTitle').textContent = kanbanTitle;
            $('#deleteKanbanModal').modal('show');
            document.getElementById('confirmDeleteButton').onclick = () => deleteKanban(kanbanId);
        });
    });

    /**
     * Удаление Kanban-доски по идентификатору.
     * @param {string} kanbanId - Идентификатор Kanban-доски.
     */
    function deleteKanban(kanbanId) {
        fetch(`/api/kanban/${kanbanId}/deactivate-kanban`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (response.ok) {
                    removeKanbanFromList(kanbanId);
                    $('#deleteKanbanModal').modal('hide'); // Закрытие модального окна после удаления
                } else {
                    console.error('Ошибка при удалении доски');
                }
            })
            .catch(error => console.error('Error:', error));
    }

    /**
     * Удаление Kanban-доски из списка на странице.
     * @param {string} kanbanId - Идентификатор Kanban-доски.
     */
    function removeKanbanFromList(kanbanId) {
        const kanbanItem = document.querySelector(`.delete-kanban-button[data-kanban-id='${kanbanId}']`).closest('li');
        if (kanbanItem) kanbanItem.remove();
    }

    /**
     * Асинхронная функция создания Kanban-доски.
     */
    async function fetchKanban() {
        const title = document.getElementById("title").value;
        if (!title) {
            showModal('Ошибка', 'Название не может быть пустым!', 'error');
            return;
        }

        const formData = { title };

        try {
            const response = await fetch('/api/kanban/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            });

            if (response.ok) {
                const data = await response.json();
                addKanbanToList(data);  // Добавляем доску в список немедленно
                showModal('Успех', 'Доска успешно создана!', 'success'); // Показываем сообщение об успехе
            } else {
                console.error('Ошибка при создании доски');
                showModal('Ошибка', 'Ошибка при создании доски', 'error');
            }
        } catch (error) {
            showModal('Ошибка', 'Ошибка при создании доски', 'error');
        }
    }

    /**
     * Добавление Kanban-доски в список на странице.
     * @param {Object} kanban - Объект с данными Kanban-доски.
     * @param {string} kanban.id - Идентификатор Kanban-доски.
     * @param {string} kanban.title - Название Kanban-доски.
     */
    function addKanbanToList(kanban) {
        const kanbanList = document.querySelector('.kanban-list');
        const li = document.createElement('li');
        li.classList.add('li');

        const link = document.createElement('a');
        link.href = `/api/kanban/${kanban.id}`;
        link.textContent = kanban.title;

        const deleteButton = document.createElement('button');
        deleteButton.classList.add('delete-kanban-button');
        deleteButton.setAttribute('data-kanban-id', kanban.id);
        deleteButton.setAttribute('data-kanban-title', kanban.title);
        deleteButton.innerHTML = `<i class="material-icons">delete_outline</i>`;
        deleteButton.addEventListener('click', () => {
            document.getElementById('kanbanTitle').textContent = kanban.title;
            $('#deleteKanbanModal').modal('show');
            document.getElementById('confirmDeleteButton').onclick = () => deleteKanban(kanban.id);
        });

        li.appendChild(link);
        li.appendChild(deleteButton);
        kanbanList.appendChild(li);
    }

    /**
     * Функция для добавления Kanban-доски.
     */
    function addKanban() {
        fetchKanban();
    }

    submitButton.addEventListener('click', addKanban);

    /**
     * Отображение модального окна.
     * @param {string} title - Заголовок модального окна.
     * @param {string} message - Сообщение модального окна.
     * @param {'success'|'error'} type - Тип сообщения (успех или ошибка).
     */
    function showModal(title, message, type) {
        const modalElement = document.getElementById('messageModal');
        const modalLabel = document.getElementById('messageModalLabel');
        const modalMessage = document.getElementById('modalMessage');
        const modalButton = document.getElementById('modal-button');

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
     * Отправка приглашения пользователю на Kanban-доску.
     * @param {string} kanbanId - Идентификатор Kanban-доски.
     * @param {string} inviteeEmail - Email приглашённого пользователя.
     */
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

    /**
     * Открывает модальное окно для добавления пользователя.
     */
    function openAddUserModal() {
        addUserModal.show();
    }

    // Добавляем обработчики событий для кнопок добавления пользователей.
    document.querySelectorAll('.add-user-button').forEach(button => {
        button.addEventListener('click', () => {
            openAddUserModal();
        });
    });


    // Обработчик сохранения нового пользователя.
    saveButton.addEventListener('click', async (e) => {
        e.preventDefault();
        const inviteeEmail = document.getElementById('inviteeEmail').value;
        const kanbanId = document.getElementById('kanbanName').value;

        inviteUser(kanbanId, inviteeEmail);
        addUserModal.hide();
    });

    // Добавляем обработчики событий для кнопок редактирования Kanban-доски.
    document.querySelectorAll('.edit-kanban-button').forEach(button => {
        button.addEventListener('click', () => {
            const kanbanId = button.getAttribute('data-kanban-id');
            const kanbanTitle = button.getAttribute('data-kanban-title');
            $('#editKanbanTitleModal').modal('show');
            document.getElementById('modal-title-input').value = kanbanTitle;
            document.getElementById('confirmUpdateButton').onclick = () => updateKanbanTitle(kanbanId);
        });
    });

    /**
     * Закрывает модальное окно для редактирования названия Kanban-доски.
     */
    function closeEditTitleModal() {
        $('#editKanbanTitleModal').modal('hide'); // Закрываем модалку
    }

    /**
     * Обновляет название Kanban-доски.
     * @param {string} kanbanId - Идентификатор Kanban-доски.
     */
    function updateKanbanTitle(kanbanId) {
        const newTitle = document.getElementById("modal-title-input").value;

        console.log(newTitle)

        fetch(`/api/kanban/${kanbanId}/title`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title: newTitle })
        })
            .then(response => response.json())
            .then(data => {
                console.log("Response data:", data); // Проверка возвращаемых данных
                closeEditTitleModal();
                document.getElementById("desk-" + kanbanId.toString()).textContent = data.title; // Обновляем на странице
            })
            .catch(error => {
                alert(error.message);
            });
    }
});
