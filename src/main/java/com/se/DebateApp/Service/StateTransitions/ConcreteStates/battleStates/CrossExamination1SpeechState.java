package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class CrossExamination1SpeechState extends BattleSpeechState{
    private static CrossExamination1SpeechState instance;

    private CrossExamination1SpeechState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new CrossExamination1SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return getNextPhaseOrNextOneIfIntervalIsZero(debateSession, DebateSessionPhase.NEGATIVE_CONSTRUCTIVE_SPEECH_1);
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.CROSS_EXAMINATION_1;
    }
}
