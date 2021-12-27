package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;

public class CrossExamination4SpeechState extends BattleSpeechState{
    private static CrossExamination4SpeechState instance;

    private CrossExamination4SpeechState() {
    }

    public static CrossExamination4SpeechState getInstance() {
        if (instance == null) {
            instance = new CrossExamination4SpeechState();
        }
        return instance;
    }
    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.NEGATIVE_REBUTTAL_1;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.CROSS_EXAMINATION_4;
    }
}
