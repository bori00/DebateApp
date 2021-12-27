function joinAffirmativeTeam() {
    joinTeam(true);
}

function joinNegativeTeam() {
    joinTeam(false);
}

function joinTeam(joinsProTeam) {
    const url = new URL("/join_team", document.URL);

    postRequestToServer(url, joinsProTeam)
        .then(response => {
            return response.json();
        })
        .catch(error => console.log('failed to parse response: ' + error))
        .then (joinTeamStatus => {
            handleOngoingDebateRequestResponse(joinTeamStatus);
        });
}

async function subscribeToDebateActivationSocket() {
    var socket = new SockJS('/secured/debates');
    var stompClient = Stomp.over(socket);
    var sessionId = "";

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-session-activated",
            function (activated) {
                window.alert("We're sorry, you missed this debate. The judge activated it" +
                    " despite you not having chosen a team yet.")
                window.location.href = "/";
            });
    });
}