$(document).ready(function() {
    const kanbanId = document.getElementById('kanban-board').getAttribute( 'name' );

    const taskLists = document.querySelectorAll('.task-list');
    const titleInput = document.querySelector('#title');
    const descriptionInput = document.querySelector('#description');
    const submitButton = document.querySelector('#submit-button');
    const errorContainer = document.querySelector('.error-container');

    const editModal = document.querySelector('#editModal');
    const editTitleInput = document.querySelector('#editTitle');
    const editDescriptionInput = document.querySelector('#editDescription');
    const saveButton = document.querySelector('#saveButton');
    const closeModalButton = document.querySelector('.close');

    let tasks = [];
    let taskIdToEdit = null; // Сохраняем ID задачи, которую редактируем

    async function fetchTasks() {
        try {
            const response = await fetch(`/api/kanban/${kanbanId}/tasks`);
            tasks = await response.json();
            tasks.forEach((task) => createTask(task.id, task.title, task.description, task.status));
        } catch (error) {
            showError('Failed to load tasks from the server.');
        }
    }

    taskLists.forEach((taskList) => {
        taskList.addEventListener('dragover', dragOver);
        taskList.addEventListener('drop', dragDrop);
    });

    function createTask(taskId, title, description, status) {
        const taskCard = document.createElement('div');
        const taskHeader = document.createElement('div');
        const taskTitle = document.createElement('p');
        const taskDescriptionContainer = document.createElement('div');
        const taskDescription = document.createElement('p');
        const deleteIcon = document.createElement('p');

        taskCard.classList.add('task-container');
        taskHeader.classList.add('task-header');
        taskDescriptionContainer.classList.add('task-description-container');

        taskTitle.textContent = title;
        taskDescription.textContent = description;
        deleteIcon.textContent = '☒';

        taskCard.setAttribute('draggable', true);
        taskCard.setAttribute('task-id', taskId);

        taskCard.addEventListener('dragstart', dragStart);
        deleteIcon.addEventListener('click', deleteTask);

        // Добавляем обработчик двойного клика для редактирования
        taskCard.addEventListener('dblclick', () => {
            editTask(taskId, taskCard);
        });

        taskHeader.append(taskTitle, deleteIcon);
        taskDescriptionContainer.append(taskDescription);
        taskCard.append(taskHeader, taskDescriptionContainer);

        const taskColumn = document.querySelector(`#${status} .task-list`);
        taskColumn.append(taskCard);
    }

    function editTask(taskId, taskCard) {
        const task = tasks.find(t => t.id == taskId);
        taskIdToEdit = taskId; // Сохраняем ID задачи

        // Заполняем поля модального окна текущими данными задачи
        editTitleInput.value = task.title;
        editDescriptionInput.value = task.description;

        // Показываем модальное окно
        editModal.style.display = 'block';
    }

    saveButton.addEventListener('click', async (e) => {
        e.preventDefault();

        if (taskIdToEdit !== null) {
            const task = tasks.find(t => t.id == taskIdToEdit);
            task.title = editTitleInput.value;
            task.description = editDescriptionInput.value;

            try {
                const response = await fetch(`/api/kanban/${kanbanId}/tasks/${taskIdToEdit}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(task),
                });

                if (response.ok) {
                    // Обновляем задачу на доске
                    const taskCard = document.querySelector(`[task-id='${taskIdToEdit}']`);
                    taskCard.querySelector('.task-header p').textContent = task.title;
                    taskCard.querySelector('.task-description-container p').textContent = task.description;

                    // Закрываем модальное окно
                    editModal.style.display = 'none';
                } else {
                    showError('Failed to update the task.');
                }
            } catch (error) {
                console.error('Error:', error);
                showError('Failed to update the task.');
            }
        }
    });

    closeModalButton.addEventListener('click', () => {
        editModal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target == editModal) {
            editModal.style.display = 'none';
        }
    });

    function addColor(column) {
        let color;
        switch (column) {
            case 'backlog':
                color = 'rgb(96, 96, 192)';
                break;
            case 'doing':
                color = 'rgb(83, 156, 174)';
                break;
            case 'done':
                color = 'rgb(224, 165, 116)';
                break;
            case 'discard':
                color = 'rgb(222, 208, 130)';
                break;
            default:
                color = 'rgb(232, 232, 232)';
        }
        return color;
    }

    async function syncTasks() {
        try {
            const response = await fetch(`/api/kanban/${kanbanId}/tasks/sync`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(tasks),
            });

            if (response.ok) {
                const textResponse = await response.text(); // Получаем ответ как текст
                const jsonResponse = textResponse ? JSON.parse(textResponse) : null; // Парсим JSON только если не пусто

                if (!jsonResponse) {
                    console.warn('No JSON response received.');
                }
            } else {
                const errorText = await response.text();
                throw new Error(`Failed to sync tasks: ${errorText}`);
            }
        } catch (error) {
            console.error('Error:', error);
            showError('Failed to sync tasks with the server.');
        }
    }

    function addTasks() {
        fetchTasks();
    }

    addTasks();

    setInterval(syncTasks, 10000); // Sync every 10 seconds

    let elementBeingDragged;

    function dragStart() {
        elementBeingDragged = this;
    }

    function dragOver(e) {
        e.preventDefault();
    }

    function dragDrop() {
        const columnId = this.parentNode.id;
        elementBeingDragged.firstChild.style.backgroundColor = addColor(columnId);
        this.append(elementBeingDragged);
        const taskId = elementBeingDragged.getAttribute('task-id');
        const task = tasks.find(t => t.id == taskId);
        task.status = columnId;
        syncTasks(); // Sync immediately after dragging a task
    }

    function showError(message) {
        const errorMessage = document.createElement('p');
        errorMessage.textContent = message;
        errorMessage.classList.add('error-message');
        errorContainer.append(errorMessage);

        setTimeout(() => {
            errorContainer.textContent = '';
        }, 2000);
    }

    function addTask(e) {
        e.preventDefault();
        const filteredTitles = tasks.filter((task) => {
            return task.title === titleInput.value;
        });

        if (!filteredTitles.length) {
            const newId = tasks.length;
            const newTask = {
                id: newId,
                title: titleInput.value,
                description: descriptionInput.value,
                status: 'backlog'
            };
            tasks.push(newTask);
            createTask(newId, titleInput.value, descriptionInput.value, 'backlog');
            titleInput.value = '';
            descriptionInput.value = '';
            syncTasks(); // Sync immediately after adding a new task
        } else {
            showError('Title must be unique!');
        }
    }
    submitButton.addEventListener('click', addTask);

    function deleteTask() {
        const headerTitle = this.parentNode.firstChild.textContent;

        const filteredTasks = tasks.filter((task) => {
            return task.title === headerTitle;
        });

        tasks = tasks.filter((task) => {
            return task !== filteredTasks[0];
        });

        this.parentNode.parentNode.remove();
        syncTasks(); // Sync immediately after deleting a task
    }
});
