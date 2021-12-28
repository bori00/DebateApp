package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class CrossExamination2SpeechState extends BattleSpeechState{
    private static CrossExamination2SpeechState instance;

    private CrossExamination2SpeechState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new CrossExamination2SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return getNextPhaseOrNextOneIfIntervalIsZero(debateSession, DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2);
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.CROSS_EXAMINATION_2;
    }
}
