function joinDebate() {
    let url = new URL("/join_debate", document.URL);
    let debateCode = document.getElementById("code-input").value;
    postRequestToServer(url, debateCode)
        .then(response => {
            return response.json();
        })
        .catch(error => console.log('failed to parse response: ' + error))
        .then (joinDebateStatus => {
            handleOngoingDebateRequestResponse(joinDebateStatus);
        });
}