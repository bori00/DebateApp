package com.se.DebateApp.Controller.StateTransitions;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;

public interface DebateState {

    String getRedirectTargetOnStateBegin();

    default void onEndOfState() {}

    DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded();

    DebateSessionPhase getCorrespondingDebateSessionPhase();
}
