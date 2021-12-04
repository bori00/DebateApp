function onDeleteDebateTemplateButtonPushed(debateTemplateId) {
    if (confirm('Are you sure you want to delete this debate template? This operation cannot be' +
        ' undone.')) {
        deleteDebateTemplate(debateTemplateId)
    }
}

function deleteDebateTemplate(debateTemplateId) {
    window.location.href = "/process_debate_template_deletion?debateTemplateId=" + debateTemplateId
}

function onViewDebateTemplateButtonPushed(debateTemplateId) {
    window.location.href = "/process_debate_template_viewing_request?debateTemplateId=" + debateTemplateId
}