let callFrame;
let preparationMeetingTeamPro, preparationMeetingTeamContra, activeDebateMeeting;
let isJudge;
let debateSessionId;
let endOfPreparationPhase = false;

const PREP_TIME_PHASE = "prep-time";
const SPEECH_TIME_PHASE = "speech-time";

async function joinDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('left-meeting', handleLeftMeeting)
        .on('joined-meeting', handleJoinedMeeting);

    let meetings = await getAllMeetingsOfDebateSession(debateSessionId);

    for ( let {meetingName, meetingUrl, meetingType} of meetings) {
        switch(meetingType) {
            case "ACTIVE" : {
                activeDebateMeeting = {meetingName, meetingUrl, meetingType}
                break;
            }
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

        let seconds = await getTimeIntervalForNextPhaseOfDebateSession();
        window.setTimeout(handleEndOfPreparationPhaseForJudge, seconds * 1000); // convert to millis

    } else {
        await subscribeToTimerNotificationSocket(PREP_TIME_PHASE);

        let debateSessionPlayer = await getDebateSessionPlayer();
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

async function getDebateSessionPlayer() {
    let debateSessionPlayerDestination = "/process_get_debate_session_player?debateSessionId="+debateSessionId;

    return await getDataFromServer(debateSessionPlayerDestination);
}

async function getTimeIntervalForNextPhaseOfDebateSession() {
    let destinationEndpoint = "/process_get_time_interval?debateSessionId="+debateSessionId;

    return await getDataFromServer(destinationEndpoint);
}

async function subscribeToTimerNotificationSocket(phase) {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-" + phase + "-times-up",
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

async function getAllMeetingsOfDebateSession(debateSessionId) {
    const allDebateMeetingsDestEndpoint = "/process_get_all_meetings?debateSessionId=" + debateSessionId;

    return await getDataFromServer(allDebateMeetingsDestEndpoint);
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

async function handleJoinedMeeting() {
    await updateParticipantsView();
}

function setElementVisibility(id, visible) {
    const element = document.getElementById(id);
    if (visible) {
        element.classList.remove('hide');
    } else {
        element.classList.add('hide');
    }
}

async function updateParticipantsView() {
    let currentDeputy = undefined; // todo fetch from server

    let debateSessionPlayer = await getDebateSessionPlayer();
    if(debateSessionPlayer === currentDeputy && debateSessionPlayer.playerRole !== "N") {
        highlightCurrentDeputy(debateSessionPlayer.team);
    }

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

function highlightCurrentDeputy(team) {
    const color = (team === "P")? 'green' : 'red';
    callFrame.updateParticipant('local', {
        styles: {
            cam: {
                div: {
                    visibility: 'visible',
                    'border-color': color,
                    'border-width': '10px',
                    'border-style': 'solid',
                    top: 20,
                    left: 20,
                    width: 20,
                    height: 20,
                },
            },
        },
    });
}


