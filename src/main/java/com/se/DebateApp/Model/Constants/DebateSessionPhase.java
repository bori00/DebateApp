package com.se.DebateApp.Model.Constants;

import com.se.DebateApp.Service.StateTransitions.*;
import com.se.DebateApp.Service.StateTransitions.ConcreteStates.*;
import com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates.*;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;
import java.util.stream.Stream;

public enum DebateSessionPhase {
    WAITING_FOR_PLAYERS("W", -1, WaitingForPlayersState.getInstance()),
    PREP_TIME("P", -1, PreparationState.getInstance()),
    DEPUTY1_VOTING_TIME("DV1", 60, Deputy1VotingState.getInstance()),
    DEPUTY2_VOTING_TIME("DV2", 60, Deputy2VotingState.getInstance()),
    AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1("AC1", -1, AffirmativeConstructive1SpeechState.getInstance()),
    CROSS_EXAMINATION_1("CX1", -1, CrossExamination1SpeechState.getInstance()),
    NEGATIVE_CONSTRUCTIVE_SPEECH_1("NC1", -1, NegativeConstructive1SpeechState.getInstance()),
    CROSS_EXAMINATION_2("CX2", -1, CrossExamination2SpeechState.getInstance()),
    AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2("AC2", -1, AffirmativeConstructive2SpeechState.getInstance()),
    CROSS_EXAMINATION_3("CX3", -1, CrossExamination3SpeechState.getInstance()),
    NEGATIVE_CONSTRUCTIVE_SPEECH_2("NC2", -1, NegativeConstructive2SpeechState.getInstance()),
    CROSS_EXAMINATION_4("CX4", -1, CrossExamination4SpeechState.getInstance()),
    NEGATIVE_REBUTTAL_1("NR1", -1, NegativeRebuttal1SpeechState.getInstance()),
    AFFIRMATIVE_REBUTTAL_1("AR1", -1, AffirmativeRebuttal1SpeechState.getInstance()),
    NEGATIVE_REBUTTAL_2("NR2", -1, NegativeRebuttal2SpeechState.getInstance()),
    AFFIRMATIVE_REBUTTAL_2("AR2", -1, AffirmativeRebuttal2SpeechState.getInstance()),
    FINAL_VOTE("FV", 60, FinalVoteState.getInstance()),
    FINAL_DISCUSSION("FD", -1, FinalDiscussionsState.getInstance()),
    FINISHED("-", -1, FinishedState.getInstance());


    private final String code;
    private final int defaultLengthInSeconds;
    private final DebateState correspondingState;

    private DebateSessionPhase(String code, int defaultLengthInSeconds, DebateState correspondingState) {
        this.code = code;
        this.defaultLengthInSeconds = defaultLengthInSeconds;
        this.correspondingState = correspondingState;
    }

    public String getCode() {
        return code;
    }

    public Optional<Integer> getDefaultLengthInSeconds() {
        if (defaultLengthInSeconds >= 0) {
            return Optional.of(defaultLengthInSeconds);
        } else {
            return Optional.empty();
        }
    }

    public DebateState getCorrespondingState() {
        return correspondingState;
    }

    @Converter(autoApply = true)
    public static class DebateSessionPhaseConverter implements AttributeConverter<DebateSessionPhase, String> {

        @Override
        public String convertToDatabaseColumn(DebateSessionPhase debateSessionPhase) {
            if (debateSessionPhase == null) {
                return null;
            }
            return debateSessionPhase.getCode();
        }

        @Override
        public DebateSessionPhase convertToEntityAttribute(String code) {
            if (code == null) {
                return null;
            }

            return Stream.of(DebateSessionPhase.values())
                    .filter(c -> c.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
