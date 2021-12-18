package com.se.DebateApp.Controller;

import com.se.DebateApp.Controller.StartDebate.DTOs.MeetingAttributes;
import com.se.DebateApp.Model.Constants.MeetingType;
import com.se.DebateApp.Model.DTOs.DebateMeetingDTO;
import com.se.DebateApp.Model.DebateMeeting;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Repository.DebateMeetingRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class DebateMeetingController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateMeetingRepository debateMeetingRepository;

    @PostMapping(value = "/process_create_meeting", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void processCreateActiveMeeting(@RequestBody MeetingAttributes meetingAttributes) {
        DebateSession debateSession = debateSessionRepository.getById(meetingAttributes.getDebateSessionId());

        DebateMeeting debateMeeting = createDebateMeeting(meetingAttributes);
        debateMeeting.setDebateSession(debateSession);
        DebateMeeting savedDebateMeeting = debateMeetingRepository.save(debateMeeting);

        debateSession.addNewMeeting(savedDebateMeeting);
        debateSessionRepository.save(debateSession);
    }

    @GetMapping(value = "/process_get_meeting", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DebateMeetingDTO processGetActiveMeeting(@RequestParam(value = "debateSessionId") Long debateSessionId, @RequestParam(value="meetingType") String meetingType) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        MeetingType.MeetingTypeConverter converter = new MeetingType.MeetingTypeConverter();
        DebateMeeting debateMeeting = debateMeetingRepository.findDebateMeetingOfDebateSessionByType(debateSession, converter.convertToEntityAttribute(meetingType));

        DebateMeetingDTO debateMeetingDTO = new DebateMeetingDTO();

        debateMeetingDTO.setMeetingName(debateMeeting.getName());
        debateMeetingDTO.setMeetingUrl(debateMeeting.getUrl());

        return debateMeetingDTO;
    }


    private DebateMeeting createDebateMeeting(MeetingAttributes meetingAttributes) {
        DebateMeeting debateMeeting = new DebateMeeting();

        debateMeeting.setMeetingType(meetingAttributes.getMeetingType());
        debateMeeting.setName(meetingAttributes.getMeetingName());
        debateMeeting.setUrl(meetingAttributes.getMeetingUrl());

        return debateMeeting;
    }
}
