$(document).ready(function() {
    const button = document.getElementById('kanbanBtn');
    button.click();

    const kanbanId = document.getElementById('boardContainer').getAttribute('name');

    const taskLists = document.querySelectorAll('.task-list');
    const titleInput = document.querySelector('#titleInput');
    const descriptionInput = document.querySelector('#descriptionInput');
    const submitButton = document.querySelector('#submitButton');
    const errorContainer = document.querySelector('.error-container');

    const editModal = new bootstrap.Modal(document.getElementById('editModal'));
    const addTaskModal = new bootstrap.Modal(document.getElementById('addTaskModal'));
    const editTitleInput = document.querySelector('#editTitle');
    const editDescriptionInput = document.querySelector('#editDescription');
    const saveButton = document.querySelector('#saveButton');

    let tasks = [];
    let taskIdToEdit = null; // Сохраняем ID задачи, которую редактируем
    let currentColumn = null; // Сохраняем текущую колонку для добавления задачи

    renderKanbanBoard();

    // Функция для открытия модального окна для добавления задачи
    function openAddTaskModal(column) {
        currentColumn = column;
        titleInput.value = '';
        descriptionInput.value = '';
        addTaskModal.show();
    }

    // Обработка клика по кнопке "Добавить задачу"
    document.querySelectorAll('.add-task-button').forEach(button => {
        button.addEventListener('click', () => {
            const column = button.parentElement.parentElement.id; // Получаем ID текущей колонки
            openAddTaskModal(column);
        });
    });

    // Обработчик кнопки для сохранения новой задачи
    submitButton.addEventListener('click', async (e) => {
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
                status: currentColumn // Добавляем задачу в текущую колонку
            };
            tasks.push(newTask);
            createTask(newId, titleInput.value, descriptionInput.value, currentColumn); // Добавляем задачу в UI
            addTaskModal.hide(); // Закрываем модальное окно после добавления задачи
            syncTasks(); // Синхронизация после добавления
        } else {
            showError('Названия должны быть уникальными!');
        }
    });

    taskLists.forEach((taskList) => {
        taskList.addEventListener('dragover', dragOver);
        taskList.addEventListener('drop', dragDrop);
    });

    function createTask(taskId, title, description, status) {
        try {
            const taskCard = document.createElement('div');
            const taskHeader = document.createElement('div');
            const taskTitle = document.createElement('p');
            const taskDescriptionContainer = document.createElement('div');
            const taskDescription = document.createElement('p');
            const deleteIcon = document.createElement('p');

            taskHeader.style.backgroundColor = addColor(status);

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
        } catch (error) {
        }
    }

    function editTask(taskId, taskCard) {
        const task = tasks.find(t => t.id == taskId);
        taskIdToEdit = taskId; // Сохраняем ID задачи

        // Заполняем поля модального окна текущими данными задачи
        editTitleInput.value = task.title;
        editDescriptionInput.value = task.description;

        // Показываем модальное окно
        editModal.show();
    }

    window.addEventListener('click', (event) => {
        if (event.target == editModal || event.target == addTaskModal) {
            editModal.hide();
            addTaskModal.hide();
        }
    });

    function addColor(column) {
        let color;
        switch (column) {
            case 'backlog':
                color = 'rgb(148,95,255)';
                break;
            case 'doing':
                color = 'rgb(120,204,224)';
                break;
            case 'inProcess':
                color = 'rgb(211,147,86)';
                break;
            case 'done':
                color = 'rgb(187,174,99)';
                break;
            default:
                color = 'rgb(255,205,205)';
        }
        return color;
    }

    function addTasks() {
        fetchTasks();
    }

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

    // Функции для отрисовки досок
    function renderKanbanBoard() {
        const boardHtml = `
                <div id="kanban-board" class="centr">
                    <div class="row mb-4">
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="backlog">
                                <div class="kanban-header">
                                    <h3>Бэклог</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="doing">
                                <div class="kanban-header">
                                    <h3>Сделать</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="inProcess">
                                <div class="kanban-header">
                                    <h3>В процессе</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="done">
                                <div class="kanban-header">
                                    <h3>Сделано</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        document.getElementById('boardContainer').innerHTML = boardHtml;

        // Действия, которые нужно выполнить после отрисовки Kanban доски
        executeKanbanScripts();
    }

    function renderKanoBoard() {
        const boardHtml = `
                <div id="kano-board" class="centr">
                    <div class="row mb-4">
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="basic">
                                <div class="kanban-header">
                                    <h3>Основные</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="performance">
                                <div class="kanban-header">
                                    <h3>Производительность</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="excitement">
                                <div class="kanban-header">
                                    <h3>Возбуждение</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="indifferent">
                                <div class="kanban-header">
                                    <h3>Безразличие</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        document.getElementById('boardContainer').innerHTML = boardHtml;

        // Действия, которые нужно выполнить после отрисовки Kano доски
        executeKanoScripts();
    }

    function renderMoSCoWBoard() {
        const boardHtml = `
                <div id="moscow-board" class="centr">
                    <div class="row mb-4">
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="mustHave">
                                <div class="kanban-header">
                                    <h3>Must Have</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="shouldHave">
                                <div class="kanban-header">
                                    <h3>Should Have</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="couldHave">
                                <div class="kanban-header">
                                    <h3>Could Have</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="wontHave">
                                <div class="kanban-header">
                                    <h3>Won't Have</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        document.getElementById('boardContainer').innerHTML = boardHtml;

        // Действия, которые нужно выполнить после отрисовки MoSCoW доски
        executeMoSCoWScripts();
    }

    // Функция для обработки выбора методологии
    function switchMethodology(methodology) {
        if (methodology === 'Kanban') {
            renderKanbanBoard();
        } else if (methodology === 'Kano') {
            renderKanoBoard();
        } else if (methodology === 'MoSCoW') {
            renderMoSCoWBoard();
        }
    }

    // Функции для выполнения после отрисовки досок
    function executeKanbanScripts() {
        addTasks();
        attachAddTaskButtonEvents();
    }

    function executeKanoScripts() {
        addTasks();
        attachAddTaskButtonEvents();
    }

    function executeMoSCoWScripts() {
        addTasks();
        attachAddTaskButtonEvents();
    }

    // Добавление слушателей событий для кнопок переключения
    document.getElementById('kanbanBtn').addEventListener('click', function () {
        switchMethodology('Kanban');
    });

    document.getElementById('kanoBtn').addEventListener('click', function () {
        switchMethodology('Kano');
    });

    document.getElementById('moscowBtn').addEventListener('click', function () {
        switchMethodology('MoSCoW');
    });

    // Первоначальная загрузка Kanban доски
    document.addEventListener('DOMContentLoaded', function () {
        switchMethodology('Kanban');
    });

    function attachAddTaskButtonEvents() {
        document.querySelectorAll('.add-task-button').forEach(button => {
            button.addEventListener('click', () => {
                const column = button.parentElement.parentElement.id; // Получаем ID текущей колонки
                openAddTaskModal(column);
            });
        });
    }

    async function fetchTasks() {
        try {
            const response = await fetch(`/api/kanban/${kanbanId}/tasks`);
            tasks = await response.json();
            tasks.forEach((task) => createTask(task.id, task.title, task.description, task.status));
        } catch (error) {
            showError('Не удалось загрузить задачу на сервер.');
        }
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
                    editModal.hide();
                } else {
                    showError('Failed to update the task.');
                }
            } catch (error) {
                console.error('Error:', error);
                showError('Failed to update the task.');
            }
        }
    });

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


});
