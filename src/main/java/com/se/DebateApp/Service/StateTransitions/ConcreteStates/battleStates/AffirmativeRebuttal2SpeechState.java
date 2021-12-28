package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Service.StateTransitions.DebateState;

public class AffirmativeRebuttal2SpeechState extends BattleSpeechState{
    private static AffirmativeRebuttal2SpeechState instance;

    private AffirmativeRebuttal2SpeechState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new AffirmativeRebuttal2SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return getNextPhaseOrNextOneIfIntervalIsZero(debateSession, DebateSessionPhase.FINAL_VOTE);
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.AFFIRMATIVE_REBUTTAL_2;
    }
}
