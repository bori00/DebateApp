package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.se.DebateApp.Model.Constants.DebateSessionPhase.FINISHED;

@Controller
public class DebateStateTransitionsController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping(SupportedMappings.GO_TO_ONGOING_DEBATES_CURRENT_PHASE)
    public String redirectToDebatesCurrentPhase(Model model) {
        User user = getCurrentUser();
        List<DebateSession> ongoingDebatesAsJudge =
                debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        List<DebateSession> ongoingDebatesAsPlayer =
                debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        if (ongoingDebatesAsJudge.isEmpty() && ongoingDebatesAsPlayer.isEmpty()) {
            // no ongoing debate --> redirect to the home page
            return SupportedMappings.REDIRECT_PREFIX + SupportedMappings.GO_TO_STARTING_PAGE;
        }
        if (ongoingDebatesAsJudge.size() > 0) {
            if (ongoingDebatesAsJudge.size() > 1) {
                return SupportedMappings.ERROR_PAGE;
            }
            DebateSession session = ongoingDebatesAsJudge.get(0);
            DebateState sessionState = session.getDebateSessionPhase().getCorrespondingState();
            return SupportedMappings.REDIRECT_PREFIX + sessionState.getJudgesRedirectTargetOnStateEnter();
        } else {
            if (ongoingDebatesAsPlayer.size() > 1) {
                return SupportedMappings.ERROR_PAGE;
            }
            DebateSession session = ongoingDebatesAsPlayer.get(0);
            DebateState sessionState = session.getDebateSessionPhase().getCorrespondingState();
            List<DebateSessionPlayer> players =
                    session.getPlayers().stream().filter(p -> p.getUser().equals(user)).collect(Collectors.toList());
            if (players.size() != 1) {
                return SupportedMappings.ERROR_PAGE;
            }
            return SupportedMappings.REDIRECT_PREFIX +
                    sessionState.getPlayersRedirectTargetOnStateEnter(players.get(0));
        }
    }


    @GetMapping(SupportedMappings.GET_CURRENT_PHASES_TIME_INTERVAL)
    @ResponseBody
    public Integer getTimeInterval(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        DebateTemplate debateTemplate = debateSession.getDebateTemplate();

        if (debateSession.getDebateSessionPhase().getDefaultLengthInSeconds().isPresent()) {
            return debateSession.getDebateSessionPhase().getDefaultLengthInSeconds().get();
        } else {
            return switch (debateSession.getDebateSessionPhase()) {
                case PREP_TIME -> debateTemplate.getPrepTimeSeconds();
                case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1, AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2, NEGATIVE_CONSTRUCTIVE_SPEECH_1, NEGATIVE_CONSTRUCTIVE_SPEECH_2 -> debateTemplate.getConstSpeechSeconds();
                case CROSS_EXAMINATION_1, CROSS_EXAMINATION_2, CROSS_EXAMINATION_3, CROSS_EXAMINATION_4 -> debateTemplate.getCrossExaminationSeconds();
                case AFFIRMATIVE_REBUTTAL_1, AFFIRMATIVE_REBUTTAL_2, NEGATIVE_REBUTTAL_1, NEGATIVE_REBUTTAL_2 -> debateTemplate.getRebuttalSpeechSeconds();
                default -> debateSession.getDebateSessionPhase().getDefaultLengthInSeconds().orElse(0);
            };
        }
    }

    @GetMapping(value = SupportedMappings.GET_CURRENT_PHASE_STARTING_TIME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Date getCurrentPhaseStartingTime(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);

        return debateSession.getCurrentPhaseStartingTime();
    }

    @GetMapping(value = SupportedMappings.IS_DEBATE_SESSION_FINISHED)
    @ResponseBody
    public Boolean isDebateFinished(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);

        return debateSession.getDebateSessionPhase().equals(FINISHED);
    }

    @PostMapping(SupportedMappings.PROCESS_END_OF_TIMED_PHASE)
    @ResponseBody
    public void processEndOfTimedPhase(@RequestParam Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        DebateSessionPhase currentPhase = debateSession.getDebateSessionPhase();
        if (currentPhase.equals(FINISHED)) {
            return;
        }
        DebateState currentState = currentPhase.getCorrespondingState();
        currentState.onEndOfState(debateSession, notificationService, debateSessionRepository);
        debateSession.getDebateSessionPhase().getCorrespondingState().onBeginningOfState(debateSession,
                notificationService);
    }

    @PostMapping(SupportedMappings.CLOSE_DEBATE)
    @ResponseBody
    public OngoingDebateRequestResponse processCloseDebate() {
        List<DebateSession> ongoingDebateSessions = debateSessionRepository
                .findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), FINISHED);
        if (ongoingDebateSessions.size() != 1) {
            return new OngoingDebateRequestResponse(false, false, OngoingDebateRequestResponse.UNEXPECTED_ERROR_MSG);
        }
        DebateSession debateSession = ongoingDebateSessions.get(0);
        debateSession.setDebateSessionPhase(FINISHED);
        DebateState newState = debateSession.getDebateSessionPhase().getCorrespondingState();
        debateSessionRepository.save(debateSession);
        newState.onBeginningOfState(debateSession, notificationService);
        return new OngoingDebateRequestResponse(true, true, "");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
