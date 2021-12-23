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
    public void onEndOfState() {

    }

    @Override
    public String getRedirectionTargetOnEndOfState() {
        return null;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.WAITING_FOR_PLAYERS;
    }
}
