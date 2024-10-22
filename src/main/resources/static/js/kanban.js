$(document).ready(function() {
    // Проверяем, был ли флаг перезагрузки уже установлен
    if (!localStorage.getItem('reloaded')) {
        // Устанавливаем таймер на 1 секунду (1000 мс) для перезагрузки
        setTimeout(function() {
            // Устанавливаем флаг, что перезагрузка выполнена
            localStorage.setItem('reloaded', 'true');
            // Перезагружаем страницу
            location.reload();
        }, 1000); // Здесь можно изменить интервал по своему усмотрению
    }
});

$(document).ready(function() {
    const button = document.getElementById('kanbanBtn');
    button.click();

    const kanbanId = document.getElementById('boardContainer').getAttribute('name');

    let taskLists = document.querySelectorAll('.task-list');
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

    let urlFetchTasks = `/api/kanban/${kanbanId}/tasks`;
    let urlEditTask = `/api/kanban/${kanbanId}/tasks/`;
    let urlSuncTasks = `/api/kanban/${kanbanId}/tasks/sync`;
    let delurl = '/api/kanban/tasks/';

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

        fetch(urlFetchTasks, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(newTask),
        })
            .then((response) => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(`Не удалось загрузить задачу: ${text}`);
                    });
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                showError('Не удалось загрузить задачу на сервер.');
            });
    });

    function DropAndDragActivator() {
        taskLists.forEach((taskList) => {
            taskList.addEventListener('dragover', dragOver);
            taskList.addEventListener('drop', dragDrop);

            // Для каждого элемента задачи (task) внутри списка задач добавляем обработчик dragstart
            const tasks = taskList.querySelectorAll('.task');
            tasks.forEach((task) => {
                task.setAttribute('draggable', true);
                task.addEventListener('dragstart', dragStart);
            });
        });
    }

    DropAndDragActivator()

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
            case 'Mandatory':
                color = 'rgb(148,95,255)';
                break;
            case 'Must':
                color = 'rgb(148,95,255)';
                break;
            case 'doing':
                color = 'rgb(120,204,224)';
                break;
            case 'Basic':
                color = 'rgb(120,204,224)';
                break;
            case 'Should':
                color = 'rgb(120,204,224)';
                break;
            case 'inProcess':
                color = 'rgb(211,147,86)';
                break;
            case 'Attractive':
                color = 'rgb(211,147,86)';
                break;
            case 'Could':
                color = 'rgb(211,147,86)';
                break;
            case 'done':
                color = 'rgb(187,174,99)';
                break;
            case 'Indifference':
                color = 'rgb(187,174,99)';
                break;
            case 'Wont':
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
        console.log(elementBeingDragged);
    }

    function dragOver(e) {
        e.preventDefault();
        console.log(2)
    }

    async function dragDrop() {
        const columnId = this.parentNode.id;
        elementBeingDragged.firstChild.style.backgroundColor = addColor(columnId);
        this.append(elementBeingDragged);
        const taskId = elementBeingDragged.getAttribute('task-id');
        console.log(taskId)

        if (taskId !== null) {
            const task = tasks.find(t => t.id == taskId);
            task.status = columnId;
            console.log(task)

            try {
                const response = await fetch(urlEditTask + taskId, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(task),
                });

                if (!response.ok) {
                    showError('Ошибка при изменении задачи.');
                }
            } catch (error) {
                console.error('Error:', error);
                showError('Ошибка при изменении задачи.');
            }
        }
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

        const taskToDelete = tasks.find((task) => task.title === headerTitle);

        if (!taskToDelete) {
            console.error('Task not found');
            return;
        }

        const taskId = taskToDelete.id;

        fetch(delurl + taskId, {
            method: 'DELETE',
        })
            .then((response) => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(`Failed to delete task: ${text}`);
                    });
                }

                tasks = tasks.filter((task) => task.id !== taskId);
                this.parentNode.parentNode.remove();

                //syncTasks();
            })
            .catch((error) => {
                console.error('Error:', error);
                showError('Failed to delete task from server.');
            });
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
                            <div class="task-column" id="Mandatory">
                                <div class="kanban-header">
                                    <h3>Обязательные</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="Basic">
                                <div class="kanban-header">
                                    <h3>Базовые</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="Attractive">
                                <div class="kanban-header">
                                    <h3>Привлекательные</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="Indifference">
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
                            <div class="task-column" id="Must">
                                <div class="kanban-header">
                                    <h3>Обязаны</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="Should">
                                <div class="kanban-header">
                                    <h3>Должны</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="Could">
                                <div class="kanban-header">
                                    <h3>Могли бы</h3>
                                    <button type="button" class="btn btn-primary add-task-button">Добавить</button>
                                </div>
                                <hr class="custom-hr">
                                <div class="task-list"></div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="task-column" id="Wont">
                                <div class="kanban-header">
                                    <h3>Не будет</h3>
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

    function GlobalReactivation() {
        taskLists = document.querySelectorAll('.task-list');

        addTasks();
        attachAddTaskButtonEvents();
        DropAndDragActivator();
    }

    // Функции для выполнения после отрисовки досок
    function executeKanbanScripts() {
        urlFetchTasks = `/api/kanban/${kanbanId}/tasks`;
        urlEditTask = `/api/kanban/${kanbanId}/tasks/`;
        urlSuncTasks = `/api/kanban/${kanbanId}/tasks/sync`;
        delurl = `/api/kanban/tasks/`;

        GlobalReactivation();
    }

    function executeKanoScripts() {
        urlFetchTasks = `/api/kanban/${kanbanId}/kano-tasks`;
        urlEditTask = `/api/kanban/${kanbanId}/kano-tasks/`;
        urlSuncTasks = `/api/kanban/${kanbanId}/kano-tasks/sync`;
        delurl = `/api/kanban/kano-tasks/`;

        GlobalReactivation();
    }

    function executeMoSCoWScripts() {
        urlFetchTasks = `/api/kanban/${kanbanId}/moscow-tasks`;
        urlEditTask = `/api/kanban/${kanbanId}/moscow-tasks/`;
        urlSuncTasks = `/api/kanban/${kanbanId}/moscow-tasks/sync`;
        delurl = `/api/kanban/moscow-tasks/`;

        GlobalReactivation();
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
            const response = await fetch(urlFetchTasks);
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
                const response = await fetch(urlEditTask + taskIdToEdit, {
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
                    showError('Ошибка при изменении задачи.');
                }
            } catch (error) {
                console.error('Error:', error);
                showError('Ошибка при изменении задачи.');
            }
        }
    });

    //const intervalId = setInterval(function() {
    //    syncTasks();
    //}, 5000)

    /*async function syncTasks() {
        try {
            const response = await fetch(urlSuncTasks, {
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
                    console.warn('Ответ в формате JSON не получен.');
                }
            } else {
                const errorText = await response.text();
                throw new Error(`Не удалось синхронизировать задачи: ${errorText}`);
            }
        } catch (error) {
            console.error('Error:', error);
            showError('Не удалось синхронизировать задачи с сервером.');
        }
    }*/

});
