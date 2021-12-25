package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.se.DebateApp.Model.Constants.DebateConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DebateParticipantsStatusDTO {
    private final Integer noWaitingToJoinParticipants;
    private final Integer noProParticipants;
    private final Integer noConParticipants;
    private final Boolean canActivateSession;

    public DebateParticipantsStatusDTO(Integer noWaitingToJoinParticipants, Integer noProParticipants, Integer noConParticipants) {
        this.noWaitingToJoinParticipants = noWaitingToJoinParticipants;
        this.noProParticipants = noProParticipants;
        this.noConParticipants = noConParticipants;
        this.canActivateSession =
                this.noProParticipants >= DebateConstants.MIN_PLAYERS_PER_TEAM &&
                this.noConParticipants >= DebateConstants.MIN_PLAYERS_PER_TEAM;
    }
}
