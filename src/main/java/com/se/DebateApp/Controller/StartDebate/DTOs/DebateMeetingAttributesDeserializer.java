package com.se.DebateApp.Controller.StartDebate.DTOs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.DebateApp.Model.Constants.MeetingType;

import java.io.IOException;

public class DebateMeetingAttributesDeserializer extends JsonDeserializer<DebateMeetingAttributesDTO> {
    @Override
    public DebateMeetingAttributesDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        DebateMeetingAttributesDTO debateMeetingAttributes = new DebateMeetingAttributesDTO();

        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            debateMeetingAttributes.setDebateSessionId(node.get("debateSessionId").longValue());
            debateMeetingAttributes.setMeetingName(node.get("meetingName").asText());
            debateMeetingAttributes.setMeetingUrl(node.get("meetingUrl").asText());
            MeetingType.MeetingTypeConverter converter = new MeetingType.MeetingTypeConverter();
            debateMeetingAttributes.setMeetingType(converter.convertToEntityAttribute(node.get("meetingType").asText()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return debateMeetingAttributes;
    }
}