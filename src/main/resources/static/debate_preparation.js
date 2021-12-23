let callFrame;
let preparationMeetingTeamPro, preparationMeetingTeamContra;
let userName, isJudge;
let debateSessionId;

const PREP_TIME_PHASE = "prep-time";

async function joinDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    userName = await getUserNameOfCurrentUser();
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('left-meeting', handleLeftMeeting)
        .on('joined-meeting', handleJoinedMeeting);

    let meetings = await getAllMeetingsOfDebateSession(debateSessionId);

    for (let {meetingName, meetingUrl, meetingType} of meetings) {
        switch (meetingType) {
            case "PREPARATION_PRO" : {
                preparationMeetingTeamPro = {meetingName, meetingUrl, meetingType}
                break;
            }
            case "PREPARATION_CONTRA" : {
                preparationMeetingTeamContra = {meetingName, meetingUrl, meetingType}
                break;
            }
        }
    }

    let meetingToken;

    if (isJudge) {
        setElementVisibility("join-preparation-team-pro", true);
        setElementVisibility("join-preparation-team-contra", true);

        await displayCountDownTimerForJudge(debateSessionId, onPreparationTimesUp);
    } else {
        let debateSessionPlayer = await getDebateSessionPlayer();
        let teamPreparationMeeting;

        if (debateSessionPlayer.team === "P") {
            callWrapper.classList.add('pro-team');
            teamPreparationMeeting = preparationMeetingTeamPro;
        } else {
            callWrapper.classList.add('contra-team');
            teamPreparationMeeting = preparationMeetingTeamContra;
        }
        meetingToken = await createMeetingToken(getParticipantPrivileges(teamPreparationMeeting.meetingName, userName, isJudge));
        await joinMeetingWithToken(isJudge, teamPreparationMeeting.meetingUrl, meetingToken.token);

        await displayCountDownTimerForPlayers(debateSessionId);
        await subscribeToTimerNotificationSocket(PREP_TIME_PHASE, onPreparationTimesUp);
    }
}

async function getDebateSessionPlayer() {
    let debateSessionPlayerDestination = "/process_get_debate_session_player?debateSessionId=" + debateSessionId;

    return await getRequestToServer(debateSessionPlayerDestination);
}

async function onPreparationTimesUp(timesUp) {
    await leaveMeeting();
    window.setTimeout(handleEndOfPreparationForParticipant,1000);
}

function handleEndOfPreparationForParticipant() {
    window.alert("Times up! The preparation for the debate has ended!");
    window.location.href = "/go_to_deputy_selection";
}

async function joinPreparationMeetingOfTeamPro() {
    await leaveMeeting();

    setElementVisibility("join-preparation-team-pro", false);
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.add('pro-team');

    let meetingToken = await createMeetingToken(getParticipantPrivileges(preparationMeetingTeamPro.meetingName, userName, isJudge));
    await joinMeetingWithToken(isJudge, preparationMeetingTeamPro.meetingUrl, meetingToken.token);
}

async function joinPreparationMeetingOfTeamContra() {
    await leaveMeeting();

    setElementVisibility("join-preparation-team-contra", false);
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.add('contra-team');

    let meetingToken = await createMeetingToken(getParticipantPrivileges(preparationMeetingTeamPro.meetingName, userName, isJudge));
    await joinMeetingWithToken(isJudge, preparationMeetingTeamContra.meetingUrl, meetingToken.token);
}

async function getAllMeetingsOfDebateSession(debateSessionId) {
    const allDebateMeetingsDestEndpoint = "/process_get_all_meetings?debateSessionId=" + debateSessionId;

    return await getRequestToServer(allDebateMeetingsDestEndpoint);
}

async function getUserNameOfCurrentUser() {
    let destEndpoint = "/process_get_username_of_current_user";

    return await getRequestToServer(destEndpoint);
}

async function updateParticipantsView() {
    // set frame color according to current team
    if (!isJudge) {
        let debateSessionPlayer = await getDebateSessionPlayer();
        const callWrapper = document.getElementById('wrapper');
        if (debateSessionPlayer.team === "P") {
            callWrapper.classList.add('pro-team');
        } else {
            callWrapper.classList.add('contra-team');
        }
    }
}

function handleLeftMeeting() {
    setElementVisibility("join-preparation-team-pro", isJudge);
    setElementVisibility("join-preparation-team-contra", isJudge);

    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.remove('pro-team');
    callWrapper.classList.remove('contra-team');
}

async function leaveMeeting() {
    callFrame.leave();
}

async function handleJoinedMeeting() {
    await updateParticipantsView();
}
