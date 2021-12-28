let callFrame, activeMeeting;
let isJudge, debateSessionId;
let battleInformation;

const FINAL_DISCUSSIONS_PHASE = "FINAL_DISCUSSION";

async function joinActiveDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    let meetings = await getAllMeetingsOfDebateSession(debateSessionId);
    activeMeeting = meetings.filter(meeting => meeting.meetingType === "ACTIVE")[0];
    let userName = await getUserNameOfCurrentUser();
    let meetingToken = await createMeetingToken(getParticipantPrivileges(activeMeeting.meetingName, isJudge));

    await joinMeetingWithToken(isJudge, activeMeeting.meetingUrl, meetingToken.token, userName);

    battleInformation = await getBattleInformation(debateSessionId);

    await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
}

async function subscribeToTimerNotificationForNextBattlePhase(debateSessionId) {
    if (battleInformation.currentPhase === FINAL_DISCUSSIONS_PHASE) {
        setElementVisibility('timer-clock', false);
        return;
    }
    if (isJudge) {
        await displayCountDownTimerForJudge(debateSessionId, onBattleSpeechTimesUp);
    } else {
        await displayCountDownTimerForPlayers(debateSessionId);
        await subscribeToTimerNotificationSocket(battleInformation.currentPhase, onBattleSpeechTimesUp);
    }
}

async function onBattleSpeechTimesUp() {
    window.alert("Times up! The " + battleInformation.currentPhase + " has ended!");
    battleInformation = await getBattleInformation(debateSessionId);
    updateView(battleInformation);

    if (!areAllSpeakersPresent() && isJudge) {
        let destEndpoint = "/process_skip_speech?debateSessionId=" + debateSessionId;
        await postRequestToServer(destEndpoint, null);
    }
    await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
}

async function subscribeToSkippedSpeechNotificationSocket() {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");
    console.log("Subscribed")

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-skip-speech", onSkip);
    });
}

async function onSkip() {
    window.alert("Skipped phase" + battleInformation.currentPhase + " because the deputies who should speak are missing.");
    battleInformation = await getBattleInformation(debateSessionId);
    updateView(battleInformation);
}

function areAllSpeakersPresent() {
    let presentParticipants = getPresentParticipantsOfMeeting(activeMeeting.roomName);
    return battleInformation.currentSpeakers.every((speaker) => presentParticipants.contains(speaker));
}

function updateView(battleInformation) {
    document.getElementById('current-phase').innerHTML = battleInformation.currentPhase;
    document.getElementById('instructions-item').innerText = battleInformation.instructions;
    document.getElementById('next-phase-item').innerText = battleInformation.nextPhase;
    document.getElementById('current-speakers-item').innerText = battleInformation.currentSpeakers.toString();
    document.getElementById('next-speakers-item').innerText = battleInformation.nextSpeakers.toString();

    let notificationPanel = document.getElementById('notification');
    if (battleInformation.isSpeaker) {
        notificationPanel.classList.remove('hide');
        notificationPanel.classList.add('alert-success');
        notificationPanel.innerText = "It's your turn to speak!";
    } else {
        if (battleInformation.isNextSpeaker) {
            notificationPanel.classList.remove('hide');
            notificationPanel.classList.add('alert-info');
            notificationPanel.innerText = "You are up next! Good luck!";
        } else {
            notificationPanel.classList.add('hide');
            notificationPanel.classList.remove('alert-success');
            notificationPanel.classList.remove('alert-info');
        }
    }

    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy1Pro, true, 'deputy1-pro');
    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy2Pro, true, 'deputy2-pro');
    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy1Con, false, 'deputy1-con');
    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy2Con, false, 'deputy2-con');
}

function highlightCurrentSpeakers(currentSpeakers, speaker, isProTeam, elemId) {
    document.getElementById(elemId).classList.remove((isProTeam) ? 'pro-team-member' : 'contra-team-member');
    if (currentSpeakers.includes(speaker)) {
        document.getElementById(elemId).classList.add((isProTeam) ? 'pro-team-member' : 'contra-team-member');
    }
}



