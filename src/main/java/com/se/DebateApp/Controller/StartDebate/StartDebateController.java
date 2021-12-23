package com.se.DebateApp.Controller.StartDebate;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.StartDebate.DTOs.*;
import com.se.DebateApp.Controller.SupportedMappings;
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

    @PostMapping(SupportedMappings.JUDGE_START_DEBATE_REQUEST)
    @ResponseBody
    public StartDebateRequestResponse processStartDebateSession(@RequestBody Long debateTemplateId, Model model) {
        if (hasOngoingDebate(getCurrentUser())) {
            return new StartDebateRequestResponse(false,
                    StartDebateRequestResponse.HAS_OTHER_ONGOING_DEBATE_ERROR_MSG);
        }
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            return new StartDebateRequestResponse(false,
                    StartDebateRequestResponse.UNEXPECTED_ERROR_MSG);
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        DebateSession debateSession = new DebateSession();
        Date currentTime = new Date(System.currentTimeMillis());
        debateSession.setCurrentPhaseStartingTime(currentTime);
        debateTemplate.addNewSession(debateSession);
        DebateSession savedSession = debateSessionRepository.save(debateSession);
        return new StartDebateRequestResponse(true, "");
    }

    @GetMapping(SupportedMappings.JUDGE_GO_TO_STARTING_DEBATE_REQUEST)
    public String reenterStartDebateSession(Model model) {
        User user = getCurrentUser();
        List<DebateSession> ongoingsSessionsAsJudge =
                debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        if (ongoingsSessionsAsJudge.size() != 1) {
            return "error";
        }
        DebateSession session = ongoingsSessionsAsJudge.get(0);
        model.addAttribute("debateCode", session.getId());
        DebateParticipantsStatus participantsStatus = session.computeParticipantsStatus();
        model.addAttribute("waitingParticipants", participantsStatus.getNoWaitingToJoinParticipants());
        model.addAttribute("proPlayers", participantsStatus.getNoProParticipants());
        model.addAttribute("conPlayers", participantsStatus.getNoConParticipants());
        return "start_debate";
    }

    @PostMapping(value = SupportedMappings.PLAYER_JOIN_DEBATE_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(SupportedMappings.PLAYER_GO_TO_CHOOSE_TEAM_REQUEST)
    public String processGoToChooseTeamPageRequest(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToJoinDebates =
                debateSessionRepository.findDebateSessionOfPlayerWithGivenState(
                        user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToJoinDebates.isEmpty()) {
            return SupportedMappings.ERROR_PAGE;
        }
        DebateSessionTeamChoiceInformation teamChoiceInformation =
                new DebateSessionTeamChoiceInformation(
                        waitingToJoinDebates.get(0).getDebateTemplate());
        model.addAttribute("team_choice_information", teamChoiceInformation);
        return SupportedMappings.PLAYER_CHOOSE_TEAM_PAGE;
    }

    @PostMapping(value = SupportedMappings.PLAYER_JOIN_TEAM_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(SupportedMappings.PLAYER_GO_TO_DEBATE_LOBBY_REQUEST)
    public String processGoToDebateLobbyPage(Model model) {
        User user = getCurrentUser();
        List<DebateSession> waitingToActivateDebates =
                debateSessionRepository.findDebateSessionOfPlayerWithGivenState(
                        user,
                        DebateSessionPhase.WAITING_FOR_PLAYERS);
        if (waitingToActivateDebates.size() != 1) {
            return SupportedMappings.ERROR_PAGE;
        }
        DebateSession session = waitingToActivateDebates.get(0);
        model.addAttribute("debateInformation",
                new DebateLobbyInformation(session.getDebateTemplate()));
        return SupportedMappings.PLAYER_DEBATE_LOBBY_PAGE;
    }

    @GetMapping("/go_to_active_debate")
    public String goToActiveDebatePage(Model model) {
        model.addAttribute("isJudge", isCurrentUserJudge());
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

    private boolean isCurrentUserJudge() {
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), DebateSessionPhase.FINISHED).size() == 1;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
