package com.se.DebateApp.Service.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;

import java.util.Date;

public class PreparationState implements DebateState {
    private static PreparationState instance = null;

    private PreparationState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new PreparationState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return SupportedMappings.GO_TO_DEBATE_PREPARATION;
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.GO_TO_DEBATE_PREPARATION;
    }

    @Override
    public void onEndOfState(DebateSession debateSession, NotificationService notificationService
            , DebateSessionRepository debateSessionRepository) {
        debateSession.setDebateSessionPhase(getNextDebateSessionPhaseAfterStateEnded(debateSession));
        debateSession.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        debateSessionRepository.save(debateSession);
        announceAllDebatePlayersAboutEndOfTimeInterval(debateSession, notificationService);
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        if (DebateSessionPhase.DEPUTY1_VOTING_TIME.getDefaultLengthInSeconds().isPresent() &&
                DebateSessionPhase.DEPUTY1_VOTING_TIME.getDefaultLengthInSeconds().get() > 0) {
            return DebateSessionPhase.DEPUTY1_VOTING_TIME;
        } else {
            return DebateSessionPhase.DEPUTY1_VOTING_TIME
                    .getCorrespondingState()
                    .getNextDebateSessionPhaseAfterStateEnded(debateSession);
        }
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.PREP_TIME;
    }
}
