
async function closeDebate(debateSessionId) {
    callFrame.leave();
    document.defaultView.location.href = "/process_close_debate";
}

async function subscribeToClosedDebateNotificationSocket() {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-closed", function(closed) {
            document.defaultView.alert('This debate has been closed by the judge.')
            callFrame.leave();
            document.defaultView.location.href = "/home";
        });
    });
}



