let callFrame, activeMeeting;
let isJudge, debateSessionId;
let battleInformation;
let countDownTimer;

const FINAL_DISCUSSIONS_PHASE = "FINAL_DISCUSSION";

async function joinActiveDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('joined-meeting', handleJoinedMeeting)
        .on('left-meeting', handleLeftMeeting);

    let meetings = await getAllMeetingsOfDebateSession(debateSessionId);
    activeMeeting = meetings.filter(meeting => meeting.meetingType === "ACTIVE")[0];
    let userName = await getUserNameOfCurrentUser();
    let meetingToken = await createMeetingToken(getParticipantPrivileges(activeMeeting.meetingName, isJudge));

    await joinMeetingWithToken(isJudge, activeMeeting.meetingUrl, meetingToken.token, userName);

    battleInformation = await getBattleInformation(debateSessionId);
    await updateUI();

    await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
    await subscribeToSkippedSpeechNotificationSocket();
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

async function onBattleSpeechTimesUp() {
    window.alert("Times up! The " + battleInformation.currentPhase + " has ended!");
    await beforeGoToNextSpeech();
}

async function onSkip() {
    let participantStatusNotification = document.getElementById('participants-notification');
    participantStatusNotification.classList.remove('hide');
    participantStatusNotification.innerText = "Skipped phase " + battleInformation.currentPhase + " because all the deputies who should speak are missing.";

    window.setTimeout(function() {
        participantStatusNotification.classList.add('hide');
    }, 10000);

    await beforeGoToNextSpeech();
}

async function beforeGoToNextSpeech() {
    if (battleInformation.nextPhase === FINAL_DISCUSSIONS_PHASE) {
        setElementVisibility('timer-clock', false);
        return;
    }
    battleInformation = await getBattleInformation(debateSessionId);
    await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
    await updateUI();
}

async function skipCurrentSpeech() {
    clearInterval(countDownTimer);
    let destEndpoint = "/process_skip_speech?debateSessionId=" + debateSessionId;
    await postRequestToServer(destEndpoint, null);

    setElementVisibility('judge-notification', false);
    await beforeGoToNextSpeech();
}

async function areAnySpeakersPresent() {
    let presentParticipants = await getPresentParticipantsOfMeeting(activeMeeting.meetingName);
    return battleInformation.currentSpeakers.some((speaker) => presentParticipants.includes(speaker));
}

function updateView(battleInformation) {
    document.getElementById('current-phase').innerHTML = battleInformation.currentPhase;
    document.getElementById('instructions-item').innerText = battleInformation.instructions;
    document.getElementById('next-phase-item').innerText = battleInformation.nextPhase;
    document.getElementById('current-speakers-item').innerText = battleInformation.currentSpeakers.toString();
    document.getElementById('next-speakers-item').innerText = battleInformation.nextSpeakers.toString();

    let notificationPanel = document.getElementById('notification');
    notificationPanel.classList.remove('alert-success');
    notificationPanel.classList.remove('alert-info');

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

async function handleJoinedMeeting() {
    await updateUI();
}

async function handleLeftMeeting() {
    await updateUI();
}

async function updateUI() {
    if(isJudge) {
        await updateJudgeView();
    }else {
        await updateParticipants();
    }
    updateView(battleInformation);
}

async function updateJudgeView() {
    let allSpeakersPresent = await areAnySpeakersPresent();

    if (!allSpeakersPresent) {
        setElementVisibility('judge-notification', true);
        document.getElementById('judge-notification-message').innerText = "All the deputies who should speak in the " + battleInformation.currentPhase + " phase are missing. Would you like to skip this speech and go to the next one?";
    }else{
        setElementVisibility('judge-notification', false);
    }
}

async function updateParticipants() {
    // if current user is a speaker turn on its microphone, otherwise mute them and turn off their camera
    let userName = await getUserNameOfCurrentUser();
    if(battleInformation.currentSpeakers.includes(userName)) {
        callFrame.setLocalAudio(true);
    }else{
        callFrame.setLocalAudio(false);
        callFrame.setLocalVideo(false);
    }
}

