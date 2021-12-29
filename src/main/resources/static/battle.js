let callFrame, activeMeeting;
let isJudge, debateSessionId;
let battleInformation;
let countDownTimer;

const FINAL_DISCUSSIONS_PHASE = "FINAL_DISCUSSION";
const FINAL_VOTE_PHASE = "FINAL_VOTE";

async function joinActiveDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);
    callFrame
        .on('participant-joined', handleParticipantJoined)
        .on('participant-left', handleParticipantLeft);

    let meetings = await getAllMeetingsOfDebateSession(debateSessionId);
    activeMeeting = meetings.filter(meeting => meeting.meetingType === "ACTIVE")[0];
    let userName = await getUserNameOfCurrentUser();
    let meetingToken = await createMeetingToken(getParticipantPrivileges(activeMeeting.meetingName, isJudge));
    await joinMeetingWithToken(isJudge, activeMeeting.meetingUrl, meetingToken.token, userName);

    battleInformation = await getBattleInformation(debateSessionId);
    if (battleInformation.currentPhase !== FINAL_DISCUSSIONS_PHASE) {
        await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
    }
    if (!isJudge) {
        await subscribeToSkippedSpeechNotificationSocket();
    }
    await updateBattleUI();
}

async function subscribeToTimerNotificationForNextBattlePhase(debateSessionId) {
    if (isJudge) {
        countDownTimer = await displayCountDownTimerForJudge(debateSessionId, onBattleSpeechTimesUp);
    } else {
        await displayCountDownTimerForPlayers(debateSessionId);
        await subscribeToTimerNotificationSocket(battleInformation.currentPhase, onBattleSpeechTimesUp);
    }
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

/** callback functions for handling the end of a phase: either by end of timer or skip */

async function onBattleSpeechTimesUp() {
    window.alert("Times up! The " + battleInformation.currentPhase + " has ended!");
    await onNextPhaseTransition();
}

async function onSkip() {
    clearInterval(countDownTimer);
    showSkippedPhaseNotification();
    await onNextPhaseTransition();
}

/**
 * Only the judge can skip the current speech phase (for other participants the "Skip" button remains hidden)
 */
async function skipCurrentSpeech() {
    let destEndpoint = "/process_skip_speech?debateSessionId=" + debateSessionId;
    await postRequestToServer(destEndpoint, null);

    await onSkip();
}

/**
 * Update the view after moving to the next phase with the newly fetched battle information.
 * If the new phase is a timed phase, then start the timer and subscribe to the timer's notification socket.
 */
async function onNextPhaseTransition() {
    battleInformation = await getBattleInformation(debateSessionId);
    if (battleInformation.currentPhase !== FINAL_DISCUSSIONS_PHASE) {
        await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
    }
    await updateBattleUI();
}

/** helper functions */

/**
 * @returns true is there are any speakers currently present in the meeting
 */
async function areAnySpeakersPresent() {
    let presentParticipants = await getPresentParticipantsOfMeeting(activeMeeting.meetingName);
    return battleInformation.currentSpeakers.some((speaker) => presentParticipants.includes(speaker));
}

function isSpeechPhase() {
    return !(battleInformation.currentPhase === FINAL_VOTE_PHASE || battleInformation.currentPhase === FINAL_DISCUSSIONS_PHASE);
}

/** meeting event handler callbacks */

async function handleParticipantJoined(event) {
    await updateBattleUI();
    if (isJudge) {
        let participantWhoLeft = event.participant.user_name;
        let presentParticipants = await getPresentParticipantsOfMeeting(activeMeeting.meetingName);
        let anySpeakerPresent = battleInformation.currentSpeakers.some(speaker => speaker === participantWhoLeft || presentParticipants.includes(speaker));
        updateJudgeNotificationView(anySpeakerPresent);
    }
}

async function handleParticipantLeft(event) {
    await updateBattleUI();
    if (isJudge) {
        let participantWhoLeft = event.participant.user_name;
        let presentParticipants = await getPresentParticipantsOfMeeting(activeMeeting.meetingName);
        let anySpeakerPresent = battleInformation.currentSpeakers.some(speaker => speaker !== participantWhoLeft && presentParticipants.includes(speaker));
        updateJudgeNotificationView(anySpeakerPresent);
    }
}

/** update UI for judge and participants */

async function updateBattleUI() {
    let battleInformation = await getBattleInformation(debateSessionId);
    if (isJudge) {
        await updateJudgeView(battleInformation);
    } else {
        await updateParticipantsView(battleInformation);
    }
    await updateView(battleInformation);
}

async function updateJudgeView(battleInformation) {
    let anySpeakerPresent = await areAnySpeakersPresent();
    updateJudgeNotificationView(battleInformation, anySpeakerPresent);
    setElementVisibility('voting-lobby-judge', isJudge && (battleInformation.currentPhase === FINAL_VOTE_PHASE));
}

function updateJudgeNotificationView(battleInformation, anySpeakerPresent) {
    if (isSpeechPhase() && !anySpeakerPresent) {
        setElementVisibility('judge-control-skip-phase', true);
        document.getElementById('judge-control-skip-phase-message').innerText =
            "All the deputies who should speak in the " +
            battleInformation.currentPhase +
            " phase are missing. Would you like to skip this speech and go to the next one?";
    } else {
        setElementVisibility('judge-control-skip-phase', false);
    }
}

async function updateParticipantsView(battleInformation) {
    if (battleInformation.currentPhase === FINAL_VOTE_PHASE) {
        setElementVisibility('voting-panel', !battleInformation.hasVoted);
        setElementVisibility('voting-lobby', battleInformation.hasVoted);
        return;
    } else if (battleInformation.currentPhase === FINAL_DISCUSSIONS_PHASE) {
        setElementVisibility('voting-panel', false);
        setElementVisibility('voting-lobby', false);
        return;
    }
    // if current user is a speaker turn on its microphone, otherwise mute them and turn off their camera
    let userName = await getUserNameOfCurrentUser();
    if (battleInformation.currentSpeakers.includes(userName)) {
        callFrame.setLocalAudio(true);
    } else {
        callFrame.setLocalAudio(false);
        callFrame.setLocalVideo(false);
    }
}

async function updateView(battleInformation) {
    document.getElementById('current-phase').innerHTML = battleInformation.currentPhase;
    document.getElementById('instructions-item').innerText = battleInformation.instructions;
    document.getElementById('next-phase-item').innerText = battleInformation.nextPhase;
    document.getElementById('current-speakers-item').innerText = battleInformation.currentSpeakers.toString();
    document.getElementById('next-speakers-item').innerText = battleInformation.nextSpeakers.toString();

    let notificationPanel = document.getElementById('speech-notification');
    let notificationPanelMessage = document.getElementById('speech-notification-message');
    notificationPanel.classList.remove('alert-success');
    notificationPanel.classList.remove('alert-info');

    if (battleInformation.isSpeaker) {
        notificationPanel.classList.remove('hide');
        notificationPanel.classList.add('alert-success');
        notificationPanelMessage.innerText = "It's your turn to speak!";
    } else {
        if (battleInformation.isNextSpeaker) {
            notificationPanel.classList.remove('hide');
            notificationPanel.classList.add('alert-primary');
            notificationPanelMessage.innerText = "You are up next! Good luck!";
        } else {
            notificationPanel.classList.add('hide');
        }
    }

    window.setTimeout(function () {
        notificationPanel.classList.add('hide');
    }, 10000);

    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy1Pro, true, 'deputy1-pro');
    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy2Pro, true, 'deputy2-pro');
    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy1Con, false, 'deputy1-con');
    highlightCurrentSpeakers(battleInformation.currentSpeakers, battleInformation.deputy2Con, false, 'deputy2-con');

    if (battleInformation.currentPhase === FINAL_DISCUSSIONS_PHASE) {
        setElementVisibility('timer-clock', false);
        await showFinalVotesStatistics();
    }
}

function showSkippedPhaseNotification() {
    let notification = document.getElementById('notification-skip-phase');
    notification.classList.remove('hide');
    document.getElementById('notification-skip-phase-message').innerText =
        "Skipped phase " +
        battleInformation.currentPhase +
        " because all the deputies who should speak are missing.";

    window.setTimeout(function () {
        notification.classList.add('hide');
    }, 10000);
}

function highlightCurrentSpeakers(currentSpeakers, speaker, isProTeam, elemId) {
    let speakerElem = document.getElementById(elemId);
    let attribute = (isProTeam) ? 'table-success' : 'table-danger';
    speakerElem.classList.remove(attribute);
    if (currentSpeakers.includes(speaker)) {
        speakerElem.classList.add(attribute);
    }
}






