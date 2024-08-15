$(document).ready(function() {
    const submitButton = document.querySelector('#submit-button');

    async function fetchKanban() {
        const formData = {
            title: document.getElementById("title").value
        };
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
});