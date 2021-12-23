package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerState;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class WaitingForPlayersState implements DebateState {

    private static WaitingForPlayersState instance = null;

    private WaitingForPlayersState() {}

    public static DebateState getInstance() {
        if (instance == null) {
            instance = new WaitingForPlayersState();
        }
        return instance;
    }

    @Override
    public String getPlayersRedirectTargetOnStateEnter(DebateSessionPlayer player) {
        if (player.getTeam() == null) {
            return SupportedMappings.PLAYER_GO_TO_CHOOSE_TEAM_REQUEST;
        } else {
            return SupportedMappings.PLAYER_GO_TO_DEBATE_LOBBY_REQUEST;
        }
    }

    @Override
    public String getJudgesRedirectTargetOnStateEnter() {
        return SupportedMappings.JUDGE_GO_TO_STARTING_DEBATE_REQUEST;
    }

    @Override
    public void onEndOfState(DebateSession debateSession, NotificationService notificationService) {
        announceAllDebatePlayersAboutDebateActivation(debateSession, notificationService);
        removeSessionsPlayersWhoDidntJoinATeam(debateSession);
    }

    @Override
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        DebateTemplate debateTemplate = debateSession.getDebateTemplate();
        if (debateTemplate.getPrepTimeSeconds() > 0) {
            return DebateSessionPhase.PREP_TIME;
        } else {
            return DebateSessionPhase.PREP_TIME
                    .getCorrespondingState()
                    .getNextDebateSessionPhaseAfterStateEnded(debateSession);
        }
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.WAITING_FOR_PLAYERS;
    }

    private void removeSessionsPlayersWhoDidntJoinATeam(DebateSession session) {
        session.getPlayers().removeIf(player -> player.getPlayerState().equals(PlayerState.WAITING_TO_JOIN_TEAM));
    }

    private void announceAllDebatePlayersAboutDebateActivation(DebateSession debateSession,
                                                               NotificationService notificationService) {
        Collection<User> joinedPlayers =
                debateSession.getPlayers()
                        .stream()
                        .map(DebateSessionPlayer::getUser)
                        .collect(Collectors.toList());
        notificationService.notifyUsers(joinedPlayers, "activated", NotificationService.DEBATE_SESSION_ACTIVATED_SOCKET_DEST);
    }
}
