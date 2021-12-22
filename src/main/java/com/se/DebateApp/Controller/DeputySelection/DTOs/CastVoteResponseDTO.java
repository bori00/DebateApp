package com.se.DebateApp.Controller.DeputySelection.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CastVoteResponseDTO {
    private final boolean success;
    private final String errorMsg;

    public static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred. Please retry" +
            " later.";
    public static final String PHASE_PASSED_MSG = "Seems like you missed the voting phase. We're " +
            "sorry.";
}
