package com.se.DebateApp.Controller.DebateMeetings;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.DebateMeetings.DTOs.DebateMeetingDTO;
import com.se.DebateApp.Controller.StartDebate.DTOs.DebateMeetingAttributesDTO;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.DebateMeeting;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateMeetingRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DebateMeetingController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateMeetingRepository debateMeetingRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = SupportedMappings.CREATE_MEETING, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void processCreateMeeting(@RequestBody DebateMeetingAttributesDTO debateMeetingAttributes) {
        DebateSession debateSession = debateSessionRepository.getById(debateMeetingAttributes.getDebateSessionId());

        DebateMeeting debateMeeting = createDebateMeeting(debateMeetingAttributes);
        debateMeeting.setDebateSession(debateSession);
        DebateMeeting savedDebateMeeting = debateMeetingRepository.save(debateMeeting);

        debateSession.addNewMeeting(savedDebateMeeting);
        debateSessionRepository.save(debateSession);
    }

    @GetMapping(value = SupportedMappings.GET_ALL_MEETINGS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<DebateMeetingDTO> processGetAllMeetings(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        List<DebateMeeting> debateMeetings = debateMeetingRepository.findByDebateSession(debateSession);

        return debateMeetings.stream()
                .map(debateMeeting -> new DebateMeetingDTO(debateMeeting.getName(), debateMeeting.getUrl(), debateMeeting.getMeetingType().getCode()))
                .collect(Collectors.toList());
    }

    private DebateMeeting createDebateMeeting(DebateMeetingAttributesDTO debateMeetingAttributes) {
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
