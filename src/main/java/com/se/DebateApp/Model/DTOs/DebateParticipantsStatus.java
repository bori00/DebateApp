package com.se.DebateApp.Model.DTOs;

import com.se.DebateApp.Model.Constants.DebateConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DebateParticipantsStatus {
    private final Integer noWaitingToJoinParticipants;
    private final Integer noProParticipants;
    private final Integer noConParticipants;
    private final Boolean canActivateSession;

    public DebateParticipantsStatus(Integer noWaitingToJoinParticipants, Integer noProParticipants, Integer noConParticipants) {
        this.noWaitingToJoinParticipants = noWaitingToJoinParticipants;
        this.noProParticipants = noProParticipants;
        this.noConParticipants = noConParticipants;
        this.canActivateSession =
                this.noProParticipants >= DebateConstants.MIN_PLAYERS_PER_TEAM &&
                this.noConParticipants >= DebateConstants.MIN_PLAYERS_PER_TEAM;
    }
}
