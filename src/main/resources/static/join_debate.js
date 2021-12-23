function joinDebate() {
    let url = new URL("/join_debate", document.URL);
    let debateCode = document.getElementById("code-input").value;
    postRequestToServer(url, debateCode)
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
        if (joinDebateStatus.debatePhaseRedirectNeeded) {
            window.location.href = "/go_to_ongoing_debates_current_phase"
        }
    } else {
        window.alert(joinDebateStatus.errorMessage)
    }
}