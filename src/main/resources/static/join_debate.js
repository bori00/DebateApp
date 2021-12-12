/*
 * Main functionalities:
 * - setting up the call frame for the meeting room
 * - joining a call (room)
 */

let callFrame;

async function createCallFrame() {
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame.on('joined-meeting', handleJoinedMeeting)
        .on('left-meeting', handleLeftMeeting);

    const callURL = document.getElementById('url-input');
    const joinButton = document.getElementById('join-call');
    callURL.addEventListener('input', () => {
        joinButton.toggleAttribute('disabled');
        if (callURL.checkValidity()) {
            joinButton.toggleAttribute('disabled');
        }
    });
}

async function joinDebate() {
    const url = document.getElementById('url-input').value;

    callFrame.join({
            url: url,
            showLeaveButton: true,
            showParticipantsBar: true,
        })
        .catch(error => {
            console.log('failed to join debate: '+ error);
            toggleUrlInputPrompt();
        });
}

/* Event listener callbacks and helpers */

/**
 * Hide the url input form after joining the meeting, and show it again after leaving the call.
 */
function toggleUrlInputPrompt() {
    const inputForm = document.getElementById('url-input-form');

    inputForm.classList.toggle('hide');
}

function handleJoinedMeeting() {
    toggleUrlInputPrompt();
}

function handleLeftMeeting() {
    toggleUrlInputPrompt();
    const callURL = document.getElementById('url-input');
    callURL.setAttribute('placeholder', 'Enter room URL...')
}
