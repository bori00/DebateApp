package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.LinkToResource;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.LinkToResourceRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class DebateTemplatesCRUDController {

    // TODO: handle illegal arguments
    // TODO: doesn't respect responding vs redirecting requests pattern

    @Autowired
    private DebateTemplateRepository debateTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LinkToResourceRepository linkToResourceRepository;

    @GetMapping(SupportedMappings.CREATE_DEBATE_TEMPLATE_REQUEST)
    public String goToCreateDebateTemplatePage(Model model) {
        model.addAttribute("debate_template", new DebateTemplate());
        return SupportedMappings.CREATE_DEBATE_TEMPLATE_PAGE;
    }

    @PostMapping(SupportedMappings.PROCESS_DEBATE_TEMPLATE_CREATION_REQUEST)
    public String processDebateTemplateCreation(DebateTemplate debateTemplate) {
        User currentUser = getCurrentUser();
        currentUser.addNewDebateTemplate(debateTemplate);
        debateTemplateRepository.save(debateTemplate);
        return SupportedMappings.REDIRECT_PREFIX + SupportedMappings.GO_TO_CONFIGURE_DEBATES_PAGE;
    }

    @GetMapping(value = SupportedMappings.PROCESS_DEBATE_TEMPLATE_DELETION_REQUEST)
    public String processDebateTemplateDeletion(@RequestParam Long debateTemplateId) {
        debateTemplateRepository.deleteById(debateTemplateId);
        return SupportedMappings.REDIRECT_PREFIX + SupportedMappings.GO_TO_CONFIGURE_DEBATES_PAGE;
    }

    @GetMapping(SupportedMappings.PROCESS_DEBATE_TEMPLATE_VIEWING_REQUEST)
    public String processDebateTemplateViewingRequest(@RequestParam Long debateTemplateId,
                                                      Model model) {
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            throw new IllegalArgumentException("Inexistent debate template");
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        model.addAttribute("debate_template", debateTemplate);
        return SupportedMappings.VIEW_DEBATE_TEMPLATE_PAGE;
    }

    @GetMapping(value = SupportedMappings.PROCESS_DEBATE_TEMPLATE_EDITING_REQUEST)
    public String processDebateTemplateEditingRequest(@RequestParam Long debateTemplateId,
                                                      Model model) {
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            throw new IllegalArgumentException("Inexistent debate template");
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        model.addAttribute("debate_template", debateTemplate);

        LinkToResource newLinkToResource = new LinkToResource();
        newLinkToResource.setDebateTemplate(debateTemplate);
        model.addAttribute("new_resource_link", newLinkToResource);

        return SupportedMappings.EDIT_DEBATE_TEMPLATE_PAGE;
    }

    @PostMapping(SupportedMappings.PROCESS_DEBATE_TEMPLATE_EDITING)
    public String processDebateTemplateEditing(DebateTemplate debateTemplate) {
        debateTemplate.computeSecondsBasedOnMinsAndSecs();
        User currentUser = getCurrentUser();
        debateTemplate.setOwner(currentUser);
        debateTemplateRepository.save(debateTemplate);
        return SupportedMappings.REDIRECT_PREFIX + SupportedMappings.PROCESS_DEBATE_TEMPLATE_EDITING_REQUEST +
                "?debateTemplateId=" +
                debateTemplate.getId();
    }

    @GetMapping(SupportedMappings.PROCESS_DEBATE_TEMPLATE_RESOURCE_LINK_DELETION_REQUEST)
    public String processDebateTemplateResourceLinkDeletion(@RequestParam Long resourceLinkId,
                                                            Model model) {
        Optional<LinkToResource> optLinkToResource =
                linkToResourceRepository.findById(resourceLinkId);
        if (optLinkToResource.isEmpty()) {
            throw new IllegalArgumentException("Inexistent link to resource");
        }
        LinkToResource linkToResource = optLinkToResource.get();
        linkToResourceRepository.delete(linkToResource);

        return SupportedMappings.REDIRECT_PREFIX +
                SupportedMappings.PROCESS_DEBATE_TEMPLATE_EDITING_REQUEST +
                "?debateTemplateId=" +
                linkToResource.getDebateTemplate().getId();
    }

    @PostMapping(SupportedMappings.PROCESS_DEBATE_TEMPLATE_RESOURCE_LINK_ADDITION_REQUEST)
    public String processDebateTemplateResourceLinkAddition(LinkToResource linkToResource,
                                                            Model model) {
        linkToResource.getDebateTemplate().addNewDLinkToResource(linkToResource);
        linkToResourceRepository.save(linkToResource);
        return SupportedMappings.REDIRECT_PREFIX +
                SupportedMappings.PROCESS_DEBATE_TEMPLATE_EDITING_REQUEST +
                "?debateTemplateId=" +
                linkToResource.getDebateTemplate().getId();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
