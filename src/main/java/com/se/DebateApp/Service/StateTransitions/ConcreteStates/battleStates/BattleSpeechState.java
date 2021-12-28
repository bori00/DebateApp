package com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates;

import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.DebateState;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public void onSkipSpeech(DebateSession debateSession, NotificationService notificationService, DebateSessionRepository debateSessionRepository) {
        debateSession.setDebateSessionPhase(getNextDebateSessionPhaseAfterStateEnded(debateSession));
        debateSession.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        debateSessionRepository.save(debateSession);
        announceAllParticipantsAboutSkippedSpeech(debateSession, notificationService);
    }

    private void announceAllParticipantsAboutSkippedSpeech(DebateSession debateSession,
                                                            NotificationService notificationService) {
        User judge = debateSession.getDebateTemplate().getOwner();
        List<User> players = debateSession.getPlayers()
                .stream()
                .map(DebateSessionPlayer::getUser)
                .collect(Collectors.toList());

        notificationService.notifyUser(judge, "skip", NotificationService.SKIP_SPEECH_SOCKET_DEST);
        notificationService.notifyUsers(players, "skip", NotificationService.SKIP_SPEECH_SOCKET_DEST);
    }

    protected DebateSessionPhase getNextPhaseOrNextOneIfIntervalIsZero(DebateSession debateSession, DebateSessionPhase nextPhase) {
        if (getIntervalLengthOfGivenPhase(nextPhase, debateSession.getDebateTemplate()) > 0) {
            return nextPhase;
        } else {
            return nextPhase
                    .getCorrespondingState()
                    .getNextDebateSessionPhaseAfterStateEnded(debateSession);
        }
    }

    private long getIntervalLengthOfGivenPhase(DebateSessionPhase debateSessionPhase, DebateTemplate debateTemplate) {
        return switch (debateSessionPhase) {
            case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1, AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2, NEGATIVE_CONSTRUCTIVE_SPEECH_1, NEGATIVE_CONSTRUCTIVE_SPEECH_2 -> debateTemplate.getConstSpeechSeconds();
            case CROSS_EXAMINATION_1, CROSS_EXAMINATION_2, CROSS_EXAMINATION_3, CROSS_EXAMINATION_4 -> debateTemplate.getCrossExaminationSeconds();
            case AFFIRMATIVE_REBUTTAL_1, AFFIRMATIVE_REBUTTAL_2, NEGATIVE_REBUTTAL_1, NEGATIVE_REBUTTAL_2 -> debateTemplate.getRebuttalSpeechSeconds();
            default -> debateSessionPhase.getDefaultLengthInSeconds().orElse(0);
        };
    }
}
