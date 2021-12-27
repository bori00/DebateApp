

async function getRequestToServer(destEndpoint) {
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

async function postRequestToServer(destEndpoint, body) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    return fetch(destEndpoint, {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            "charset": "UTF-8",
            [header]: token,
        },
        body: JSON.stringify(body),
    })
        .catch(error => console.log('failed to fetch meeting: ' + error))
}

function handleOngoingDebateRequestResponse(response) {
    if (response.success) {
        if (response.debatePhaseRedirectNeeded) {
            window.location.href = "/go_to_ongoing_debates_current_phase"
        }
    } else {
        window.alert(response.errorMsg)
    }
}