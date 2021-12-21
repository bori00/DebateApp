
async function closeDebate(debateSessionId) {
    let closeDebateNotificationDestination = "/process_close_debate?debateSessionId="+debateSessionId;
    await sendDataToServer(closeDebateNotificationDestination);
    onCloseDebateJudge();
}

async function subscribeToClosedDebateNotificationSocket() {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-closed", function(closed) {
            onCloseDebate();
        });
    });
}


function onCloseDebateJudge() {
    callFrame.leave();
    document.defaultView.location.href = "/home";
}

function onCloseDebate() {
    document.defaultView.alert('This debate has been closed by the judge.')
    callFrame.leave();
    document.defaultView.location.href = "/home";
}



