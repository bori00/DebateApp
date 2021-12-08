let callFrame;

async function createCallFrame(userId) {
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('joined-meeting', handleJoinedMeeting)
        .on('left-meeting', handleLeftMeeting);

    await startDebate(userId);
}

async function startDebate(userId) {

    let room = await createRoom();
    if (!room) {
        console.error('room could not be created');
    }

    console.log(room);

    // generate a unique meeting token for the owner of the meeting with special privileges
    let requestTokenForOwner = await createOwnerMeetingToken(room.name, userId);
    let ownerToken = requestTokenForOwner.token;

    if (!ownerToken) {
        console.error('meeting token could not be generated');
    }

    const copyUrl = document.getElementById('copy-url');
    copyUrl.value = room.url;

    try {
        await callFrame.join({
            url: room.url,
            token: ownerToken,
            showLeaveButton: true,
            showFullscreenButton: true,
            showParticipantsBar: true,
        });
    } catch (e) {
        console.error(e);
    }

}

async function createRoom() {

    const newRoomEndpoint = DAILY_REST_DOMAIN + "/rooms";
    // room expires in 24 hours
    const exp = Math.round(Date.now() / 1000) + 60 * 60 * 24;

    const options = {
        properties: {
            enable_prejoin_ui: true,
            enable_video_processing_ui: true,
            enable_chat: true,
            start_video_off: true,
            start_audio_off: true,
            exp: exp,
            eject_at_room_exp: true,
        },
    };

    try {
        let response = await fetch(newRoomEndpoint, {
                method: 'POST',
                body: JSON.stringify(options),
                mode: 'cors',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${DAILY_API_KEY}`,
                }
            }),
            room = await response.json();
        return room;
    } catch (e) {
        console.error(e);
    }
}

async function createOwnerMeetingToken(roomName, userId) {
    const newMeetingTokenEndpoint = DAILY_REST_DOMAIN + "/meeting-tokens";

    const options = {
        properties: {
            room_name: roomName,
            is_owner: true,
            user_id: userId,
            enable_screenshare: true,
        }
    };

    try {
        let response = await fetch(newMeetingTokenEndpoint, {
                method: 'POST',
                body: JSON.stringify(options),
                mode: 'cors',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${DAILY_API_KEY}`,
                }
            }),
            token = await response.json();
        return token;
    } catch (e) {
        console.error(e);
    }
}

function copyUrl() {
    const url = document.getElementById('copy-url');
    const copyButton = document.getElementById('copy-url-button');
    url.select();
    document.execCommand('copy');
    copyButton.innerHTML = 'Copied!';
}

/* Event listener callbacks and helpers */

function toggleCopyUrl() {
    const copyUrl = document.getElementById('controls-copy-url');

    copyUrl.classList.toggle('hide');
}

function handleJoinedMeeting() {
    toggleCopyUrl();
}

function handleLeftMeeting() {
    toggleCopyUrl();
}