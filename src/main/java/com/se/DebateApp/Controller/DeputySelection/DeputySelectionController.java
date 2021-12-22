package com.se.DebateApp.Controller.DeputySelection;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.DeputySelection.DTOs.DeputyCandidateDTO;
import com.se.DebateApp.Controller.DeputySelection.DTOs.DeputyCandidatesListDTO;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionPlayerRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/get_deputy1_candidates")
    @ResponseBody
    DeputyCandidatesListDTO getDeputy1Candidates() {
        User user = getCurrentUser();
        Optional<DebateSession> optDebateSession = getOngoingDebate(user);
        if (optDebateSession.isEmpty()) {
            return new DeputyCandidatesListDTO(new ArrayList<>(), false,
                    DeputyCandidatesListDTO.UNEXPECTED_ERROR_MSG);
        }
        DebateSession debateSession = optDebateSession.get();
        if (debateSession.getDebateSessionPhase() != DebateSessionPhase.DEPUTY1_VOTING_TIME) {
            return new DeputyCandidatesListDTO(new ArrayList<>(), false,
                    DeputyCandidatesListDTO.PHASE_PASSED_MSG);
        }
        DebateSessionPlayer usersPlayer =
                debateSession.getPlayers()
                        .stream()
                        .filter(player -> player.getUser().equals(getCurrentUser()))
                        .collect(Collectors.toList())
                        .get(0);

        List<DeputyCandidateDTO> candidatePlayersInUsersTeam =
                debateSession.getPlayers()
                        .stream()
                        .filter(player -> !player.getUser().equals(getCurrentUser()))
                        .filter(player -> !player.getTeam().equals(usersPlayer.getTeam()))
                        .map(player -> new DeputyCandidateDTO(player.getUser().getUserName()))
                        .collect(Collectors.toList());

        return new DeputyCandidatesListDTO(candidatePlayersInUsersTeam, true, "");
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
}
