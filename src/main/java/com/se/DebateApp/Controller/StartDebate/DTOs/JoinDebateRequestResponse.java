package com.se.DebateApp.Controller.StartDebate.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinDebateRequestResponse {

    public static final String HAS_OTHER_ONGOING_DEBATE_ERROR_MSG = "You can't join a new debate," +
            " " +
            "when you are a player/judge in an other ongoing debate. Please retry later!";

    public static final String DEBATE_NOT_FOUND_ERROR_MSG = "Seems like the " +
            "requested debate does not exist. Please verify that you entered the correct code.";

    public static final String DEBATE_NO_LONGER_ACCEPTING_PLAYERS_ERROR_MSG = "This debate no " +
            "longer accepts players. We're sorry, but you cannot join the debate.";

    private final boolean success;
    private final String errorMessage;

}
