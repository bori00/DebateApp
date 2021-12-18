package com.se.DebateApp.Controller.StartDebate;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.StartDebate.DTOs.*;
import com.se.DebateApp.Model.*;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerState;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DTOs.DebateParticipantsStatus;
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

import java.util.*;
import java.util.stream.Collectors;

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

    @PostMapping(value = "/process_join_debate", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping("/choose_team")
    public String processGoToChooseTeamPageRequest(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToJoinDebates =
                debateSessionRepository.findDebateSessionOfPlayerWithGivenState(
                        user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToJoinDebates.isEmpty()) {
            return "error";
        }
        DebateSessionTeamChoiceInformation teamChoiceInformation =
                new DebateSessionTeamChoiceInformation(
                        waitingToJoinDebates.get(0).getDebateTemplate());
        model.addAttribute("team_choice_information", teamChoiceInformation);
        return "choose_team";
    }

    @PostMapping(value = "/process_join_team", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JoinTeamRequestResponse processJoinDebateSession(@RequestBody Boolean joinsProTeam) {
        User user = getCurrentUser();
        List<DebateSession> waitingToJoinDebates =
                debateSessionRepository.findDebateSessionOfPlayerWithGivenState(
                        user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToJoinDebates.isEmpty()) {
            List<DebateSession> nonFinishedDebates =
                    debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user
                            , DebateSessionPhase.FINISHED);
            if (nonFinishedDebates.isEmpty()) {
                return new JoinTeamRequestResponse(false, JoinTeamRequestResponse.UNEXPECTED_ERROR);
            } else {
                return new JoinTeamRequestResponse(false,
                        JoinTeamRequestResponse.TOO_LATE_TO_JOIN_TEAM);
            }
        }
        if (waitingToJoinDebates.size() > 1) {
            return new JoinTeamRequestResponse(false, JoinTeamRequestResponse.UNEXPECTED_ERROR);
        }
        DebateSession session = waitingToJoinDebates.get(0);
        List<DebateSessionPlayer> usersPlayers =
                session.getPlayers()
                        .stream()
                        .filter(player -> player.getUser().getId().equals(user.getId()))
                        .collect(Collectors.toList());
        if (usersPlayers.size() != 1) {
            return new JoinTeamRequestResponse(false, JoinTeamRequestResponse.UNEXPECTED_ERROR);
        }
        DebateSessionPlayer usersPlayer = usersPlayers.get(0);
        if (usersPlayer.getPlayerState().equals(PlayerState.JOINED_A_TEAM)) {
            return new JoinTeamRequestResponse(false, JoinTeamRequestResponse.ALREADY_JOINED_TEAM);
        }
        usersPlayer.setPlayerState(PlayerState.JOINED_A_TEAM);
        if (joinsProTeam) {
            usersPlayer.setTeam(TeamType.PRO);
        } else {
            usersPlayer.setTeam(TeamType.CON);
        }
        debateSessionPlayerRepository.save(usersPlayer);
        announceJudgeAboutDebateSessionParticipantsState(
                session.getDebateTemplate().getOwner(),
                session.computeParticipantsStatus());
        return new JoinTeamRequestResponse(true, "");
    }

    @GetMapping("/go_to_debate_lobby")
    public String processGoToDebateLobbyPage(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToActivateDebates =
                debateSessionRepository.findDebateSessionOfPlayerWithGivenState(
                        user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToActivateDebates.size() != 1) {
            return "error";
        }
        DebateSession session = waitingToActivateDebates.get(0);
        model.addAttribute("debateInformation",
                new DebateLobbyInformation(session.getDebateTemplate()));
        return "debate_lobby";
    }

    @GetMapping("/process_activate_debate_session")
    public String activateDebateSession(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToActivateDebates =
                debateSessionRepository.findDebateSessionOfJudgeWithGivenState(user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToActivateDebates.size() != 1) {
            return "error";
        }
        DebateSession session = waitingToActivateDebates.get(0);
        // TODO: update when timing solutions are clarified
        session.setDebateSessionPhase(DebateSessionPhase.PREP_TIME);
        session.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        Set<DebateSessionPlayer> joinedPlayers = new HashSet<>(session.getPlayers());
        session.removePlayersWhoDidntJoinATeam();
        debateSessionRepository.save(session);
        announceAllDebatePlayersAboutDebateActivation(joinedPlayers);
        return goToActiveDebatePage(model);
    }

    @GetMapping("/go_to_active_debate")
    public String goToActiveDebatePage(Model model) {
        User currentUser = getCurrentUser();
        DebateSession debateSession = null;
        boolean isJudge = false;

        Optional<DebateSessionPlayer> debateSessionPlayer = debateSessionPlayerRepository.findDebateSessionPlayerByUser(currentUser);

        if(debateSessionPlayer.isPresent()) {
            debateSession = debateSessionPlayer.get().getDebateSession();
        }else{
            isJudge = true;
            List<DebateSession> preparationPhaseDebatesOfJudge =
                    debateSessionRepository.findDebateSessionOfJudgeWithGivenState(currentUser,
                            DebateSessionPhase.PREP_TIME);
            if (preparationPhaseDebatesOfJudge.size() == 1) {
                debateSession = preparationPhaseDebatesOfJudge.get(0);
            }
        }
        if(debateSession == null) {
            return "error";
        }

        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("isJudge", isJudge);
        model.addAttribute("debateSessionId", debateSession.getId());
        return "active_debate";
    }

    private void announceJudgeAboutDebateSessionParticipantsState(
            User judge,
            DebateParticipantsStatus debateParticipantsStatus) {
        simpMessagingTemplate.convertAndSendToUser(
                judge.getUserName(),
                "/queue/debate-session-participants-status",
                debateParticipantsStatus);

    }

    private void announceAllDebatePlayersAboutDebateActivation(Set<DebateSessionPlayer> joinedPlayers) {
        for (DebateSessionPlayer player : joinedPlayers) {
            simpMessagingTemplate.convertAndSendToUser(
                    player.getUser().getUserName(),
                    "/queue/debate-session-activated",
                    "activated");
        }
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
