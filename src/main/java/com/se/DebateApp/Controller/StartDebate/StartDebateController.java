package com.se.DebateApp.Controller.StartDebate;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.StartDebate.DTOs.JoinDebateRequestResponse;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DTOs.DebateParticipantsStatus;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionPlayerRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class StartDebateController {

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

    private static final String errorMessageName = "errorMessage";
    private static final String hasOtherOngoingDebateErrorMsg = "You can't start a new debate, " +
            "when you " +
            "are a player/judge in an other ongoing debate. Please retry later!";

    @GetMapping("/process_start_debate")
    public String processStartDebateSession(@RequestParam Long debateTemplateId, Model model) {
        if (hasOngoingDebate(getCurrentUser())) {
            model.addAttribute(errorMessageName, hasOtherOngoingDebateErrorMsg);
            return "start_debate";
        }
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            throw new IllegalArgumentException();
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        DebateSession debateSession = new DebateSession();
        Date currentTime = new Date(System.currentTimeMillis());
        debateSession.setCurrentPhaseStartingTime(currentTime);
        debateTemplate.addNewSession(debateSession);
        DebateSession savedSession = debateSessionRepository.save(debateSession);
        model.addAttribute("debateCode", savedSession.getId());
        return "start_debate";
    }

    @PostMapping(value="/process_join_debate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JoinDebateRequestResponse processJoinDebateSession(@RequestBody Long debateCode) {
        User currentUser = getCurrentUser();
        if (hasOngoingDebate(getCurrentUser())) {
            return new JoinDebateRequestResponse(false,
                    JoinDebateRequestResponse.HAS_OTHER_ONGOING_DEBATE_ERROR_MSG);
        }
        Optional<DebateSession> optDebateSession =
                debateSessionRepository.findById(debateCode);
        if (optDebateSession.isEmpty()) {
            return new JoinDebateRequestResponse(false,
                    JoinDebateRequestResponse.DEBATE_NOT_FOUND_ERROR_MSG);
        }
        DebateSession debateSession = optDebateSession.get();
        if (debateSession.getDebateSessionPhase() != DebateSessionPhase.WAITING_FOR_PLAYERS) {
            return new JoinDebateRequestResponse(false,
                    JoinDebateRequestResponse.DEBATE_NO_LONGER_ACCEPTING_PLAYERS_ERROR_MSG);
        }
        DebateSessionPlayer debateSessionPlayer = new DebateSessionPlayer();
        debateSessionPlayer.setUser(currentUser);
        debateSession.addNewPlayer(debateSessionPlayer);
        debateSessionPlayerRepository.save(debateSessionPlayer);
        announceJudgeAboutDebateSessionParticipantsState(debateSession.getDebateTemplate().getOwner(), debateSession.computeParticipantsStatus());
        return new JoinDebateRequestResponse(true, null);
    }

//    @GetMapping
//    public String processGoToChooseTeamPageRequest(Model model) {
//        User currentUser = getCurrentUser();
//        List<DebateSession> usersSessions =
//                debateSessionRepository.findActiveDebateSessionsOfPlayer(currentUser);
//        return "/"; // TODO: complete
//    }


    public void announceJudgeAboutDebateSessionParticipantsState(User judge,
                                                                 DebateParticipantsStatus debateParticipantsStatus) {
        simpMessagingTemplate.convertAndSendToUser(
                judge.getUserName(),
                "/queue/debate-session-participants-status",
                debateParticipantsStatus);

    }

    private boolean hasOngoingDebate(User user) {
        List<DebateSession> ongoingSessionsAsJudge =
                debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        if (!ongoingSessionsAsJudge.isEmpty()) {
            return true;
        }
        List<DebateSession> ongoingDebatesAsPlayer =
                debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        return !ongoingDebatesAsPlayer.isEmpty();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
