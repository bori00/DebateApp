let callFrame;

async function joinDebateMeeting(userId, isJudge, debateSessionId) {
    const callWrapper = document.getElementById('wrapper');
    callFrame = await createDebateCallFrame(callWrapper);

    callFrame
        .on('joined-meeting', handleJoinedMeeting)
        .on('left-meeting', handleLeftMeeting);

    console.log("id="+debateSessionId);
    let meeting = await getDebateMeeting(debateSessionId, "ACTIVE");
    console.log("meeting name="+meeting.meetingName);
    console.log("meeting url="+meeting.meetingUrl);

    let requestToken;

    if(isJudge) {
        requestToken = await createMeetingToken(getJudgePrivileges(meeting.meetingName, userId));
    }else{
        requestToken = await createMeetingToken(getPlayerPrivileges(meeting.meetingName, userId));
    }
    await joinMeetingWithToken(meeting, requestToken.token);
}

async function getDebateMeeting(debateSessionId, meetingType) {
    const destEndpoint = "/process_get_meeting?debateSessionId="+debateSessionId+"&meetingType="+meetingType;
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    return fetch(destEndpoint, {
        method: 'GET',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            "charset": "UTF-8",
            [header]: token,
        },
    })
        .catch(error => console.log('failed to fetch meeting: ' + error))
        .then(response => response.json())
        .catch(error => console.log('failed to parse json: ' + error))
}

async function joinMeetingWithToken(meeting, meetingToken) {
    callFrame.join({
        url: meeting.meetingUrl,
        token: meetingToken,
        showLeaveButton: true,
        showFullscreenButton: true,
        showParticipantsBar: true,
    })
        .catch(console.log('failed to join meeting, invalid token'));
}

function getJudgePrivileges(roomName, userId) {
    return {
        properties: {
            room_name: roomName,
            is_owner: true,
            user_id: userId,
            enable_screenshare: true,
        }
    };
}

function getPlayerPrivileges(roomName, userId) {
     return {
        properties: {
            room_name: roomName,
            is_owner: false,
            user_id: userId,
            enable_screenshare: false,
        }
    };
}

function handleJoinedMeeting() {
    //todo
}

function handleLeftMeeting() {
    //todo
}
