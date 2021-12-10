package com.se.DebateApp.Model.Constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

public enum TeamType{
    PRO("P"),
    CON("C");

    private final String code;

    private TeamType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class TeamTypeConverter implements AttributeConverter<TeamType, String> {

        @Override
        public String convertToDatabaseColumn(TeamType teamType) {
            if (teamType == null) {
                return null;
            }
            return teamType.getCode();
        }

        @Override
        public TeamType convertToEntityAttribute(String code) {
            if (code == null) {
                return null;
            }

            return Stream.of(TeamType.values())
                    .filter(c -> c.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
