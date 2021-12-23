package com.se.DebateApp.Controller.StartDebate.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartDebateRequestResponse {
    public static final String HAS_OTHER_ONGOING_DEBATE_ERROR_MSG = "You can't join a new debate," +
            " " +
            "when you are a player/judge in an other ongoing debate. Please retry later!";

    public static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred. Please try " +
            "again!";

    private final boolean success;
    private final String errorMessage;
}
