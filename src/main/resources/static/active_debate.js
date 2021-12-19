let callFrame;
let preparationMeetingTeamPro, preparationMeetingTeamContra, activeDebateMeeting;
let isJudge;
let debateSessionId;
let endOfPreparationPhase = false;

async function joinDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('left-meeting', handleLeftMeeting);

    activeDebateMeeting = await getMeeting(debateSessionId, "ACTIVE");
    preparationMeetingTeamPro = await getMeeting(debateSessionId, "PREPARATION_PRO");
    preparationMeetingTeamContra = await getMeeting(debateSessionId, "PREPARATION_CONTRA");

    let meetingToken;

    if (isJudge) {
        setElementVisibility("join-preparation-team-pro", true);
        setElementVisibility("join-preparation-team-contra", true);

        let seconds = await getTimeIntervalForNextPhaseOfDebateSession();
        window.setTimeout(handleEndOfPreparationPhaseForJudge, seconds * 1000); // convert to millis

    } else {
        await subscribeToPreparationTimerNotificationSocket();

        let debateSessionPlayerDestination = "/process_get_debate_session_player?debateSessionId="+debateSessionId;
        let debateSessionPlayer = await getDataFromServer(debateSessionPlayerDestination);

        let teamPreparationMeeting;

        if(debateSessionPlayer.team === "P") {
            callWrapper.classList.add('pro-team');
            teamPreparationMeeting = preparationMeetingTeamPro;
        }else {
            callWrapper.classList.add('contra-team');
            teamPreparationMeeting = preparationMeetingTeamContra;
        }
        meetingToken = await createMeetingToken(getPlayerPrivileges(teamPreparationMeeting.meetingName));
        await joinMeetingWithToken(teamPreparationMeeting.meetingUrl, meetingToken.token);
    }
}

async function getTimeIntervalForNextPhaseOfDebateSession() {
    let destinationEndpoint = "/process_get_time_interval?debateSessionId="+debateSessionId;
    return await getDataFromServer(destinationEndpoint);
}

async function subscribeToPreparationTimerNotificationSocket() {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-prep_time-times-up",
            function (timesUp) {
               onPreparationTimesUp();
            });
    });
}

async function onPreparationTimesUp() {
    window.alert("Times up! The preparation for the debate has ended!");
    endOfPreparationPhase = true;
    await leaveMeeting();
}

async function handleEndOfPreparationPhaseForJudge() {
    let timerEndNotificationDestination = "/process_end_of_preparation_phase";
    await sendDataToServer(timerEndNotificationDestination);
    await onPreparationTimesUp();
}

async function joinPreparationMeetingOfTeamPro() {
    await leaveMeeting();

    setElementVisibility("join-preparation-team-pro", false);
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.add('pro-team');

    let meetingToken = await createMeetingToken(getJudgePrivileges(preparationMeetingTeamPro.meetingName));
    await joinMeetingWithToken(preparationMeetingTeamPro.meetingUrl, meetingToken.token);

}

async function joinPreparationMeetingOfTeamContra() {
    await leaveMeeting();

    setElementVisibility("join-preparation-team-contra", false);
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.add('contra-team');

    let meetingToken = await createMeetingToken(getJudgePrivileges(preparationMeetingTeamPro.meetingName));
    await joinMeetingWithToken(preparationMeetingTeamContra.meetingUrl, meetingToken.token);
}

async function getMeeting(debateSessionId, meetingType) {
    const debateMeetingDestEndpoint = "/process_get_meeting?debateSessionId=" + debateSessionId + "&meetingType=" + meetingType;

    return await getDataFromServer(debateMeetingDestEndpoint);
}

async function joinMeetingWithToken(meetingUrl, meetingToken) {
    callFrame.join({
        url: meetingUrl,
        token: meetingToken,
        showLeaveButton: true,
        showFullscreenButton: true,
        showParticipantsBar: true,
    })
        .catch(console.log('failed to join meeting, invalid token'));
}

function getJudgePrivileges(roomName) {
    return {
        properties: {
            room_name: roomName,
            is_owner: true,
            enable_screenshare: true,
        }
    };
}

function getPlayerPrivileges(roomName) {
    return {
        properties: {
            room_name: roomName,
            is_owner: false,
            enable_screenshare: false,
        }
    };
}

async function leaveMeeting() {
    callFrame.leave();

    if(endOfPreparationPhase) {
        let privileges = (isJudge) ? getJudgePrivileges(activeDebateMeeting.meetingName): getPlayerPrivileges(activeDebateMeeting.meetingName);
        let meetingToken = await createMeetingToken(privileges);

        await joinMeetingWithToken(activeDebateMeeting.meetingUrl, meetingToken.token);
    }
}

function handleLeftMeeting() {
    setElementVisibility("join-preparation-team-pro", isJudge && !endOfPreparationPhase);
    setElementVisibility("join-preparation-team-contra", isJudge && !endOfPreparationPhase);
    const callWrapper = document.getElementById('wrapper');
    callWrapper.classList.remove('pro-team');
    callWrapper.classList.remove('contra-team');
}

function setElementVisibility(id, visible) {
    const element = document.getElementById(id);
    if (visible) {
        element.classList.remove('hide');
    } else {
        element.classList.add('hide');
    }
}


