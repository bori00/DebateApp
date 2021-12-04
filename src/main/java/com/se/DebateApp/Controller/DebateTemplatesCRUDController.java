package com.se.DebateApp.Controller;

import com.se.DebateApp.Controller.HttpResponseModels.DebateTemplateIdentifier;
import com.se.DebateApp.Controller.HttpResponseModels.TargetPage;
import com.se.DebateApp.Model.CustomUserDetails;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

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
        debateTemplate.computeSeconds();
        User currentUser = getCurrentUser();
        currentUser.addNewDebateTemplate(debateTemplate);
        debateTemplateRepository.save(debateTemplate);
        return "redirect:/configure_debates";
    }

    @PostMapping(value="/process_debate_template_deletion", produces =
            MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    /** Returns the name of page to be redirected to. */
    public TargetPage processDebateTemplateDeletion(@RequestBody DebateTemplateIdentifier debateTemplateInfo) {
        debateTemplateRepository.deleteById(debateTemplateInfo.getDebateTemplateId());
        return new TargetPage("/configure_debates");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
