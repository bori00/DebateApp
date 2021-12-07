/*
 * Main functionalities:
 * - setting up the call frame for the meeting room
 * - joining a call (room)
 */

let callFrame;

async function createCallFrame() {
    const callWrapper = document.getElementById('wrapper');
    callFrame = await window.DailyIframe.createFrame(callWrapper, {
        iframeStyle: {
            height: '100%',
            width: '100%',
            aspectRatio: 16 / 9,
            minWidth: '500px',
            maxWidth: '920px',
            border: '0',
            borderRadius: '12px',
        },
    });

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

    try {
        await callFrame.join({
            url: url,
            showLeaveButton: true,
            showParticipantsBar: true,
        });
    } catch (e) {
        toggleUrlInputPrompt();
        console.error(e);
    }
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
