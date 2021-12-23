package com.se.DebateApp.Controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OngoingDebateRequestResponse {
    private final boolean success;
    private final boolean debatePhaseRedirectNeeded;
    private final String errorMsg;

    public static final String HAS_OTHER_ONGOING_DEBATE_ERROR_MSG = "You can't join a new debate," +
            " " +
            "when you are a player/judge in an other ongoing debate. Please retry later!";

    public static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred. Please try " +
            "again!";

    public static final String TOO_LATE_TO_JOIN_TEAM_MSG = "We're sorry, you were too late. The " +
        "debate" +
            " was already activated, before you joined the team.";

    public static final String ALREADY_JOINED_TEAM_MSG = "Seems like you already joined a team. " +
            "You " +
            "cannot join a team twice/join two different teams in the same debate.";

    public static final String DEBATE_NOT_FOUND_ERROR_MSG = "Seems like the " +
            "requested debate does not exist. Please verify that you entered the correct code.";

    public static final String DEBATE_NO_LONGER_ACCEPTING_PLAYERS_ERROR_MSG = "This debate no " +
            "longer accepts players. We're sorry, but you cannot join the debate.";
}
