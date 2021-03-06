package com.se.DebateApp.Service.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;

import java.util.stream.Collectors;

public class FinishedState implements DebateState {
    private static FinishedState instance = null;

    private FinishedState() {
    }

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new FinishedState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        return SupportedMappings.GO_TO_STARTING_PAGE;
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.GO_TO_STARTING_PAGE;
    }

    @Override
    public void onBeginningOfState(DebateSession debateSession, NotificationService notificationService) {
        announceAllDebatePlayersAboutDebateClosed(debateSession, notificationService);
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        return DebateSessionPhase.FINISHED;
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.FINISHED;
    }

    private void announceAllDebatePlayersAboutDebateClosed(DebateSession session,
                                                           NotificationService notificationService) {
        notificationService.notifyUsers(
                session.getPlayers().stream()
                        .map(DebateSessionPlayer::getUser)
                        .collect(Collectors.toList()),
                "closed",
                NotificationService.DEBATE_SESSION_CLOSED_SOCKET_DEST);
    }
}
