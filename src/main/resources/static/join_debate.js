function joinDebate() {
    let url = new URL("/process_join_debate", document.URL);
    let debateCode = document.getElementById("code-input").value;
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    fetch(url, {
        method: 'POST',
        headers: {
            [header]: token,
            "charset": "UTF-8",
            "Content-Type": "application/json"
        },
        body: JSON.stringify(debateCode)
    })
        .catch(error => console.log('failed to send request to server '+ error))
        .then(response => {
            return response.json();
        })
        .catch(error => console.log('failed to parse response: ' + error))
        .then (joinDebateStatus => {
            handleJoinDebateStatus(joinDebateStatus)
        });
}

function handleJoinDebateStatus(joinDebateStatus) {
    if (joinDebateStatus.success) {
        console.log("Success");
    } else {
        showErrorAlert(joinDebateStatus.errorMessage)
    }
}

function showErrorAlert(message) {
    window.alert(message)
}