let callFrame, preparationRoomProTeam, preparationRoomContraTeam;

/**
 * Create a container for the video call frame and start the call.
 * @param userId = id of the currently logged in user, who starts the call
 * @returns {Promise<void>}
 */
async function createCallFrameAndStartCall(userId) {
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('joined-meeting', handleJoinedMeeting)
        .on('left-meeting', handleLeftMeeting);

    await startDebate(userId);
}

/**
 * Start a video call for the debate.
 * @param userId the id of the user who requested the call.
 * @returns {Promise<void>}
 */
async function startDebate(userId) {
    let room = await createRoom();


    callFrame.join({
            url: room.url,
            token: ownerToken,
            showLeaveButton: true,
            showFullscreenButton: true,
            showParticipantsBar: true,
        })
        .catch(console.log('failed to join meeting'));

}

/**
 * Join the call as owner with special privileges.
 * @param userId the id of the meeting owner
 * @param room the call room
 * @returns {Promise<void>}
 */
async function joinCallAsOwner(userId, room) {
    // generate a unique meeting token for the owner of the meeting with special privileges
    let requestTokenForOwner = await createOwnerMeetingToken(room.name, userId);
    let ownerToken = requestTokenForOwner.token;

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

/**
 * Generate a unique meeting token for the owner, allowing them to join the call with special privileges.
 * @param roomName the name of the room previously created by the owner
 * @param userId of the owner of the room
 * @returns {Promise<unknown>}
 */
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

/**
 * Start 2 separate calls for each team for the preparation phase.
 * @param debateSessionId the id of the currently ongoing debate session
 */
async function startPreparationPhase(debateSessionId) {

    preparationRoomProTeam = await createRoom();
    preparationRoomContraTeam = await createRoom();

    const requestBody = {
        "preparationPhaseUrlProTeam": preparationRoomProTeam.url,
        "preparationPhaseUrlContraTeam": preparationRoomContraTeam.url,
    };

    await updateDebateSession(debateSessionId, requestBody);

    toggleStartPreparation();
    toggleSwitchTeams();
}

function updateDebateSession(debateSessionId,requestBody) {
    const destEndpoint = "/process_start_preparation?debateSessionId="+debateSessionId;

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    return fetch(destEndpoint, {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            "charset": "UTF-8",
            [header]: token,
        },
        body: JSON.stringify(requestBody),
    })
        .catch(error => console.log('failed to create debate session: '+ error))
        .then(response => response.json())
        .catch(error => console.log('failed to parse response to json: ' + error));
}

function copyUrl() {
    const url = document.getElementById('copy-url');
    const copyButton = document.getElementById('copy-url-button');
    url.select();
    document.execCommand('copy');
    copyButton.innerHTML = 'Copied!';
}

async function joinPreparationPhaseOfProTeam(userId) {
    await joinCallAsOwner(userId, preparationRoomProTeam);
    toggleSwitchTeams();
}

async function joinPreparationPhaseOfContraTeam(userId) {
    await joinCallAsOwner(userId, preparationRoomContraTeam);
    toggleSwitchTeams();
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