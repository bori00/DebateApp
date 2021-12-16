function joinAffirmativeTeam() {
    joinTeam(true);
}

function joinNegativeTeam() {
    joinTeam(false);
}

function joinTeam(joinsProTeam) {
    const url = new URL("/process_join_team", document.URL);
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    const postData = {joinsProTeam: joinsProTeam};
    fetch(url, {
        method: 'POST',
        headers: {
            [header]: token,
            "charset": "UTF-8",
            "Content-Type": "application/json"
        },
        body: JSON.stringify(joinsProTeam)
    })
        .catch(error => console.log('failed to send request to server '+ error))
        .then(response => {
            return response.json();
        })
        .catch(error => console.log('failed to parse response: ' + error))
        .then (joinTeamStatus => {
            handleJoinTeamStatus(joinTeamStatus)
        });
}

function handleJoinTeamStatus(joinTeamStatus) {
    if (joinTeamStatus.success) {
        window.location.href = "/go_to_debate_lobby"
    } else {
        window.alert(joinTeamStatus.errorMsg)
        window.location.href = "/"
    }
}