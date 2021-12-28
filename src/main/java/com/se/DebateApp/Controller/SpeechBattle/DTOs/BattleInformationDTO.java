package com.se.DebateApp.Controller.SpeechBattle.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BattleInformationDTO {
    private Boolean isSpeaker;
    private Boolean isNextSpeaker;
    private String currentPhase;
    private String nextPhase;
    private String instructions;
    private String deputy1Pro;
    private String deputy2Pro;
    private String deputy1Con;
    private String deputy2Con;
    private List<String> currentSpeakers;
    private List<String> nextSpeakers;
    private List<String> proTeamMembers;
    private List<String> conTeamMembers;
}
