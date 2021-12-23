package com.se.DebateApp.Controller.StartDebate.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinTeamRequestResponse {
    public static final String TOO_LATE_TO_JOIN_TEAM = "We're sorry, you were too late. The debate" +
            " was already activated, before you joined the team.";

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again!";

    public static final String ALREADY_JOINED_TEAM = "Seems like you already joined a team. You " +
            "cannot join a team twice/join two different teams in the same debate.";

    private final boolean success;
    private final String errorMessage;
}
