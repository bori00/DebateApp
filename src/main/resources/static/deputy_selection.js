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
            function (votingStatusJSON) {
                const votingStatus = JSON.parse(votingStatusJSON.body);
                updateUIBasedOnVotingStatus(votingStatus);
                console.log(votingStatus);
            });
    });
}

function updateUIBasedOnVotingStatus(votingStatus) {
    document.getElementById("proDeputy1NameCell").innerText = votingStatus.proDeputy1Name;
    document.getElementById("proDeputy2NameCell").innerText = votingStatus.proDeputy2Name;
    document.getElementById("conDeputy1NameCell").innerText = votingStatus.conDeputy1Name;
    document.getElementById("conDeputy2NameCell").innerText = votingStatus.conDeputy2Name;

    document.getElementById("proVotesTd").innerHTML="";
    document.getElementById("conVotesTd").innerHTML="";

    proVotesList = document.createElement("ul")
    document.getElementById("proVotesTd").appendChild(proVotesList)

    conVotesList = document.createElement("ul")
    document.getElementById("conVotesTd").appendChild(conVotesList)

    votingStatus.currentVotingPhaseVotesPro
        .forEach(votedPlayer => addBadgeListItem(proVotesList, votedPlayer.name, votedPlayer.noVotes));
    votingStatus.currentVotingPhaseVotesCon
        .forEach(votedPlayer => addBadgeListItem(conVotesList, votedPlayer.name, votedPlayer.noVotes));
}

function addBadgeListItem(list, itemText, badgeText) {
    item = document.createElement("li");
    item.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center");
    item.innerText = itemText;

    itemSpan = document.createElement("span")
    itemSpan.classList.add("badge", "badge-primary", "badge-pill")
    itemSpan.innerText = badgeText;

    item.appendChild(itemSpan);

    list.appendChild(item);
}