package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.se.DebateApp.Model.Constants.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = DebateMeetingAttributesDeserializer.class)
public class DebateMeetingAttributes {
    private Long debateSessionId;
    private String meetingName;
    private String meetingUrl;
    private MeetingType meetingType;
}
