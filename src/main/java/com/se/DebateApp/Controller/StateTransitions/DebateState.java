package com.se.DebateApp.Controller.StateTransitions;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface DebateState {

    String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player);

    String getJudgesRedirectTargetOnStateEnter();

    default void onEndOfState(DebateSession debateSession, NotificationService notificationService) {}

    DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession);

    DebateSessionPhase getCorrespondingDebateSessionPhase();
}
