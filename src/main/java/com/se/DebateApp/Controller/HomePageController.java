package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class HomePageController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateTemplateRepository debateTemplateRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/join_debate")
    public String goToJoinDebatePage() {
        return "join_debate";
    }

    @GetMapping("/start_debate")
    public String goToStartDebatePage(Model model) {
        model.addAttribute("user", getCurrentUser());
        return "start_debate";
    }

    @GetMapping("/configure_debates")
    public String goToConfigureDebatesPage(Model model) {
        model.addAttribute("myDebateTemplates",
                debateTemplateRepository.findAllDebateTemplatesOfUser(getCurrentUser()));
        return "configure_debates";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
