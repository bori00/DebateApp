function onDeleteDebateTemplateButtonPushed(debateTemplateId) {

    if (confirm('Are you sure you want to delete this debate template? This operation cannot be' +
        ' undone.')) {
        deleteDebateTemplate(debateTemplateId)
    }
}

function deleteDebateTemplate(debateTemplateId) {
    const debateTemplateInfo = {debateTemplateId: debateTemplateId};
    var url = new URL("/process_debate_template_deletion", document.URL);

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    return fetch(url, {
                method: 'POST',
                headers: {
                    [header]: token,
                    "charset": "UTF-8",
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(debateTemplateInfo)
            })
            .catch(error => console.log('failed to get response from server' + error))
            .then(response => {
                return response.json();
            })
            .catch(error => console.log('failed to parse response: ' + error))
            .then(response => {
                window.location.href = response.targetPage;
            });
}
