package com.se.DebateApp.Model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebateMeetingDTO {
    private String meetingName;
    private String meetingUrl;
}
