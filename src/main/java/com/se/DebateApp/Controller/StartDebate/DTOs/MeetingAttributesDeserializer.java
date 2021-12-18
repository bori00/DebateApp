package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class MeetingAttributesDeserializer  extends JsonDeserializer<MeetingAttributes> {
    @Override
    public MeetingAttributes deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        MeetingAttributes meetingAttributes = new MeetingAttributes();

        try{
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            meetingAttributes.setDebateSessionId(node.get("debateSessionId").longValue());
            meetingAttributes.setMeetingName(node.get("meetingName").asText());
            meetingAttributes.setMeetingUrl(node.get("meetingUrl").asText());
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return meetingAttributes;
    }
}