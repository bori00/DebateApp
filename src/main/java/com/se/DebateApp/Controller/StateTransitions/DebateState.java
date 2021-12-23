package com.se.DebateApp.Controller.StateTransitions;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;

public interface DebateState {

    default void onEndOfState() {}

    String getRedirectionTargetOnEndOfState();

    DebateSessionPhase getCorrespondingDebateSessionPhase();
}
