$(document).ready(function() {
    const submitButton = document.querySelector('#submit-button');

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
});