const DEPUTY1_SELECTION_PHASE = "deputy1-voting-time";
const DEPUTY2_SELECTION_PHASE = "deputy1-voting-time";

function onDeputyVotingTimesUp_Judge() {
    window.alert("This deputy voting phase has ended!");
    window.location.href="/go_to_deputy_selection";
}

function onDeputy1VotingTimesUp_Player() {
    window.alert("The 1st deputy voting phase has ended!");
    window.location.href="/go_to_deputy_selection";
}

function onDeputy2VotingTimesUp_Player() {
    console.log("The 2nd deputy voting phase has ended!");
}