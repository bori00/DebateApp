package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerState;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomePageController {

    @Autowired
    private DebateTemplateRepository debateTemplateRepository;

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping(SupportedMappings.GO_TO_STARTING_PAGE)
    public String viewUserDependentHomePage() {
        if (userIsAuthenticated()) {
            return SupportedMappings.AUTHENTICATED_HOME_PAGE;
        }
        return SupportedMappings.UNAUTHENTICATED_INDEX_PAGE;
    }

    @GetMapping(SupportedMappings.GO_TO_AUTHENTICATED_HOME_PAGE)
    public String viewHomePage() {
        return SupportedMappings.AUTHENTICATED_HOME_PAGE;
    }

    @GetMapping(SupportedMappings.GO_TO_REGISTER_PAGE)
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return SupportedMappings.REGISTRATION_PAGE;
    }

    @GetMapping(SupportedMappings.GO_TO_JOIN_DEBATE_PAGE)
    public String goToJoinDebatePage() {
        return SupportedMappings.JOIN_DEBATE_PAGE;
    }

    @GetMapping(SupportedMappings.GO_TO_CONFIGURE_DEBATES_PAGE)
    public String goToConfigureDebatesPage(Model model) {
        model.addAttribute("myDebateTemplates",
                debateTemplateRepository.findAllDebateTemplatesOfUser(getCurrentUser()));
        return SupportedMappings.CONFIGURE_DEBATES_PAGE;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }

    private boolean userIsAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal() instanceof UserDetails;
    }

    @Getter
    @AllArgsConstructor
    private static class StringWrapper {
        private final String value;
    }
}
