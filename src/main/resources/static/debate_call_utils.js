async function createDebateCallFrame(callWrapper) {
    return window.DailyIframe.createFrame(callWrapper, {
        iframeStyle: {
            height: '100%',
            width: '100%',
            aspectRatio: 16 / 9,
            border: '0',
            borderRadius: '12px',
        },
    });
}

async function createMeetingRoom() {
    const newRoomEndpoint = DAILY_REST_DOMAIN + "/rooms";

    const options = {
        properties: {
            enable_prejoin_ui: false,
            enable_video_processing_ui: true,
            enable_chat: true,
            start_video_off: true,
            start_audio_off: true,
            eject_at_room_exp: true,
        },
    };

    return fetch(newRoomEndpoint, {
        method: 'POST',
        body: JSON.stringify(options),
        mode: 'cors',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: `Bearer ${DAILY_API_KEY}`,
        }
    })
        .catch(error => console.log('failed to create room: ' + error))
        .then(response => response.json())
        .catch(error => console.log('failed to parse response to json: ' + error));
}

async function createMeetingToken(options) {
    const newMeetingTokenEndpoint = DAILY_REST_DOMAIN + "/meeting-tokens";

    return fetch(newMeetingTokenEndpoint, {
        method: 'POST',
        body: JSON.stringify(options),
        mode: 'cors',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: `Bearer ${DAILY_API_KEY}`,
        }
    })
        .catch(error => console.log('failed to create meeting token: ' + error))
        .then(response => response.json())
        .catch(error => console.log('failed to parse response to json: ' + error));
}

async function joinMeetingWithToken(isJudge, meetingUrl, meetingToken, userName) {
    callFrame.join({
        url: meetingUrl,
        token: meetingToken,
        userName: userName,
        showLeaveButton: !isJudge,
        showFullscreenButton: true,
        showParticipantsBar: true,
    })
        .catch(console.log('failed to join meeting, invalid token'));
}

function getParticipantPrivileges(roomName, isJudge) {
    return {
        properties: {
            room_name: roomName,
            is_owner: isJudge,
            enable_screenshare: false,
            start_video_off: true,
            start_audio_off: true,
        }
    };
}

async function getPresentParticipantsOfMeeting(roomName) {
    const participantsEndpoint = DAILY_REST_DOMAIN + "/presence";

    return fetch(participantsEndpoint, {
        method: 'GET',
        mode: 'cors',
        body: '',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: `Bearer ${DAILY_API_KEY}`,
        }
    })
        .catch(error => console.log('failed to fetch participants present in the meeting: ' + error))
        .then(response => response.json())
        .then(rooms => rooms[roomName]);
}