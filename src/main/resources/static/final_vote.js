async function voteForPro() {
    await castVote(true);
}

async function voteForContra() {
    await castVote(false);
}

async function castVote(votesForPro) {
    const destEndpoint = new URL("/cast_final_vote", document.URL);

    postRequestToServer(destEndpoint, votesForPro)
        .then(response => response.json())
        .catch(error => console.log('failed to parse response: ' + error))
        .then (castVoteResponse => {
            if (castVoteResponse.success) {
                setElementVisibility('voting-panel', false);
                setElementVisibility('voting-lobby', true);
            } else {
                alert(castVoteResponse.errorMsg)
            }
        });
}

async function showFinalVotesStatistics() {
    let destEndpoint = "/get_final_vote_status";
    let votesStatistics = await postRequestToServer(destEndpoint, null)
        .then(response => response.json())
        .catch(error => console.log('failed to parse response: ' + error));

    setElementVisibility('voting-statistics', true);
    document.getElementById('vote-pro-both-times').innerText = votesStatistics.noPlayersWhoVotedProBothTimes;
    document.getElementById('vote-con-both-times').innerText = votesStatistics.noPlayersWhoVotedConBothTimes;
    document.getElementById('vote-pro-then-con').innerText = votesStatistics.noPlayersWhoVotedProThenCon;
    document.getElementById('vote-con-then-pro').innerText = votesStatistics.noPlayersWhoVotedConThenPro;
}