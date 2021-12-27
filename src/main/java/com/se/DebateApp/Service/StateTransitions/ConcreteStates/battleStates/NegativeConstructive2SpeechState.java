package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;

public class NegativeConstructive2SpeechState extends BattleSpeechState{
    private static NegativeConstructive2SpeechState instance;

    private NegativeConstructive2SpeechState() {
    }

    public static NegativeConstructive2SpeechState getInstance() {
        if (instance == null) {
            instance = new NegativeConstructive2SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.CROSS_EXAMINATION_4;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.NEGATIVE_CONSTRUCTIVE_SPEECH_2;
    }
}
