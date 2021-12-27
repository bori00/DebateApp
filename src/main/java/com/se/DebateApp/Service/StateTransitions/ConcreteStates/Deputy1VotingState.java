package com.se.DebateApp.Service.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;

import java.util.*;
import java.util.stream.Collectors;

public class Deputy1VotingState implements DebateState {
    private static Deputy1VotingState instance = null;

    private Deputy1VotingState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new Deputy1VotingState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return SupportedMappings.GO_TO_DEPUTY_SELECTION;
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.GO_TO_DEPUTY_SELECTION;
    }

    @Override
    public void onEndOfState(DebateSession debateSession, NotificationService notificationService
            , DebateSessionRepository debateSessionRepository) {
        DebateSessionPlayer deputy1Pro = getDeputy1(debateSession, TeamType.PRO);
        deputy1Pro.setPlayerRole(PlayerRole.DEPUTY1);
        DebateSessionPlayer deputy1Con = getDeputy1(debateSession, TeamType.CON);
        deputy1Con.setPlayerRole(PlayerRole.DEPUTY1);
        debateSession.setDebateSessionPhase(getNextDebateSessionPhaseAfterStateEnded(debateSession));
        debateSession.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        debateSessionRepository.save(debateSession);;
        announceAllDebatePlayersAboutEndOfTimeInterval(debateSession, notificationService);
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        if (DebateSessionPhase.DEPUTY2_VOTING_TIME.getDefaultLengthInSeconds().isPresent() &&
                DebateSessionPhase.DEPUTY2_VOTING_TIME.getDefaultLengthInSeconds().get() > 0) {
            return DebateSessionPhase.DEPUTY2_VOTING_TIME;
        } else {
            return DebateSessionPhase.DEPUTY2_VOTING_TIME
                    .getCorrespondingState()
                    .getNextDebateSessionPhaseAfterStateEnded(debateSession);
        }
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.DEPUTY1_VOTING_TIME;
    }

    private DebateSessionPlayer getDeputy1(DebateSession debateSession, TeamType teamType) {
        Set<Map.Entry<DebateSessionPlayer, Long>> playersToNoVotes =
                debateSession.getPlayersToNoVotesForDeputyRole(teamType, PlayerRole.DEPUTY1)
                .entrySet();

        if (playersToNoVotes.size() == 0) {
            List<DebateSessionPlayer> playersWithNoRoleList =
                    debateSession.getPlayers().stream()
                            .filter(p -> p.getTeam().equals(teamType))
                            .filter(p -> p.getPlayerRole().equals(PlayerRole.NONE))
                            .collect(Collectors.toList());
            int num = (int) (Math.random() * playersWithNoRoleList.size());
            return playersWithNoRoleList.get(num);
        }

        Long maxNoVotes =
                playersToNoVotes.stream().max(Comparator.comparingLong(Map.Entry::getValue)).get().getValue();

        return playersToNoVotes.stream().filter(entry -> entry.getValue().equals(maxNoVotes)).findAny().get().getKey();
    }
}
