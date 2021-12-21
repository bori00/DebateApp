
async function updateDeputiesView() {
    let currentDeputy = undefined; // todo fetch from server

    let debateSessionPlayer = await getDebateSessionPlayer();

    // mute all participants except the current deputy: TODO
    if(debateSessionPlayer === currentDeputy) {
        callFrame.setLocalAudio(true);
    }else{
        callFrame.setLocalAudio(false);
    }

    // set frame color according to current team
    if(!isJudge) {
        const callWrapper = document.getElementById('wrapper');
        if(debateSessionPlayer.team === "P") {
            callWrapper.classList.add('pro-team');
        }else{
            callWrapper.classList.add('contra-team');
        }
    }
}