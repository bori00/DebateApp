package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;

/*
TODO: this should be deleted by the end of the development process, when all States are
 implemented. I instroduced it for now just to provide a general DebateState implementation for
 the states that we didn't handle yet. Please, when you implement the handlers for a new state,
 create the corresponding classes, and add them into the DebateSessionPhase enum as well.
 */
public class GeneralState implements DebateState {
    private static GeneralState instance = null;

    private GeneralState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new GeneralState();
        }
        return instance;
    }

    @Override
    public String getRedirectionTargetOnEndOfState() {
        return "/";
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return null;
    }
}
