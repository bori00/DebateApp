package com.se.DebateApp.Controller;

import com.se.DebateApp.Controller.StartDebate.DTOs.MeetingAttributes;
import com.se.DebateApp.Model.Constants.MeetingType;
import com.se.DebateApp.Model.DebateMeeting;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Repository.DebateMeetingRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DebateMeetingController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateMeetingRepository debateMeetingRepository;

    @PostMapping(value = "/process_create_active_meeting", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void processCreateActiveMeeting(@RequestBody MeetingAttributes meetingAttributes) {
        DebateSession debateSession = debateSessionRepository.getById(meetingAttributes.getDebateSessionId());

        DebateMeeting debateMeeting = createDebateMeeting(meetingAttributes, MeetingType.ACTIVE);
        debateMeeting.setDebateSession(debateSession);
        DebateMeeting savedDebateMeeting = debateMeetingRepository.save(debateMeeting);

        debateSession.addNewMeeting(savedDebateMeeting);
        debateSessionRepository.save(debateSession);
    }


    private DebateMeeting createDebateMeeting(MeetingAttributes meetingAttributes, MeetingType meetingType) {
        DebateMeeting debateMeeting = new DebateMeeting();

        debateMeeting.setMeetingType(meetingType);
        debateMeeting.setName(meetingAttributes.getMeetingName());
        debateMeeting.setUrl(meetingAttributes.getMeetingUrl());

        return debateMeeting;
    }
}
