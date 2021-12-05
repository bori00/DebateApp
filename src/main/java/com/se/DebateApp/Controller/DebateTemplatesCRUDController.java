package com.se.DebateApp.Controller;

import com.se.DebateApp.Model.CustomUserDetails;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class DebateTemplatesCRUDController {

    @Autowired
    DebateTemplateRepository debateTemplateRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/create_debate_template")
    public String goToCreateDebateTemplatePage(Model model) {
        model.addAttribute("debate_template", new DebateTemplate());
        return "create_debate_template";
    }

    @PostMapping("/process_debate_template_creation")
    public String processDebateTemplateCreation(DebateTemplate debateTemplate) {
        debateTemplate.computeSecondsBasedOnMinsAndSecs();
        User currentUser = getCurrentUser();
        currentUser.addNewDebateTemplate(debateTemplate);
        debateTemplateRepository.save(debateTemplate);
        return "redirect:/configure_debates";
    }

    @GetMapping(value="/process_debate_template_deletion")
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
        debateTemplate.computeMinsAndSecsBasedOnSeconds();
        model.addAttribute("debate_template", debateTemplate);
        return "view_debate_template";
    }

    @GetMapping(value="/process_debate_template_editing_request")
    public String processDebateTemplateEditingRequest(@RequestParam Long debateTemplateId,
                                                      Model model) {
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            throw new IllegalArgumentException("Inexistent debate template");
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        debateTemplate.computeMinsAndSecsBasedOnSeconds();
        model.addAttribute("debate_template", debateTemplate);
        return "edit_debate_template";
    }

    @PostMapping("/process_debate_template_editing")
    public String processDebateTemplateEditing(DebateTemplate debateTemplate) {
        debateTemplate.computeSecondsBasedOnMinsAndSecs();
        User currentUser = getCurrentUser();
        debateTemplate.setOwner(currentUser);
        debateTemplateRepository.save(debateTemplate);
        return "redirect:/configure_debates";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
