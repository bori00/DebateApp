package com.se.DebateApp.Controller.DeputySelection.DTOs;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class DeputyVotingStatusDTO {
    @Getter
    @AllArgsConstructor
    public static class CandidateVotingStatus {
        private final String name;
        private final Long noVotes;
    }

    private final String proDeputy1Name;
    private final String proDeputy2Name;
    private final String conDeputy1Name;
    private final String conDeputy2Name;

    private final List<CandidateVotingStatus> currentVotingPhaseVotesPro;
    private final List<CandidateVotingStatus> currentVotingPhaseVotesCon;

    public static final String MISISNG_DEPUTY_NAME = "-";

    public DeputyVotingStatusDTO(DebateSession debateSession) {
        proDeputy1Name =
                debateSession.getDeputy(TeamType.PRO, PlayerRole.DEPUTY1)
                        .map(debateSessionPlayer -> debateSessionPlayer.getUser().getUserName())
                        .orElse(MISISNG_DEPUTY_NAME);
        proDeputy2Name =
                debateSession.getDeputy(TeamType.PRO, PlayerRole.DEPUTY2)
                        .map(debateSessionPlayer -> debateSessionPlayer.getUser().getUserName())
                        .orElse(MISISNG_DEPUTY_NAME);
        conDeputy1Name =
                debateSession.getDeputy(TeamType.CON, PlayerRole.DEPUTY1)
                        .map(debateSessionPlayer -> debateSessionPlayer.getUser().getUserName())
                        .orElse(MISISNG_DEPUTY_NAME);
        conDeputy2Name =
                debateSession.getDeputy(TeamType.CON, PlayerRole.DEPUTY2)
                        .map(debateSessionPlayer -> debateSessionPlayer.getUser().getUserName())
                        .orElse(MISISNG_DEPUTY_NAME);

        currentVotingPhaseVotesPro =
                convertPlayersToVotesMapToCandidateStatusList(debateSession.getPlayersToNoVotesForDeputyRole(TeamType.PRO,
                debateSession.getDebateSessionPhase().equals(DebateSessionPhase.DEPUTY1_VOTING_TIME) ? PlayerRole.DEPUTY1 : PlayerRole.DEPUTY2));

        currentVotingPhaseVotesCon =
                convertPlayersToVotesMapToCandidateStatusList(debateSession.getPlayersToNoVotesForDeputyRole(TeamType.CON,
                        debateSession.getDebateSessionPhase().equals(DebateSessionPhase.DEPUTY1_VOTING_TIME) ? PlayerRole.DEPUTY1 : PlayerRole.DEPUTY2));
    }

    private static List<CandidateVotingStatus> convertPlayersToVotesMapToCandidateStatusList(Map<DebateSessionPlayer,
            Long> playersToVotesMap) {
        return playersToVotesMap
                .entrySet()
                .stream()
                .map(entry -> new CandidateVotingStatus(entry.getKey().getUser().getUserName(), entry.getValue()))
                .sorted((c1, c2) -> Long.compare(c2.getNoVotes(), c1.getNoVotes()))
                .collect(Collectors.toList());
    }
}
