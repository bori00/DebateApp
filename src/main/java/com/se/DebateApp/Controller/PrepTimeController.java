package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PrepTimeController {
    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(SupportedMappings.JUDGE_ACTIVATE_DEBATE)
    @ResponseBody
    public OngoingDebateRequestResponse startDebatePreparation(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToActivateDebates =
                debateSessionRepository.findDebateSessionOfJudgeWithGivenState(user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToActivateDebates.size() != 1) {
            return new OngoingDebateRequestResponse(false, false,
                    OngoingDebateRequestResponse.UNEXPECTED_ERROR_MSG);
        }

        DebateSession session = waitingToActivateDebates.get(0);

        DebateState currentState = session.getDebateSessionPhase().getCorrespondingState();
        currentState.onEndOfState(session, notificationService, debateSessionRepository);

        session.getDebateSessionPhase().getCorrespondingState().onBeginningOfState(session, notificationService);

        return new OngoingDebateRequestResponse(true, true, "");
    }

    @GetMapping(SupportedMappings.GO_TO_DEBATE_PREPARATION)
    public String goToDebatePreparationPage(Model model) {
        User currentUser = getCurrentUser();
        DebateSession debateSession;

        List<DebateSession> debateSessionsOfJudgeInPreparationState =
                debateSessionRepository
                        .findDebateSessionOfJudgeWithGivenState(
                                currentUser,
                                DebateSessionPhase.PREP_TIME);
        if (debateSessionsOfJudgeInPreparationState.size() == 1) {
            debateSession = debateSessionsOfJudgeInPreparationState.get(0);
        } else {
            List<DebateSession> debateSessionsOfPlayerInPreparationState =
                    debateSessionRepository.findDebateSessionOfPlayerWithGivenState(
                            currentUser,
                            DebateSessionPhase.PREP_TIME);
            if (debateSessionsOfPlayerInPreparationState.size() == 1) {
                debateSession = debateSessionsOfPlayerInPreparationState.get(0);
            } else {
                return SupportedMappings.ERROR_PAGE;
            }
        }
        model.addAttribute("isJudge", isCurrentUserJudge());
        model.addAttribute("debateSessionId", debateSession.getId());
        model.addAttribute("debateTemplate", debateSession.getDebateTemplate());
        return SupportedMappings.DEBATE_PREPARATION_PAGE;
    }

    private boolean isCurrentUserJudge() {
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), DebateSessionPhase.FINISHED).size() == 1;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
