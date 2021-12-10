package com.se.DebateApp.Model.Constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

public enum PlayerRole {
    NONE("N"),
    DEPUTY1("D1"),
    DEPUTY2("D2");

    private final String code;

    private PlayerRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class PlayerRoleConverter implements AttributeConverter<PlayerRole, String> {

        @Override
        public String convertToDatabaseColumn(PlayerRole playerRole) {
            if (playerRole == null) {
                return null;
            }
            return playerRole.getCode();
        }

        @Override
        public PlayerRole convertToEntityAttribute(String code) {
            if (code == null) {
                return null;
            }

            return Stream.of(PlayerRole.values())
                    .filter(c -> c.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
