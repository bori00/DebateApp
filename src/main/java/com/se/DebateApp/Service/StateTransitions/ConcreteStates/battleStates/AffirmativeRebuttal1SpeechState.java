package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class AffirmativeRebuttal1SpeechState extends BattleSpeechState{
    private static AffirmativeRebuttal1SpeechState instance;

    private AffirmativeRebuttal1SpeechState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new AffirmativeRebuttal1SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return getNextPhaseOrNextOneIfIntervalIsZero(debateSession, DebateSessionPhase.NEGATIVE_REBUTTAL_2);
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.AFFIRMATIVE_REBUTTAL_1;
    }
}
