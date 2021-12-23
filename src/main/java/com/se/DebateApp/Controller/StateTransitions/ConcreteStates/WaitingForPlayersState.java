package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;

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
    public String getRedirectTargetOnStateBegin() {
        return null;
    }

    @Override
    public void onEndOfState() {

    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded() {
        // TODO
        return DebateSessionPhase.FINISHED;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.WAITING_FOR_PLAYERS;
    }
}
