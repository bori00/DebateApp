async function subscribeToDebateActivationSocket() {
    var socket = new SockJS('/secured/debates');
    var stompClient = Stomp.over(socket);
    var sessionId = "";

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-session-activated",
            function (activated) {
                window.location.href = "/go_to_active_debate"
            });
    });
}
