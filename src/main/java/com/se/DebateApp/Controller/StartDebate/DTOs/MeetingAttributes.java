package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = MeetingAttributesDeserializer.class)
public class MeetingAttributes {
    private Long debateSessionId;
    private String meetingName;
    private String meetingUrl;
}
