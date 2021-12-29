package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class NegativeConstructive1SpeechState extends BattleSpeechState {
    private static NegativeConstructive1SpeechState instance;

    private NegativeConstructive1SpeechState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new NegativeConstructive1SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return getNextPhaseOrNextOneIfIntervalIsZero(debateSession, DebateSessionPhase.CROSS_EXAMINATION_2);
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.NEGATIVE_CONSTRUCTIVE_SPEECH_1;
    }
}
