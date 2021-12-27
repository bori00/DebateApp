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

    if (!isJudge) {
        let debateSessionPlayer = await getDebateSessionPlayer();
        let teamPreparationMeeting;

        if (debateSessionPlayer.team === "P") {
            callWrapper.classList.add('pro-team');
            teamPreparationMeeting = preparationMeetingTeamPro;
        } else {
            callWrapper.classList.add('contra-team');
            teamPreparationMeeting = preparationMeetingTeamContra;
        }
        let meetingToken = await createMeetingToken(getParticipantPrivileges(teamPreparationMeeting.meetingName, isJudge));
        await joinMeetingWithToken(isJudge, teamPreparationMeeting.meetingUrl, meetingToken.token, userName);
    }
}

async function subscribeToTimerNotificationForPreparationPhase(isJudge, debateSessionId) {
    if (isJudge) {
        await displayCountDownTimerForJudge(debateSessionId, onPreparationTimesUp);
    }else{
        await displayCountDownTimerForPlayers(debateSessionId);
        await subscribeToTimerNotificationSocket(PREP_TIME_PHASE, onPreparationTimesUp);
    }
}

async function onPreparationTimesUp(timesUp) {
    await leaveMeeting();
    window.setTimeout(handleEndOfPreparationForParticipant,1000);
}

function handleEndOfPreparationForParticipant() {
    window.alert("Times up! The preparation for the debate has ended!");
    window.location.href = "/go_to_ongoing_debates_current_phase";
}

async function joinPreparationMeetingOfTeamPro() {
    await leaveMeeting();

    toggleElementVisibility("join-preparation-team-pro");
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.add('pro-team');

    let meetingToken = await createMeetingToken(getParticipantPrivileges(preparationMeetingTeamPro.meetingName, isJudge));
    await joinMeetingWithToken(isJudge, preparationMeetingTeamPro.meetingUrl, meetingToken.token, userName);
}

async function joinPreparationMeetingOfTeamContra() {
    await leaveMeeting();

    toggleElementVisibility("join-preparation-team-contra");
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.add('contra-team');

    let meetingToken = await createMeetingToken(getParticipantPrivileges(preparationMeetingTeamPro.meetingName, isJudge));
    await joinMeetingWithToken(isJudge, preparationMeetingTeamContra.meetingUrl, meetingToken.token, userName);
}

function handleLeftMeeting() {
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.remove('pro-team');
    callWrapper.classList.remove('contra-team');
}

async function leaveMeeting() {
    callFrame.leave();
    if(isJudge) {
        setElementVisibility("join-preparation-team-pro", true);
        setElementVisibility("join-preparation-team-contra", true);
    }
}

async function handleJoinedMeeting() {
    await updateParticipantsView();
}

function toggleElementVisibility(id) {
    document.getElementById(id).classList.toggle('hide');
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