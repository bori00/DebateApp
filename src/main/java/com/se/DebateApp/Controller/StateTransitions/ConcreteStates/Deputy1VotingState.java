package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;

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
