package com.se.DebateApp.Model.Constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

public enum PlayerState {
    WAITING_TO_JOIN_TEAM("W"),
    JOINED_A_TEAM("J");

    private final String code;

    private PlayerState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class PlayerStateConverter implements AttributeConverter<PlayerState, String> {

        @Override
        public String convertToDatabaseColumn(PlayerState playerState) {
            if (playerState == null) {
                return null;
            }
            return playerState.getCode();
        }

        @Override
        public PlayerState convertToEntityAttribute(String code) {
            if (code == null) {
                return null;
            }

            return Stream.of(PlayerState.values())
                    .filter(c -> c.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
