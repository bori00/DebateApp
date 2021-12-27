package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;

import java.util.Date;

public class AffirmativeConstructive1SpeechState extends BattleSpeechState {
    private static AffirmativeConstructive1SpeechState instance;

    private AffirmativeConstructive1SpeechState() {
    }

    public static AffirmativeConstructive1SpeechState getInstance() {
        if (instance == null) {
            instance = new AffirmativeConstructive1SpeechState();
        }
        return instance;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1;
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.CROSS_EXAMINATION_1;
    }
}
