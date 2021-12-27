package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;

import java.util.Date;

public abstract class BattleSpeechState implements DebateState {

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.GO_TO_BATTLE;
    }


    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return SupportedMappings.GO_TO_BATTLE;
    }

    @Override
    public void onEndOfState(DebateSession debateSession, NotificationService notificationService, DebateSessionRepository debateSessionRepository) {
        debateSession.setDebateSessionPhase(getNextDebateSessionPhaseAfterStateEnded(debateSession));
        debateSession.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        debateSessionRepository.save(debateSession);
        announceAllDebatePlayersAboutEndOfTimeInterval(debateSession, notificationService);
    }
}
