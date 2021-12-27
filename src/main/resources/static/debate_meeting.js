

async function getAllMeetingsOfDebateSession(debateSessionId) {
    const allDebateMeetingsDestEndpoint = "/get_all_meetings?debateSessionId=" + debateSessionId;

    return await getRequestToServer(allDebateMeetingsDestEndpoint);
}

async function getUserNameOfCurrentUser() {
    let destEndpoint = "/get_username_of_current_user";

    return await getRequestToServerWithResponseAsText(destEndpoint);
}

async function getBattleInformation(debateSessionId) {
    let destEndpoint = "/get_battle_information?debateSessionId=" + debateSessionId;

    return await getRequestToServer(destEndpoint);
}

async function getDebateSessionPlayer() {
    let debateSessionPlayerDestination = "/get_debate_session_player?debateSessionId=" + debateSessionId;

    return await getRequestToServer(debateSessionPlayerDestination);
}