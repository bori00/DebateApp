package com.se.DebateApp.Controller.DeputySelection.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeputyCandidatesListDTO {
    private final List<DeputyCandidateDTO> deputyCandidates;
    private final boolean success;
    private final String errorMsg;

    public static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred. Please retry" +
            " later.";
}
