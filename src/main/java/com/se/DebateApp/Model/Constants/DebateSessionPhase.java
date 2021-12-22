package com.se.DebateApp.Model.Constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;
import java.util.stream.Stream;

public enum DebateSessionPhase {
    WAITING_FOR_PLAYERS("W", -1),
    PREP_TIME("P", -1),
    DEPUTY1_VOTING_TIME("DV1", 90),
    DEPUTY2_VOTING_TIME("DV2", 90),
    AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1("AC1", -1),
    CROSS_EXAMINATION_1("CX1", -1),
    NEGATIVE_CONSTRUCTIVE_SPEECH_1("NC1", -1),
    CROSS_EXAMINATION_2("CX2", -1),
    AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2("AC2", -1),
    CROSS_EXAMINATION_3("CX3", -1),
    NEGATIVE_CONSTRUCTIVE_SPEECH_2("NC2", -1),
    CROSS_EXAMINATION_4("CX4", -1),
    NEGATIVE_REBUTTAL_1("NR1", -1),
    AFFIRMATIVE_REBUTTAL_1("AR1", -1),
    NEGATIVE_REBUTTAL_2("NR2", -1),
    AFFIRMATIVE_REBUTTAL_2("AR2", -1),
    FINAL_VOTE("FV", -1),
    FINAL_DISCUSSION("FD", -1),
    FINISHED("-", -1);


    private final String code;
    private final int defaultLengthInSeconds;

    private DebateSessionPhase(String code, int defaultLengthInSeconds) {
        this.code = code;
        this.defaultLengthInSeconds = defaultLengthInSeconds;
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
