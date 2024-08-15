// Флаг для отслеживания скрытия header
let headerHidden = false;

document.addEventListener('DOMContentLoaded', function() {
    let hideButton = document.getElementById('hideButton');
    if (hideButton) {
        hideButton.addEventListener('click', toggleHeaderVisibility);
    }
});

// Функция для скрытия/показа header
function toggleHeaderVisibility() {
    const header = document.getElementById('mainHeader');

    if (!headerHidden) {
        header.style.position = 'fixed';
        header.style.top = '0';
        headerHidden = true;
    } else {
        header.style.position = 'relative';
        headerHidden = false;
    }
}