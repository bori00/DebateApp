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


    callFrame.join({
            url: room.url,
            token: ownerToken,
            showLeaveButton: true,
            showFullscreenButton: true,
            showParticipantsBar: true,
        })
        .catch(console.log('failed to join meeting'));

}

async function createOwnerMeetingToken(roomName, userId) {
    const options = {
        properties: {
            room_name: roomName,
            is_owner: true,
            user_id: userId,
            enable_screenshare: true,
        }
    };

    return await createMeetingToken(options);
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