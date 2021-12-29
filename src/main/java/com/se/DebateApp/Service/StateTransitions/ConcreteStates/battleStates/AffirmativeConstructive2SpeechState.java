package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class AffirmativeConstructive2SpeechState extends BattleSpeechState {
    private static AffirmativeConstructive2SpeechState instance;

    private AffirmativeConstructive2SpeechState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new AffirmativeConstructive2SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return getNextPhaseOrNextOneIfIntervalIsZero(debateSession, DebateSessionPhase.CROSS_EXAMINATION_3);
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2;
    }
}
