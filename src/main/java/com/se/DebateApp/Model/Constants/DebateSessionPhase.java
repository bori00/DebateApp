package com.se.DebateApp.Model.Constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

public enum DebateSessionPhase {
    WAITING_FOR_PLAYERS("W"),
    PREP_TIME("P"),
    DEPUTY1_VOTING_TIME("DV1"),
    DEPUTY2_VOTING_TIME("DV2"),
    AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1("AC1"),
    CROSS_EXAMINATION_1("CX1"),
    NEGATIVE_CONSTRUCTIVE_SPEECH_1("NC1"),
    CROSS_EXAMINATION_2("CX2"),
    AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2("AC2"),
    CROSS_EXAMINATION_3("CX3"),
    NEGATIVE_CONSTRUCTIVE_SPEECH_2("NC2"),
    CROSS_EXAMINATION_4("CX4"),
    NEGATIVE_REBUTTAL_1("NR1"),
    AFFIRMATIVE_REBUTTAL_1("AR1"),
    NEGATIVE_REBUTTAL_2("NR2"),
    AFFIRMATIVE_REBUTTAL_2("AR2"),
    FINAL_VOTE("FV"),
    FINAL_DISCUSSION("FD");


    private final String code;

    private DebateSessionPhase(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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
