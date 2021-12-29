package com.se.DebateApp.Service.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class FinalDiscussionsState implements DebateState {

    private static FinalDiscussionsState instance;

    private FinalDiscussionsState() {}

    public static DebateState getInstance() {
        if(instance == null) {
            instance = new FinalDiscussionsState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return SupportedMappings.GO_TO_BATTLE;
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.GO_TO_BATTLE;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.FINISHED;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.FINAL_DISCUSSION;
    }
}
