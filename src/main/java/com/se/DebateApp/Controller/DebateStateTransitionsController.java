package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.se.DebateApp.Model.Constants.DebateSessionPhase.FINISHED;

@Controller
public class DebateStateTransitionsController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @GetMapping("/process_get_time_interval")
    @ResponseBody
    public Integer processGetTimeInterval(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        DebateTemplate debateTemplate = debateSession.getDebateTemplate();

        switch (debateSession.getDebateSessionPhase()) {
            case PREP_TIME -> {
                return debateTemplate.getPrepTimeSeconds();
            }
            case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1, AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2, NEGATIVE_CONSTRUCTIVE_SPEECH_1, NEGATIVE_CONSTRUCTIVE_SPEECH_2 -> {
                return debateTemplate.getConstSpeechSeconds();
            }
            case CROSS_EXAMINATION_1, CROSS_EXAMINATION_2, CROSS_EXAMINATION_3, CROSS_EXAMINATION_4 -> {
                return debateTemplate.getCrossExaminationSeconds();
            }
            case AFFIRMATIVE_REBUTTAL_1, AFFIRMATIVE_REBUTTAL_2, NEGATIVE_REBUTTAL_1, NEGATIVE_REBUTTAL_2 -> {
                return debateTemplate.getRebuttalSpeechSeconds();
            }
            default -> {
                return 24 * 60 * 60; // 24 hours
            }
        }
    }

    @GetMapping(value = "/process_get_current_phase_starting_time", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Date processGetCurrentPhaseStartingTime(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);

        return debateSession.getCurrentPhaseStartingTime();
    }

    @GetMapping(value = "/process_is_debate_finished")
    @ResponseBody
    public Boolean processIsDebateFinished(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);

        return debateSession.getDebateSessionPhase().equals(FINISHED);
    }

    @PostMapping("/process_end_of_current_phase")
    @ResponseBody
    public void processEndOfCurrentPhase(@RequestParam Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        DebateSessionPhase currentPhase = debateSession.getDebateSessionPhase();
        if(currentPhase.equals(FINISHED)) {
            return;
        }
        int nextPhase = currentPhase.ordinal() + 1;
        debateSession.setDebateSessionPhase(DebateSessionPhase.values()[nextPhase]);
        debateSession.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        debateSessionRepository.save(debateSession);
        announceAllDebatePlayersAboutEndOfTimeInterval(debateSession.getPlayers(), currentPhase);
    }

    @GetMapping("/process_close_debate")
    public String processCloseDebate() {
        List<DebateSession> ongoingDebateSessions = debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), FINISHED);
        if(ongoingDebateSessions.size() != 1) {
            return "error";
        }
        DebateSession debateSession = ongoingDebateSessions.get(0);
        debateSession.setDebateSessionPhase(FINISHED);
        debateSessionRepository.save(debateSession);
        announceAllDebatePlayersAboutDebateClosed(debateSession.getPlayers());
        return "home";
    }

    private void announceAllDebatePlayersAboutDebateClosed(Set<DebateSessionPlayer> players) {
        for (DebateSessionPlayer player : players) {
            simpMessagingTemplate.convertAndSendToUser(
                    player.getUser().getUserName(), "queue/debate-closed", "closed");
        }
    }

    private void announceAllDebatePlayersAboutEndOfTimeInterval(Set<DebateSessionPlayer> players, DebateSessionPhase phase) {
        String destinationUrl = "/queue/debate-" + phase.name().toLowerCase().replace('_', '-') + "-times-up";
        for (DebateSessionPlayer player : players) {
            simpMessagingTemplate.convertAndSendToUser(
                    player.getUser().getUserName(), destinationUrl, "timesUp");
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
