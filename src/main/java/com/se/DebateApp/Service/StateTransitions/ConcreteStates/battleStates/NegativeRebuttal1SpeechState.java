package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;

public class NegativeRebuttal1SpeechState extends BattleSpeechState{
    private static NegativeRebuttal1SpeechState instance;

    private NegativeRebuttal1SpeechState() {
    }

    public static NegativeRebuttal1SpeechState getInstance() {
        if (instance == null) {
            instance = new NegativeRebuttal1SpeechState();
        }
        return instance;
    }
    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.AFFIRMATIVE_REBUTTAL_1;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.NEGATIVE_REBUTTAL_1;
    }
}
