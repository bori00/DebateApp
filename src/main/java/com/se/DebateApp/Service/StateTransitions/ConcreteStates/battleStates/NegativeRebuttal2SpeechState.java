package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;

public class NegativeRebuttal2SpeechState extends BattleSpeechState{
    private static NegativeRebuttal2SpeechState instance;

    private NegativeRebuttal2SpeechState() {
    }

    public static NegativeRebuttal2SpeechState getInstance() {
        if (instance == null) {
            instance = new NegativeRebuttal2SpeechState();
        }
        return instance;
    }
    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.AFFIRMATIVE_REBUTTAL_2;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.NEGATIVE_REBUTTAL_2;
    }
}
