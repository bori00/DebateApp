package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;

public class Deputy2VotingState implements DebateState {
    private static Deputy2VotingState instance = null;

    private Deputy2VotingState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new Deputy2VotingState();
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
        return null;
    }
}
