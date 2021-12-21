
async function updateDeputiesView() {
    let currentDeputy = undefined; // todo fetch from server

    let debateSessionPlayer = await getDebateSessionPlayer();

    // mute all participants except the current deputy: TODO
    if(debateSessionPlayer === currentDeputy) {
        callFrame.setLocalAudio(true);
    }else{
        callFrame.setLocalAudio(false);
    }
}