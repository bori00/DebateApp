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

@Controller
public class DebateMeetingController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateMeetingRepository debateMeetingRepository;

    @Autowired
    private DebateSessionPlayerRepository debateSessionPlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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

    @GetMapping(value = "/process_get_debate_session_player", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DebateSessionPlayerDTO processGetDebateSessionPlayer(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSessionPlayer debateSessionPlayer = debateSessionPlayerRepository.findDebateSessionPlayerByUserAndDebateSession(getCurrentUser(), debateSessionRepository.getById(debateSessionId))
                .orElseThrow(() -> new NotFoundException("The given user is not a player in the debate session"));

        DebateSessionPlayerDTO debateSessionPlayerDTO = new DebateSessionPlayerDTO();

        debateSessionPlayerDTO.setDebateSessionId(debateSessionPlayer.getDebateSession().getId());
        debateSessionPlayerDTO.setId(debateSessionPlayer.getId());
        debateSessionPlayerDTO.setPlayerRole(debateSessionPlayer.getPlayerRole().getCode());
        debateSessionPlayerDTO.setPlayerState(debateSessionPlayer.getPlayerState().getCode());
        debateSessionPlayerDTO.setTeam(debateSessionPlayer.getTeam().getCode());

        return debateSessionPlayerDTO;
    }

    private DebateMeeting createDebateMeeting(DebateMeetingAttributes debateMeetingAttributes) {
        DebateMeeting debateMeeting = new DebateMeeting();

        debateMeeting.setMeetingType(debateMeetingAttributes.getMeetingType());
        debateMeeting.setName(debateMeetingAttributes.getMeetingName());
        debateMeeting.setUrl(debateMeetingAttributes.getMeetingUrl());

        return debateMeeting;
    }

    @GetMapping("/process_get_time_interval")
    @ResponseBody
    public Integer processGetTimeInterval(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        DebateTemplate debateTemplate = debateSession.getDebateTemplate();

        switch (debateSession.getDebateSessionPhase()) {
            case PREP_TIME: {
                return debateTemplate.getPrepTimeSeconds();
            }
            case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1:
            case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2:
            case NEGATIVE_CONSTRUCTIVE_SPEECH_1:
            case NEGATIVE_CONSTRUCTIVE_SPEECH_2: {
                return debateTemplate.getConstSpeechSeconds();
            }
            case CROSS_EXAMINATION_1:
            case CROSS_EXAMINATION_2:
            case CROSS_EXAMINATION_3:
            case CROSS_EXAMINATION_4: {
                return debateTemplate.getCrossExaminationSeconds();
            }
            case AFFIRMATIVE_REBUTTAL_1:
            case AFFIRMATIVE_REBUTTAL_2:
            case NEGATIVE_REBUTTAL_1:
            case NEGATIVE_REBUTTAL_2: {
                return debateTemplate.getRebuttalSpeechSeconds();
            }
            default: {
                return 24 * 60 * 60; // 24 hours
            }
        }
    }

    @PostMapping("/process_end_of_preparation_phase")
    @ResponseBody
    public void processEndOfPreparationPhase() {
        DebateSession debateSession = debateSessionRepository.findDebateSessionOfJudgeWithGivenState(getCurrentUser(), DebateSessionPhase.PREP_TIME).get(0);
        DebateSessionPhase previousPhase = debateSession.getDebateSessionPhase();
        debateSession.setDebateSessionPhase(DebateSessionPhase.AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1);
        debateSession.setCurrentPhaseStartingTime(new Date(System.currentTimeMillis()));
        debateSessionRepository.save(debateSession);
        announceAllDebatePlayersAboutEndOfTimeInterval(debateSession.getPlayers(), previousPhase);
    }

    private void announceAllDebatePlayersAboutEndOfTimeInterval(Set<DebateSessionPlayer> players, DebateSessionPhase phase) {
        String destinationUrl = "/queue/debate-" + phase.name().toLowerCase().replace('_', '-') + "-times-up";
        System.out.println(destinationUrl);
        for (DebateSessionPlayer player : players) {
            simpMessagingTemplate.convertAndSendToUser(
                    player.getUser().getUserName(), destinationUrl, "timesUp");
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
