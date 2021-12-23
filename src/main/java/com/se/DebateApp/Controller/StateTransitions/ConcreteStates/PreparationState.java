package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import org.springframework.security.core.parameters.P;

public class PreparationState implements DebateState {
    private static PreparationState instance = null;

    private PreparationState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new PreparationState();
        }
        return instance;
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
