package com.se.DebateApp.Controller.DeputySelection;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.DeputySelection.DTOs.CastVoteResponseDTO;
import com.se.DebateApp.Controller.DeputySelection.DTOs.DeputyCandidateDTO;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DebateRoleVote;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateRoleVoteRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DeputySelectionController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DebateRoleVoteRepository roleVotesRepository;

    @GetMapping("/go_to_deputy_selection")
    public String goToDeputySelectionPage(Model model) {
        User user = getCurrentUser();
        if (isCurrentUserJudge(user)) {
            List<DebateSession> sessions =
                    debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                            DebateSessionPhase.FINISHED);
            DebateSession session = sessions.get(0);
            model.addAttribute("debateSessionId", session.getId());
            model.addAttribute("roleName",
                    session.getDebateSessionPhase().equals(DebateSessionPhase.DEPUTY1_VOTING_TIME) ? "1st Deputy" : "2nd Deputy");
            return "deputy_selection_for_judge";
        } else {
            List<DebateSession> sessions =
                    debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                            DebateSessionPhase.FINISHED);
            if (sessions.size() != 1) {
                return "error";
            }
            DebateSession session = sessions.get(0);
            DebateSessionPlayer player = findPlayerOfUserInDebateSession(user, session);
            model.addAttribute("debateSessionId", session.getId());
            model.addAttribute("candidates", getNextDeputyCandidates(player, session));
            model.addAttribute("teamName",
                    player.getTeam().equals(TeamType.PRO) ? "Affirmative" : "Negative");
            model.addAttribute("isPro",
                    player.getTeam().equals(TeamType.PRO));
            model.addAttribute("roleName",
                    session.getDebateSessionPhase().equals(DebateSessionPhase.DEPUTY1_VOTING_TIME) ? "1st Deputy" : "2nd Deputy");
            return "deputy_selection_for_players";
        }
    }

    @PostMapping("/cast_deputy_vote")
    @ResponseBody
    CastVoteResponseDTO castDeputyVote(@RequestBody DeputyCandidateDTO deputyCandidateDTO) {
        User user = getCurrentUser();
        Optional<DebateSession> optDebateSession = getOngoingDebate(user);
        if (optDebateSession.isEmpty()) {
            System.out.println("No debate session found");
            return new CastVoteResponseDTO(false, CastVoteResponseDTO.UNEXPECTED_ERROR_MSG);
        }
        DebateSession debateSession = optDebateSession.get();
        if (debateSession.getDebateSessionPhase() != DebateSessionPhase.DEPUTY1_VOTING_TIME &&
            debateSession.getDebateSessionPhase() != DebateSessionPhase.DEPUTY2_VOTING_TIME) {
            return new CastVoteResponseDTO(false,
                    CastVoteResponseDTO.PHASE_PASSED_MSG);
        }

        System.out.println("Searched name: " + deputyCandidateDTO.getUserName());
        System.out.println("Players: " + debateSession.getPlayers());
        List<DebateSessionPlayer> selectedPlayersList = debateSession.getPlayers()
                .stream()
                .filter(player -> player.getPlayerRole().equals(PlayerRole.NONE))
                .filter(player -> player.getUser().getUserName().equals(deputyCandidateDTO.getUserName()))
                .collect(Collectors.toList());

        if (selectedPlayersList.size() != 1) {
            System.out.println(selectedPlayersList);
            return new CastVoteResponseDTO(false, CastVoteResponseDTO.UNEXPECTED_ERROR_MSG);
        }
        DebateSessionPlayer selectedPlayer = selectedPlayersList.get(0);

        DebateRoleVote vote = new DebateRoleVote();
        vote.setDebateSession(debateSession);
        vote.setForPlayer(selectedPlayer);
        if (debateSession.getDebateSessionPhase().equals(DebateSessionPhase.DEPUTY1_VOTING_TIME)) {
            vote.setForPlayerRole(PlayerRole.DEPUTY1);
        } else if (debateSession.getDebateSessionPhase().equals(DebateSessionPhase.DEPUTY2_VOTING_TIME)) {
            vote.setForPlayerRole(PlayerRole.DEPUTY2);
        } else {
            System.out.println(debateSession.getDebateSessionPhase());
            return new CastVoteResponseDTO(false, CastVoteResponseDTO.UNEXPECTED_ERROR_MSG);
        }
        // TODO: add to session/player
        roleVotesRepository.save(vote);

        return new CastVoteResponseDTO(true, "");
    }

    private List<DeputyCandidateDTO> getNextDeputyCandidates(DebateSessionPlayer usersPlayer,
                                                             DebateSession debateSession) {

        // Select players in the same team as the current user, who do not have a role assigned
        // yet and are not the same as the current user
        return debateSession.getPlayers()
                .stream()
                .filter(player -> !player.getUser().equals(getCurrentUser()))
                .filter(player -> player.getPlayerRole().equals(PlayerRole.NONE))
                .filter(player -> player.getTeam().equals(usersPlayer.getTeam()))
                .map(player -> new DeputyCandidateDTO(player.getUser().getUserName()))
                .collect(Collectors.toList());
    }

    private Optional<DebateSession> getOngoingDebate(User user) {
        List<DebateSession> ongoingDebatesAsJudge =
                debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        List<DebateSession> ongoingDebatesAsPlayer =
                debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED);
        if (ongoingDebatesAsJudge.isEmpty() && ongoingDebatesAsPlayer.isEmpty()) {
            return Optional.empty();
        }
        if (ongoingDebatesAsJudge.size() > 0) {
            if (ongoingDebatesAsJudge.size() > 1) {
                return Optional.empty();
            }
            return Optional.of(ongoingDebatesAsJudge.get(0));
        } else {
            if (ongoingDebatesAsPlayer.size() > 1) {
                return Optional.empty();
            }
            return Optional.of(ongoingDebatesAsPlayer.get(0));
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }

    private boolean isCurrentUserJudge(User user) {
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                DebateSessionPhase.FINISHED).size() == 1;
    }

    private DebateSessionPlayer findPlayerOfUserInDebateSession(User user, DebateSession session) {
        return session.getPlayers()
                .stream()
                .filter(player -> player.getUser().equals(getCurrentUser()))
                .collect(Collectors.toList())
                .get(0);
    }
}
