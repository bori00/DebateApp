package com.se.DebateApp.Controller.FinalVote;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.DeputySelection.DTOs.DeputyCandidateDTO;
import com.se.DebateApp.Controller.FinalVote.DTOs.FinalVoteStatusDTO;
import com.se.DebateApp.Controller.OngoingDebateRequestResponse;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DebateRoleVote;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class FinalVoteController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateSessionPlayerRepository debateSessionPlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(SupportedMappings.CAST_FINAL_VOTE)
    @ResponseBody
    OngoingDebateRequestResponse castDeputyVote(@RequestBody boolean votesForPro) {
        User user = getCurrentUser();
        Optional<DebateSession> optDebateSession = getOngoingDebate(user);
        if (optDebateSession.isEmpty()) {
            return new OngoingDebateRequestResponse(false,
                    false, OngoingDebateRequestResponse.UNEXPECTED_ERROR_MSG);
        }
        DebateSession debateSession = optDebateSession.get();
        if (debateSession.getDebateSessionPhase() != DebateSessionPhase.FINAL_VOTE) {
            return new OngoingDebateRequestResponse(false, false,
                    OngoingDebateRequestResponse.VOTING_PHASE_PASSED_MSG);
        }
        DebateSessionPlayer player = debateSession.getPlayers().stream()
                        .filter(p -> p.getUser().equals(user))
                        .collect(Collectors.toList()).get(0);
        if (votesForPro) {
            player.setFinalVoteTeam(TeamType.PRO);
        } else {
            player.setFinalVoteTeam(TeamType.CON);
        }
        debateSessionPlayerRepository.save(player);
        return new OngoingDebateRequestResponse(true, false,"");
    }

    @PostMapping(SupportedMappings.GET_FINAL_VOTE_STATUS)
    @ResponseBody
    FinalVoteStatusDTO getFinalVotestatus() {
        User user = getCurrentUser();
        DebateSession debateSession = getOngoingDebate(user).get();
        return new FinalVoteStatusDTO(debateSession);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
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
}
