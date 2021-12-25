package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.se.DebateApp.Model.DebateTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DebateLobbyInformationDTO {
    private final String title;
    private final String statement;

    public DebateLobbyInformationDTO(DebateTemplate debateTemplate) {
        this.title = debateTemplate.getTitle();
        this.statement = debateTemplate.getStatement();
    }
}
