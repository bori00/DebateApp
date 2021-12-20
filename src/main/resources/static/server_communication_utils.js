

async function getDataFromServer(destEndpoint) {
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

async function sendDataToServer(destEndpoint, body) {
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