package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateConstants;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Deputy1VotingState implements DebateState {
    private static Deputy1VotingState instance = null;

    private Deputy1VotingState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new Deputy1VotingState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return SupportedMappings.GO_TO_DEPUTY_SELECTION;
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.GO_TO_DEPUTY_SELECTION;
    }

    @Override
    public void onEndOfState(DebateSession debateSession, NotificationService notificationService) {
        announceAllDebatePlayersAboutEndOfTimeInterval(debateSession, notificationService);
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        if (DebateSessionPhase.DEPUTY2_VOTING_TIME.getDefaultLengthInSeconds().isPresent() &&
                DebateSessionPhase.DEPUTY2_VOTING_TIME.getDefaultLengthInSeconds().get() > 0) {
            return DebateSessionPhase.DEPUTY2_VOTING_TIME;
        } else {
            return DebateSessionPhase.DEPUTY2_VOTING_TIME
                    .getCorrespondingState()
                    .getNextDebateSessionPhaseAfterStateEnded(debateSession);
        }
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.DEPUTY1_VOTING_TIME;
    }
}
