package com.se.DebateApp.Controller.StateTransitions;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.stream.Collectors;

public interface DebateState {

    String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player);

    String getJudgesRedirectTargetOnStateEnter();

    default void onBeginningOfState(DebateSession debateSession,
                             NotificationService notificationService) {}

    /**
     * Performs any changes needed on the debateSession (including setting its phase to the next
     * one), notifies anyone to be notified and saves the new state of the DebateSession in the
     * database.
     *
     * Note: refers only to the normal ending of the state, not to its interruption by closing
     * the debate.
     */
    default void onEndOfState(DebateSession debateSession,
                              NotificationService notificationService,
                              DebateSessionRepository debateSessionRepository) {
        debateSession.setDebateSessionPhase(getNextDebateSessionPhaseAfterStateEnded(debateSession));
        debateSessionRepository.save(debateSession);
    }

    DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession);

    DebateSessionPhase getCorrespondingDebateSessionPhase();

    default void announceAllDebatePlayersAboutEndOfTimeInterval(DebateSession debateSession,
                                                                NotificationService notificationService) {
        String destinationUrl =
                String.format(NotificationService.DEBATE_TIMED_INTERVAL_IS_UP_SOCKET_TEMPLATE,
                        getCorrespondingDebateSessionPhase().name().toLowerCase().replace('_',
                                '-'));
        notificationService.notifyUsers(
                debateSession.getPlayers().stream().map(DebateSessionPlayer::getUser).collect(Collectors.toList()),
                "closed",
                destinationUrl);
    }
}
