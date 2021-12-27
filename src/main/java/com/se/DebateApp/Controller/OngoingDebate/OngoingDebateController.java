package com.se.DebateApp.Controller.OngoingDebate;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.OngoingDebate.DTOs.DebateSessionPlayerDTO;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionPlayerRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.NotFoundException;

@Controller
public class OngoingDebateController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DebateSessionPlayerRepository debateSessionPlayerRepository;

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @GetMapping(value = SupportedMappings.GET_USERNAME_OF_CURRENT_USER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUserNameOfCurrentUser() {
        return getCurrentUser().getUserName();
    }

    @GetMapping(value = SupportedMappings.GET_DEBATE_SESSION_PLAYER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DebateSessionPlayerDTO getDebateSessionPlayer(@RequestParam(value = "debateSessionId") Long debateSessionId) {
        DebateSessionPlayer debateSessionPlayer = debateSessionPlayerRepository
                .findDebateSessionPlayerByUserAndDebateSession(
                        getCurrentUser(), debateSessionRepository.getById(debateSessionId))
                .orElseThrow(() -> new NotFoundException("The given user is not a player in the debate session"));

        DebateSessionPlayerDTO debateSessionPlayerDTO = new DebateSessionPlayerDTO();

        debateSessionPlayerDTO.setDebateSessionId(debateSessionPlayer.getDebateSession().getId());
        debateSessionPlayerDTO.setId(debateSessionPlayer.getId());
        debateSessionPlayerDTO.setPlayerRole(debateSessionPlayer.getPlayerRole().getCode());
        debateSessionPlayerDTO.setPlayerState(debateSessionPlayer.getPlayerState().getCode());
        debateSessionPlayerDTO.setTeam(debateSessionPlayer.getTeam().getCode());

        return debateSessionPlayerDTO;
    }

    @PostMapping(SupportedMappings.HAS_USER_ONGOING_DEBATE)
    @ResponseBody
    public boolean hasUserOngoingDebate() {
        User user = getCurrentUser();
        return debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(user,
                DebateSessionPhase.FINISHED).size() > 0 ||
                debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(user,
                        DebateSessionPhase.FINISHED).size() > 0;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }
}
