package com.se.DebateApp.Controller.StartDebate.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class DebateParticipantsStatus {
    private final Integer noWaitingToJoinParticipants;
    private final Integer noAffirmativeParticipants;
    private final Integer noNegativeParticipants;
    private final Boolean canActivateSession;
}
