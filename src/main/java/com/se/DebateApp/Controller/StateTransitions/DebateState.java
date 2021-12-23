package com.se.DebateApp.Controller.StateTransitions;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Repository.DebateSessionRepository;

public interface DebateState {

    String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player);

    String getJudgesRedirectTargetOnStateEnter();

    default void onEndOfState() {}

    DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded();

    DebateSessionPhase getCorrespondingDebateSessionPhase();
}
