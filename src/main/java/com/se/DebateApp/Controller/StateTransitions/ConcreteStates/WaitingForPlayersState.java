package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Repository.DebateSessionRepository;

public class WaitingForPlayersState implements DebateState {
    private static WaitingForPlayersState instance = null;

    private WaitingForPlayersState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new WaitingForPlayersState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        if (player.getTeam() == null) {
            return SupportedMappings.PLAYER_GO_TO_CHOOSE_TEAM_REQUEST;
        } else {
            return SupportedMappings.PLAYER_GO_TO_DEBATE_LOBBY_REQUEST;
        }
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.JUDGE_GO_TO_STARTING_DEBATE_REQUEST;
    }

    @Override
    public void onEndOfState() {

    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded() {
        return DebateSessionPhase.PREP_TIME;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.WAITING_FOR_PLAYERS;
    }
}
