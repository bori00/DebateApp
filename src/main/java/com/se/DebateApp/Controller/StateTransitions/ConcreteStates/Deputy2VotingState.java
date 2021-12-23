package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return null;
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return null;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return null;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return null;
    }
}
