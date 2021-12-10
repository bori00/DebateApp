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

    @Autowired
    private DebateTemplateRepository debateTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LinkToResourceRepository linkToResourceRepository;

    @GetMapping("/create_debate_template")
    public String goToCreateDebateTemplatePage(Model model) {
        model.addAttribute("debate_template", new DebateTemplate());
        return "create_debate_template";
    }

    @PostMapping("/process_debate_template_creation")
    public String processDebateTemplateCreation(DebateTemplate debateTemplate) {
        User currentUser = getCurrentUser();
        currentUser.addNewDebateTemplate(debateTemplate);
        debateTemplateRepository.save(debateTemplate);
        return "redirect:/configure_debates";
    }

    @GetMapping(value = "/process_debate_template_deletion")
    public String processDebateTemplateDeletion(@RequestParam Long debateTemplateId) {
        debateTemplateRepository.deleteById(debateTemplateId);
        return "redirect:/configure_debates";
    }

    @GetMapping("/process_debate_template_viewing_request")
    public String processDebateTemplateViewingRequest(@RequestParam Long debateTemplateId,
                                                      Model model) {
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            throw new IllegalArgumentException("Inexistent debate template");
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        model.addAttribute("debate_template", debateTemplate);
        return "view_debate_template";
    }

    @GetMapping(value = "/process_debate_template_editing_request")
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

        return "edit_debate_template";
    }

    @PostMapping("/process_debate_template_editing")
    public String processDebateTemplateEditing(DebateTemplate debateTemplate) {
        debateTemplate.computeSecondsBasedOnMinsAndSecs();
        User currentUser = getCurrentUser();
        debateTemplate.setOwner(currentUser);
        debateTemplateRepository.save(debateTemplate);
        return "redirect:/process_debate_template_editing_request?debateTemplateId=" +
                debateTemplate.getId();
    }

    @GetMapping("/process_debate_template_resource_link_deletion")
    public String processDebateTemplateResourceLinkDeletion(@RequestParam Long resourceLinkId,
                                                            Model model) {
        Optional<LinkToResource> optLinkToResource =
                linkToResourceRepository.findById(resourceLinkId);
        if (optLinkToResource.isEmpty()) {
            throw new IllegalArgumentException("Inexistent link to resource");
        }
        LinkToResource linkToResource = optLinkToResource.get();
        linkToResourceRepository.delete(linkToResource);

        return "redirect:/process_debate_template_editing_request?debateTemplateId=" +
                linkToResource.getDebateTemplate().getId();
    }

    @PostMapping("/process_debate_template_resource_link_addition")
    public String processDebateTemplateResourceLinkAddition(LinkToResource linkToResource,
                                                            Model model) {
        linkToResource.getDebateTemplate().addNewDLinkToResource(linkToResource);
        linkToResourceRepository.save(linkToResource);
        return "redirect:/process_debate_template_editing_request?debateTemplateId=" +
                linkToResource.getDebateTemplate().getId();
    }

    @GetMapping("/process_start_debate")
    public String processStartDebate(DebateTemplate debateTemplate, Model model) {
        model.addAttribute("debate_template", debateTemplate);
        return "redirect:/start_debate";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
