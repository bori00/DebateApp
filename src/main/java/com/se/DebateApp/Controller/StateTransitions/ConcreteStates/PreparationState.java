package com.se.DebateApp.Controller.StateTransitions.ConcreteStates;

import com.se.DebateApp.Controller.StateTransitions.DebateState;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.DebateTemplate;
import org.springframework.security.core.parameters.P;

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
    public DebateSessionPhase getNextDebateSessionPhaseAfterStateEnded(DebateSession debateSession) {
        DebateTemplate debateTemplate = debateSession.getDebateTemplate();
        if (debateTemplate.getConstSpeechSeconds() > 0) {
            return DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1;
        } else {
            return DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1
                    .getCorrespondingState()
                    .getNextDebateSessionPhaseAfterStateEnded(debateSession);
        }
    }

    @Override
    public DebateSessionPhase getCorrespondingDebateSessionPhase() {
        return DebateSessionPhase.PREP_TIME;
    }
}
