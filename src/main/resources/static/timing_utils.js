let countDownTime, remainingTime;
const refreshPeriod = 1000; // update the clock every second

async function getCurrentPhaseTimeInterval(debateSessionId) {
    let destinationEndpoint = "/process_get_time_interval?debateSessionId="+debateSessionId;

    return await getRequestToServer(destinationEndpoint);
}

async function getCurrentPhaseStartingTime(debateSessionId) {
    let destEndpoint = "/process_get_current_phase_starting_time?debateSessionId=" + debateSessionId;

    return await getRequestToServer(destEndpoint);
}

async function subscribeToTimerNotificationSocket(phase, onTimesUp) {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-" + phase + "-times-up", onTimesUp);
    });
}

async function displayCountDownTimerForPlayers(debateSessionId) {
    countDownTime = await getCountDownTime(debateSessionId);

    let countDownTimer = window.setInterval(function() {
        let {hours, minutes, seconds} = getTimeUnits();

        if(remainingTime >= 0) {
            displayTime(hours, minutes, seconds);
        }else{
            clearInterval(countDownTimer);
        }
    }, refreshPeriod);

    return countDownTimer;
}

async function isDebateClosed(debateSessionId) {
    let destEndpoint = "/process_is_debate_finished?debateSessionId=" + debateSessionId;

    return await getRequestToServer(destEndpoint);
}

async function displayCountDownTimerForJudge(debateSessionId, onTimesUp) {
    countDownTime = await getCountDownTime(debateSessionId);

    let countDownTimer = window.setInterval(async function() {
        let {hours, minutes, seconds} = getTimeUnits();
        let isClosed = await isDebateClosed(debateSessionId);

        if(remainingTime >= 0 && !isClosed) {
            displayTime(hours, minutes, seconds);
        }else{
            clearInterval(countDownTimer);
            await handleEndOfDebateSessionPhaseByJudge(debateSessionId, onTimesUp);
        }}, refreshPeriod);
}

async function handleEndOfDebateSessionPhaseByJudge(debateSessionId, onTimesUp) {
    let timerEndNotificationDestination = "/process_end_of_current_phase?debateSessionId="+debateSessionId;
    await postRequestToServer(timerEndNotificationDestination);
    await onTimesUp(true);
}

async function getCountDownTime(debateSessionId) {
    let startTime = new Date(await getCurrentPhaseStartingTime(debateSessionId)).getTime();
    let timeInterval = await getCurrentPhaseTimeInterval(debateSessionId); // in seconds
    return startTime + timeInterval * 1000;
}

function displayTime(hours, minutes, seconds) {
    document.getElementById('timer-clock').innerHTML = padNumber(hours, 2) + ':' + padNumber(minutes, 2) + ':' + padNumber(seconds, 2);
}

function getTimeUnits() {
    let currentTime = new Date().getTime(); // in millis
    remainingTime = countDownTime - currentTime;

    let hours = Math.floor(remainingTime / (1000 * 60 * 60));
    let minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
    let seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

    return {hours, minutes, seconds};
}

function padNumber(number, size) {
    number = number.toString();
    while (number.length < size) number = "0" + number;
    return number;
}