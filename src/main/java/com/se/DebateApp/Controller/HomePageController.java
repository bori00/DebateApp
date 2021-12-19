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

    @GetMapping("")
    public String viewUserDependentHomePage() {
        if (userIsAuthenticated()) {
            return "home";
        }
        return "index";
    }

    @GetMapping("/home")
    public String viewHomePage() {
        return "home";
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

    @PostMapping("/has_user_ongoing_debate")
    @ResponseBody
    public boolean hasUserOngoingDebate() {
        User user = getCurrentUser();
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                DebateSessionPhase.FINISHED).size() > 0 ||
                debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED).size() > 0;
    }

    @PostMapping("/get_ongoing_debate_href")
    @ResponseBody
    public StringWrapper getOngoingDebateLink() {
        User user = getCurrentUser();
        List<DebateSession> ongoingDebatesAsJudge =
                debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                DebateSessionPhase.FINISHED);
        List<DebateSession> ongoingDebatesAsPlayer =
                debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        if (ongoingDebatesAsJudge.isEmpty() && ongoingDebatesAsPlayer.isEmpty()) {
            return new StringWrapper("error");
        }
        if (ongoingDebatesAsJudge.size() > 0) {
            if (ongoingDebatesAsJudge.size() > 1) {
                return new StringWrapper("error");
            }
            DebateSession session = ongoingDebatesAsJudge.get(0);
            if (session.getDebateSessionPhase() == DebateSessionPhase.WAITING_FOR_PLAYERS) {
                return new StringWrapper("/reenter_start_debate");
            }
            return new StringWrapper("active_debate");
            // TODO: possibly extend
        } else {
            if (ongoingDebatesAsPlayer.size() > 1) {
                return new StringWrapper("error");
            }
            DebateSession session = ongoingDebatesAsPlayer.get(0);
            if (session.getDebateSessionPhase() == DebateSessionPhase.WAITING_FOR_PLAYERS) {
                DebateSessionPlayer player =
                        session.getPlayers()
                                .stream()
                                .filter(p -> p.getUser().equals(user))
                                .collect(Collectors.toList()).get(0);
                if (player.getPlayerState().equals(PlayerState.WAITING_TO_JOIN_TEAM)) {
                    return new StringWrapper("choose_team");
                } else {
                    return new StringWrapper("debate_lobby");
                }
            }
            return new StringWrapper("active_debate");
            // TODO: possibly extend
        }
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
