package com.se.DebateApp.Controller.DeputySelection;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.DeputySelection.DTOs.CastVoteResponseDTO;
import com.se.DebateApp.Controller.DeputySelection.DTOs.DeputyCandidateDTO;
import com.se.DebateApp.Controller.DeputySelection.DTOs.DeputyCandidatesListDTO;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        if (isCurrentUserJudge()) {
            return "deputy_selection_for_judge";
        } else {
            return "deputy_selection_for_players";
        }
    }

    @PostMapping("/get_next_deputy_candidates")
    @ResponseBody
    DeputyCandidatesListDTO getNextDeputyCandidates() {
        User user = getCurrentUser();
        Optional<DebateSession> optDebateSession = getOngoingDebate(user);
        if (optDebateSession.isEmpty()) {
            return new DeputyCandidatesListDTO(new ArrayList<>(), false,
                    DeputyCandidatesListDTO.UNEXPECTED_ERROR_MSG);
        }
        DebateSession debateSession = optDebateSession.get();

        DebateSessionPlayer usersPlayer =
                debateSession.getPlayers()
                        .stream()
                        .filter(player -> player.getUser().equals(getCurrentUser()))
                        .collect(Collectors.toList())
                        .get(0);

        // Select players in the same team as the current user, who do not have a role assigned
        // yet and are not the same as the current user
        List<DeputyCandidateDTO> candidatePlayersInUsersTeam =
                debateSession.getPlayers()
                        .stream()
                        .filter(player -> !player.getUser().equals(getCurrentUser()))
                        .filter(player -> player.getPlayerRole().equals(PlayerRole.NONE))
                        .filter(player -> !player.getTeam().equals(usersPlayer.getTeam()))
                        .map(player -> new DeputyCandidateDTO(player.getUser().getUserName()))
                        .collect(Collectors.toList());

        return new DeputyCandidatesListDTO(candidatePlayersInUsersTeam, true, "");
    }

    @PostMapping("/cast_deputy_vote")
    @ResponseBody
    CastVoteResponseDTO castDeputyVote(@RequestParam String selectedCandidateName) {
        User user = getCurrentUser();
        Optional<DebateSession> optDebateSession = getOngoingDebate(user);
        if (optDebateSession.isEmpty()) {
            return new CastVoteResponseDTO(false, CastVoteResponseDTO.UNEXPECTED_ERROR_MSG);
        }
        DebateSession debateSession = optDebateSession.get();
        if (debateSession.getDebateSessionPhase() != DebateSessionPhase.DEPUTY1_VOTING_TIME) {
            return new CastVoteResponseDTO(false,
                    CastVoteResponseDTO.PHASE_PASSED_MSG);
        }

        List<DebateSessionPlayer> selectedPlayersList = debateSession.getPlayers()
                .stream()
                .filter(player -> player.getPlayerRole().equals(PlayerRole.NONE))
                .filter(player -> player.getUser().getUserName().equals(selectedCandidateName))
                .collect(Collectors.toList());

        if (selectedPlayersList.size() != 1) {
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
            return new CastVoteResponseDTO(false, CastVoteResponseDTO.UNEXPECTED_ERROR_MSG);
        }
        // TODO: add to session/player
        roleVotesRepository.save(vote);

        return new CastVoteResponseDTO(true, "");
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

    private boolean isCurrentUserJudge() {
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), DebateSessionPhase.FINISHED).size() == 1;
    }
}
