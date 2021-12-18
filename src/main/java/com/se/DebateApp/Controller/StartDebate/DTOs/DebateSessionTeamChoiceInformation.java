package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.se.DebateApp.Model.DebateTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DebateSessionTeamChoiceInformation {
    private final String title;
    private final String statement;

    public DebateSessionTeamChoiceInformation(DebateTemplate debateTemplate) {
        this.title = debateTemplate.getTitle();
        this.statement = debateTemplate.getStatement();
    }
}
