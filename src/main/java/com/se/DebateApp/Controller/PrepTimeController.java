package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionPlayerRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class PrepTimeController {
    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateTemplateRepository debateTemplateRepository;

    @Autowired
    private DebateSessionPlayerRepository debateSessionPlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/process_start_debate_preparation")
    public String startDebatePreparation(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToActivateDebates =
                debateSessionRepository.findDebateSessionOfJudgeWithGivenState(user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToActivateDebates.size() != 1) {
            return "error";
        }
        DebateSession session = waitingToActivateDebates.get(0);
        DebateTemplate debateTemplate = session.getDebateTemplate();
        boolean skipPhase = debateTemplate.getPrepTimeSeconds() == 0;

        session.setDebateSessionPhase((skipPhase)? DebateSessionPhase.DEPUTY1_VOTING_TIME: DebateSessionPhase.PREP_TIME);
        session.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        Set<DebateSessionPlayer> joinedPlayers = new HashSet<>(session.getPlayers());
        session.removePlayersWhoDidntJoinATeam();

        debateSessionRepository.save(session);
        announceAllDebatePlayersAboutDebateActivation(joinedPlayers);
        return (skipPhase)? goToDeputySelectionPage(model) : goToDebatePreparationPage(model);
    }

    @GetMapping("/go_to_debate_preparation")
    public String goToDebatePreparationPage(Model model) {
        User currentUser = getCurrentUser();
        DebateSession debateSession;

        List<DebateSession> debateSessionsOfJudgeInPreparationState = debateSessionRepository.findDebateSessionOfJudgeWithGivenState(currentUser, DebateSessionPhase.PREP_TIME);
        if(debateSessionsOfJudgeInPreparationState.size() == 1) {
            debateSession = debateSessionsOfJudgeInPreparationState.get(0);
        }else{
            List<DebateSession> debateSessionsOfPlayerInPreparationState = debateSessionRepository.findDebateSessionOfPlayerWithGivenState(currentUser, DebateSessionPhase.PREP_TIME);
            if(debateSessionsOfPlayerInPreparationState.size() == 1) {
                debateSession = debateSessionsOfPlayerInPreparationState.get(0);
            }else{
                return "error";
            }
        }
        model.addAttribute("isJudge", isCurrentUserJudge());
        model.addAttribute("debateSessionId", debateSession.getId());
        model.addAttribute("debateTemplate", debateSession.getDebateTemplate());
        return "debate_preparation";
    }

    @GetMapping("/reenter_debate_preparation")
    public String reenterActiveDebatePage(Model model) {
        return goToDebatePreparationPage(model);
    }

    private void announceAllDebatePlayersAboutDebateActivation(Set<DebateSessionPlayer> joinedPlayers) {
        for (DebateSessionPlayer player : joinedPlayers) {
            simpMessagingTemplate.convertAndSendToUser(
                    player.getUser().getUserName(),
                    "/queue/debate-session-activated",
                    "activated");
        }
    }

    @GetMapping("/go_to_deputy_selection")
    public String goToDeputySelectionPage(Model model) {
        model.addAttribute("isJudge", isCurrentUserJudge());
        return "deputy_selection";
    }

    private boolean isCurrentUserJudge() {
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), DebateSessionPhase.FINISHED).size() == 1;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
