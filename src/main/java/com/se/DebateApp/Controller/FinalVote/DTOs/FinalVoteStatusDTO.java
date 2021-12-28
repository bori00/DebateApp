package com.se.DebateApp.Controller.FinalVote.DTOs;

import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DebateSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FinalVoteStatusDTO {
    private final long noPlayersWhoVotedProBothTimes;
    private final long noPlayersWhoVotedConBothTimes;
    private final long noPlayersWhoVotedProThenCon;
    private final long noPlayersWhoVotedConThenPro;

    public FinalVoteStatusDTO(DebateSession debateSession) {
        noPlayersWhoVotedProBothTimes = getNoPlayersWithVotes(TeamType.PRO, TeamType.PRO,
                debateSession);
        noPlayersWhoVotedConBothTimes = getNoPlayersWithVotes(TeamType.CON, TeamType.CON,
                debateSession);
        noPlayersWhoVotedProThenCon = getNoPlayersWithVotes(TeamType.PRO, TeamType.CON,
               debateSession);
        noPlayersWhoVotedConThenPro = getNoPlayersWithVotes(TeamType.CON, TeamType.PRO,
                debateSession);

    }

    private long getNoPlayersWithVotes(TeamType initVote, TeamType finalVote, DebateSession debateSession) {
        return debateSession.getPlayers().stream()
                .filter(p -> p.getTeam().equals(initVote))
                .filter(p -> p.getFinalVoteTeam() != null && p.getFinalVoteTeam().equals(finalVote))
                .count();
    }
}
