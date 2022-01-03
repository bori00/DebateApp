async function closeDebate() {
    if (confirm("Are you sure that you want to close this debate session? This action cannot be" +
        " undone!")) {
        const url = new URL("/close_debate", document.URL);

        postRequestToServer(url, {})
            .then(response => {
                return response.json();
            })
            .catch(error => console.log('failed to parse response: ' + error))
            .then(response => {
                handleOngoingDebateRequestResponse(response);
            });
    }
}

async function subscribeToClosedDebateNotificationSocket() {
    const socket = new SockJS('/secured/debates');
    const stompClient = Stomp.over(socket);

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-closed", function (closed) {
            document.defaultView.setTimeout(onDebateClosed,1000);
        });
    });
}

function onDebateClosed() {
    if (typeof callFrame !== 'undefined') {
        callFrame.leave();
    }
    if (typeof countDownTimer !== 'undefined') {
        clearInterval(countDownTimer);
    }
    document.defaultView.alert('This debate has been closed by the judge.')
    document.defaultView.location.href = "/go_to_home";
}


