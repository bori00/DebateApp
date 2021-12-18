package com.se.DebateApp.Model.Constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

public enum MeetingType {
    ACTIVE("ACTIVE"),
    PREPARATION_PRO_TEAM("PREPARATION_PRO"),
    PREPARATION_CONTRA_TEAM("PREPARATION_CONTRA");

    private final String code;

    MeetingType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class MeetingTypeConverter implements AttributeConverter<MeetingType, String> {

        @Override
        public String convertToDatabaseColumn(MeetingType meetingType) {
            return (meetingType != null) ? meetingType.getCode() : null;
        }

        @Override
        public MeetingType convertToEntityAttribute(String code) {
            if (code == null) {
                return null;
            }

            return Stream.of(MeetingType.values())
                    .filter(c -> c.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
