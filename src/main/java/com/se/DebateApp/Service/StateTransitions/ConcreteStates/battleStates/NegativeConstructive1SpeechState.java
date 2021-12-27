package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;

public class NegativeConstructive1SpeechState extends BattleSpeechState{
    private static NegativeConstructive1SpeechState instance;

    private NegativeConstructive1SpeechState() {
    }

    public static NegativeConstructive1SpeechState getInstance() {
        if (instance == null) {
            instance = new NegativeConstructive1SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.CROSS_EXAMINATION_2;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.NEGATIVE_CONSTRUCTIVE_SPEECH_1;
    }
}
