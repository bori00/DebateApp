package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.StartDebate.DTOs.DebateMeetingAttributes;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DTOs.DebateMeetingDTO;
import com.se.DebateApp.Model.DTOs.DebateSessionPlayerDTO;
import com.se.DebateApp.Model.*;
import com.se.DebateApp.Repository.DebateMeetingRepository;
import com.se.DebateApp.Repository.DebateSessionPlayerRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.se.DebateApp.Model.Constants.DebateSessionPhase.FINISHED;

@Controller
public class DebateMeetingController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateMeetingRepository debateMeetingRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/process_create_meeting", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void processCreateMeeting(@RequestBody DebateMeetingAttributes debateMeetingAttributes) {
        DebateSession debateSession = debateSessionRepository.getById(debateMeetingAttributes.getDebateSessionId());

        DebateMeeting debateMeeting = createDebateMeeting(debateMeetingAttributes);
        debateMeeting.setDebateSession(debateSession);
        DebateMeeting savedDebateMeeting = debateMeetingRepository.save(debateMeeting);

        debateSession.addNewMeeting(savedDebateMeeting);
        debateSessionRepository.save(debateSession);
    }

    @GetMapping(value = "/process_get_all_meetings", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<DebateMeetingDTO> processGetAllMeetings(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        List<DebateMeeting> debateMeetings = debateMeetingRepository.findByDebateSession(debateSession);

        return debateMeetings.stream()
                .map(debateMeeting -> new DebateMeetingDTO(debateMeeting.getName(), debateMeeting.getUrl(), debateMeeting.getMeetingType().getCode()))
                .collect(Collectors.toList());
    }

    private DebateMeeting createDebateMeeting(DebateMeetingAttributes debateMeetingAttributes) {
        DebateMeeting debateMeeting = new DebateMeeting();

        debateMeeting.setMeetingType(debateMeetingAttributes.getMeetingType());
        debateMeeting.setName(debateMeetingAttributes.getMeetingName());
        debateMeeting.setUrl(debateMeetingAttributes.getMeetingUrl());

        return debateMeeting;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
