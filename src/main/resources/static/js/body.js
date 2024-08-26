$(document).ready(function() {
    const header = document.getElementById('mainHeader');

    header.style.position = 'fixed';
    header.style.top = '0';

    // Флаг для отслеживания скрытия header
    let headerHidden = true;

    let hideButton = document.getElementById('hideButton');

    hideButton.addEventListener('click', toggleHeaderVisibility);

    // Функция для скрытия/показа header
    function toggleHeaderVisibility() {
        if (!headerHidden) {
            header.style.position = 'fixed';
            header.style.top = '0';
            headerHidden = true;
        } else {
            header.style.position = 'relative';
            headerHidden = false;
        }
    }
});