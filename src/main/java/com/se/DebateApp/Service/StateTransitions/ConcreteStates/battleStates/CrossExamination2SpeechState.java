package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;

public class CrossExamination2SpeechState extends BattleSpeechState{
    private static CrossExamination2SpeechState instance;

    private CrossExamination2SpeechState() {
    }

    public static CrossExamination2SpeechState getInstance() {
        if (instance == null) {
            instance = new CrossExamination2SpeechState();
        }
        return instance;
    }
    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.CROSS_EXAMINATION_2;
    }
}
