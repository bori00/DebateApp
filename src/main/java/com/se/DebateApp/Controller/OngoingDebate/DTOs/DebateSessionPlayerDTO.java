package com.se.DebateApp.Controller.OngoingDebate.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebateSessionPlayerDTO {
    private Long id;
    private Long debateSessionId;
    private String playerState;
    private String playerRole;
    private String team;
}
