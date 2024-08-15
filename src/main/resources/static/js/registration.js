$(document).ready(function() {
    document.getElementById('registrationForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const formData = {
            name: event.target.name.value,
            email: event.target.email.value,
            password: event.target.password.value
        };

        fetch('http://localhost:8090/api/v1/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => response.text())
            .then(data => {
                alert('Registration successful!');
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Registration failed!');
            });
    });
});