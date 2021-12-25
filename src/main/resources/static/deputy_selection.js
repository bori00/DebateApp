const DEPUTY1_SELECTION_PHASE = "deputy1-voting-time";
const DEPUTY2_SELECTION_PHASE = "deputy2-voting-time";

function onDeputyVotingTimesUp_Judge() {
    window.alert("This deputy voting phase has ended!");
    window.location.href="/go_to_deputy_selection";
}

function onDeputy1VotingTimesUp_Player() {
    window.alert("The 1st deputy voting phase has ended!");
    window.location.href="/go_to_ongoing_debates_current_phase";
}

function onDeputy2VotingTimesUp_Player() {
    window.alert("The 2nd deputy voting phase has ended!");
    window.location.href="/go_to_ongoing_debates_current_phase";
}

function voteFor(selectedCandidateName) {
    const url = new URL("/cast_vote", document.URL);
    const body = {userName: selectedCandidateName};
    postRequestToServer(url, body)
        .then(response => {
            return response.json();
        })
        .catch(error => console.log('failed to parse response: ' + error))
        .then (castVoteResponse => {
            if (castVoteResponse.success) {
                showWaitingStateForPlayerWhoVoted();
            } else {
                alert(castVoteResponse.errorMsg)
            }
        });
}

function showWaitingStateForPlayerWhoVoted() {
    setElementVisibility("votingOptionsDiv", false);
    setElementVisibility("votingLobbyDiv", true);
}

async function subscribeToDebateVotingStatusSocket() {
    var socket = new SockJS('/secured/debates');
    var stompClient = Stomp.over(socket);
    var sessionId = "";

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-voting-status",
            function (votingStatus) {
                console.log(votingStatus)
            });
    });
}