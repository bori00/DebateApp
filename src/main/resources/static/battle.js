let callFrame, activeMeeting, currentPhase, isJudge, debateSessionId;

const FINAL_DISCUSSIONS_PHASE = "FINAL_DISCUSSION";

async function joinActiveDebateMeeting(isParticipantJudge, currentDebateSessionId) {
    isJudge = isParticipantJudge;
    debateSessionId = currentDebateSessionId;
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('left-meeting', handleLeftMeeting);

    let meetings = await getAllMeetingsOfDebateSession(debateSessionId);
    activeMeeting = meetings.filter(meeting => meeting.meetingType === "ACTIVE")[0];
    let userName = await getUserNameOfCurrentUser();
    let meetingToken = await createMeetingToken(getParticipantPrivileges(activeMeeting.meetingName, isJudge));

    await joinMeetingWithToken(isJudge, activeMeeting.meetingUrl, meetingToken.token, userName);

    let battleInformation = await getBattleInformation(debateSessionId);
    currentPhase = battleInformation.currentPhase;

    await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
}

async function subscribeToTimerNotificationForNextBattlePhase(debateSessionId) {
    if (currentPhase === FINAL_DISCUSSIONS_PHASE) {
        setElementVisibility('timer-clock', false);
        return;
    }
    if (isJudge) {
        await displayCountDownTimerForJudge(debateSessionId, onBattleSpeechTimesUp);
    } else {
        await displayCountDownTimerForPlayers(debateSessionId);
        await subscribeToTimerNotificationSocket(currentPhase, onBattleSpeechTimesUp);
    }
}

async function subscribeToParticipantLeftDebateSocket() {
    var socket = new SockJS('/secured/debates');
    var stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-participant-left",
            function (left) {
                updateTeamMembersList();
            });
    });
}

function updateView(battleInformation) {
    document.getElementById('current-phase').innerHTML = currentPhase;
    document.getElementById('instructions-item').innerText = battleInformation.instructions;
    document.getElementById('next-phase-item').innerText = battleInformation.nextPhase;
    document.getElementById('current-speakers-item').innerText = battleInformation.currentSpeakers.toString();
    document.getElementById('next-speakers-item').innerText = battleInformation.nextSpeakers.toString();

    //update current speakers
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

async function onBattleSpeechTimesUp() {
    window.alert("Times up! The " + currentPhase + " has ended!");
    let battleInformation = await getBattleInformation(debateSessionId);
    currentPhase = battleInformation.currentPhase;
    updateView(battleInformation);
    await subscribeToTimerNotificationForNextBattlePhase(debateSessionId);
}

async function handleLeftMeeting() {
    //todo: isSpeaker => show alert and then go to next phase(skip)

}

async function updateTeamMembersList() {
    let battleInformation = await getBattleInformation(debateSessionId);

    updateList('pro-team-members-list', battleInformation.proTeamMembers, true);
    updateList('contra-team-members-list', battleInformation.conTeamMembers, false);
}

function updateList(listId, elements, isProTeam) {
    let list = document.getElementById(listId);
    list.innerHTML = ''; //remove all previous elements of the list

    for (let elem of elements) {
        let listElem = document.createElement("li");
        listElem.classList.add("list-group-item");
        listElem.classList.add((isProTeam) ? "pro-team-member" : "contra-team-member");
        listElem.innerText = elem;
        list.appendChild(listElem);
    }
}