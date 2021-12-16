function copyDebateCode() {
    const code = document.getElementById('copy-code');
    const copyButton = document.getElementById('copy-code-button');
    code.select();
    document.execCommand('copy');
    copyButton.innerHTML = 'Copied!';
}

async function subscribeToParticipantAnnouncementSocket() {
    var socket = new SockJS('/secured/debates');
    var stompClient = Stomp.over(socket);
    var sessionId = "";

    console.log("Socket initialized");

    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/queue/debate-session-participants-status",
            function (participantsStatusResponse) {
                const participantsStatus = JSON.parse(participantsStatusResponse.body);
                console.log("New participants status: " + participantsStatus);
                updateUIWithNewParticipantsStatus(participantsStatus);
            });
    });
}

function updateUIWithNewParticipantsStatus(participantsStatus) {
    $('#noWaitingToJoinParticipantsTd')[0].innerText=participantsStatus.noWaitingToJoinParticipants;
    $('#noProParticipantsTd')[0].innerText=participantsStatus.noProParticipants;
    $('#noConParticipantsTd')[0].innerText=participantsStatus.noConParticipants;
    if (participantsStatus.canActivateSession) {
        $('#activate-debate-session-button')[0].removeAttribute('disabled');
    } else {
        $('#activate-debate-session-button')[0].setAttribute('disabled', '');
    }
}

function activateDebateSession() {
    console.log("Activate...");
}