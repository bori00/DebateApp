function onDeleteDebateTemplateButtonPushed(debateTemplateId) {
    if (confirm('Are you sure you want to delete this debate template? This operation cannot be' +
        ' undone.')) {
        window.location.href = "/process_debate_template_deletion?debateTemplateId=" + debateTemplateId
    }
}

function deleteDebateTemplate(debateTemplateId) {
    window.location.href = "/process_debate_template_deletion?debateTemplateId=" + debateTemplateId
}

function onViewDebateTemplateButtonPushed(debateTemplateId) {
    window.location.href = "/process_debate_template_viewing_request?debateTemplateId=" + debateTemplateId
}

function onEditDebateTemplateButtonPushed(debateTemplateId) {
    window.location.href = "/process_debate_template_editing_request?debateTemplateId=" + debateTemplateId
}

function onDeleteDebateTemplateResourceLinkButtonPushed(resourceLinkId) {
    if (confirm('Are you sure you want to delete this resource link? This operation cannot be' +
        ' undone.')) {
        window.location.href = "/process_debate_template_resource_link_deletion?resourceLinkId=" + resourceLinkId
    }
}