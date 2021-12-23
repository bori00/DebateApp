function loadMenu() {
    checkIfUserAnOngoingDebate()
        .then(hasOngoingDebate => {
            if (hasOngoingDebate) {
                loadMenuForUserWithActiveDebate();
            } else {
                loadMenuForUserWithNoActiveDebate();
            }
        })
}

function checkIfUserAnOngoingDebate() {
    let url = new URL("/has_user_ongoing_debate", document.URL);

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    return fetch(url, {
        method: 'POST',
        headers: {
            [header]: token,
            "charset": "UTF-8",
            "Content-Type": "application/json"
        },
        body: {}
    })
        .catch(error => console.log('failed to send request to server '+ error))
        .then(response => {
            return response.json();
        })
        .catch(error => console.log('failed to parse response: ' + error));
}

function loadMenuForUserWithActiveDebate() {
    addMenuItem("/go_to_ongoing_debates_current_phase", "My Ongoing Debate")
}

function loadMenuForUserWithNoActiveDebate() {
    addMenuItem("join_debate", "Join a Debate")
    addMenuItem("configure_debates", "Setup a Debate")
}

function addMenuItem(link, text) {
    const newItem = document.createElement("li");
    newItem.classList.add("nav-item")
    const newLink = document.createElement("a");
    newLink.innerText = text;
    newLink.classList.add("nav-link");
    newLink.setAttribute("href", link);
    newItem.appendChild(newLink);
    const menuItemsList = document.getElementById("menu_items");
    menuItemsList.appendChild(newItem);
}