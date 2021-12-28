function setElementVisibility(id, visible) {
    const element = document.getElementById(id);
    if (visible) {
        element.classList.remove('hide');
    } else {
        element.classList.add('hide');
    }
}

function createToastNotification(title, message) {
    let toast = document.createElement('div');
    toast.classList.add("toast");
    toast.setAttribute("role","alert");
    toast.setAttribute("aria-live", "assertive");
    toast.setAttribute("aria-atomic", "true");

    let toastHeader = document.createElement("div");
    toastHeader.classList.add("toast-header");
    toastHeader.innerText = title;

    let closeButton = document.createElement('button');
    closeButton.classList.add("ml-2 mb-1 close");
    closeButton.setAttribute("type", "button");
    closeButton.setAttribute("data-dismiss","toast");
    closeButton.setAttribute("aria-label", "Close");

    toastHeader.appendChild(closeButton);
    toast.appendChild(toastHeader);

    let toastBody = document.createElement("div");
    toastBody.classList.add("toast-body");
    toastBody.innerText = message;

    toast.appendChild(toastBody);

    return toast;
}