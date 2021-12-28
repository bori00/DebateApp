package com.se.DebateApp.Controller.SpeechBattle;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Controller.SpeechBattle.DTOs.BattleInformationDTO;
import com.se.DebateApp.Controller.SupportedMappings;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionPlayerRepository;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.UserRepository;
import com.se.DebateApp.Service.NotificationService;
import com.se.DebateApp.Service.StateTransitions.ConcreteStates.battleStates.BattleSpeechState;
import com.se.DebateApp.Service.StateTransitions.DebateState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.se.DebateApp.Model.Constants.DebateSessionPhase.FINISHED;
import static com.se.DebateApp.Model.Constants.PlayerRole.DEPUTY1;
import static com.se.DebateApp.Model.Constants.PlayerRole.DEPUTY2;
import static com.se.DebateApp.Model.Constants.TeamType.CON;
import static com.se.DebateApp.Model.Constants.TeamType.PRO;

@Controller
public class BattleController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateSessionPlayerRepository debateSessionPlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping(value = SupportedMappings.GET_CURRENT_PHASE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCurrentPhase(@RequestParam Long debateSessionId) {
        return debateSessionRepository.getById(debateSessionId).getDebateSessionPhase().name();
    }

    @GetMapping(value = SupportedMappings.GET_BATTLE_INFORMATION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BattleInformationDTO getBattleInformation(@RequestParam Long debateSessionId) {
        return createBattleInformationDTO(debateSessionRepository.getById(debateSessionId));
    }

    @GetMapping(SupportedMappings.GO_TO_BATTLE)
    public String goToBattlePage(Model model) {
        DebateSession debateSession = null;
        List<DebateSession> debateSessionsOfJudge = debateSessionRepository.findDebateSessionsOfJudgeWithStateDifferentFrom(getCurrentUser(), DebateSessionPhase.FINISHED);
        boolean isJudge = debateSessionsOfJudge.size() == 1;
        model.addAttribute("isJudge", isJudge);
        if (isJudge) {
            model.addAttribute("judge", getCurrentUser().getUserName());
            debateSession = debateSessionsOfJudge.get(0);
        } else {
            List<DebateSession> debateSessionsOfPlayer = debateSessionRepository.findDebateSessionsOfPlayerWithStateDifferentFrom(getCurrentUser(), DebateSessionPhase.FINISHED);
            if (debateSessionsOfPlayer.size() == 1) {
                debateSession = debateSessionsOfPlayer.get(0);
                model.addAttribute("judge", debateSession.getDebateTemplate().getOwner().getUserName());
            } else {
                return SupportedMappings.ERROR_PAGE;
            }
        }
        model.addAttribute("debateSessionId", debateSession.getId());
        model.addAttribute("debateStatement", debateSession.getDebateTemplate().getStatement());;
        model.addAttribute("battleInformation", createBattleInformationDTO(debateSession));

        return SupportedMappings.BATTLE_PAGE;
    }

    @PostMapping(value = SupportedMappings.PROCESS_SKIP_SPEECH)
    public void processSkipSpeech(@RequestParam Long debateSessionId) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);
        DebateSessionPhase currentPhase = debateSession.getDebateSessionPhase();
        if(currentPhase.equals(FINISHED)) {
            return;
        }
        DebateState currentState = currentPhase.getCorrespondingState();
        if(currentState.getClass().getSuperclass().equals(BattleSpeechState.class)) {
            BattleSpeechState battleSpeechState = (BattleSpeechState)currentState;
            battleSpeechState.onSkipSpeech(debateSession, notificationService, debateSessionRepository);
            debateSession.getDebateSessionPhase().getCorrespondingState().onBeginningOfState(debateSession,
                    notificationService);
        }
    }

    private static final String CONSTRUCTIVE_SPEECH_INSTRUCTIONS = "Present and bring new arguments supporting your opinion.";
    private static final String CROSS_EXAMINATION_SPEECH_INSTRUCTIONS = "Discuss the previously presented ideas and ask related questions.";
    private static final String REBUTTAL_SPEECH_INSTRUCTIONS = "Defend your previously presented arguments with the gathered evidence. At this point you cannot bring new arguments supporting your idea.";

    private List<String> getNamesOfSpeakers(List<DebateSessionPlayer> speakers) {
        return speakers.stream()
                .map(DebateSessionPlayer::getUser)
                .map(User::getUserName)
                .collect(Collectors.toList());
    }

    private String getInstructionsForPhase(DebateSessionPhase debateSessionPhase) {
        return switch (debateSessionPhase) {
            case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1, AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2, NEGATIVE_CONSTRUCTIVE_SPEECH_1, NEGATIVE_CONSTRUCTIVE_SPEECH_2 -> CONSTRUCTIVE_SPEECH_INSTRUCTIONS;
            case CROSS_EXAMINATION_1, CROSS_EXAMINATION_2, CROSS_EXAMINATION_3, CROSS_EXAMINATION_4 -> CROSS_EXAMINATION_SPEECH_INSTRUCTIONS;
            case AFFIRMATIVE_REBUTTAL_1, AFFIRMATIVE_REBUTTAL_2, NEGATIVE_REBUTTAL_1, NEGATIVE_REBUTTAL_2 -> REBUTTAL_SPEECH_INSTRUCTIONS;
            default -> "";
        };
    }

    private boolean isSpeaker(List<DebateSessionPlayer> currentSpeakers) {
        return currentSpeakers.stream()
                .anyMatch(currentSpeaker -> currentSpeaker.getUser().equals(getCurrentUser()));
    }

    private List<DebateSessionPlayer> getSpeakersForGivenPhase(DebateSessionPhase phase, DebateSessionPlayer deputy1Pro, DebateSessionPlayer deputy2Pro, DebateSessionPlayer deputy1Con, DebateSessionPlayer deputy2Con) {
        return
                switch (phase) {
                    case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_1, AFFIRMATIVE_REBUTTAL_1 -> Arrays.asList(deputy1Pro);
                    case CROSS_EXAMINATION_1 -> Arrays.asList(deputy1Pro, deputy2Con);
                    case NEGATIVE_CONSTRUCTIVE_SPEECH_1, NEGATIVE_REBUTTAL_1 -> Arrays.asList(deputy1Con);
                    case CROSS_EXAMINATION_2 -> Arrays.asList(deputy1Con, deputy1Pro);
                    case AFFIRMATIVE_CONSTRUCTIVE_SPEECH_2, AFFIRMATIVE_REBUTTAL_2 -> Arrays.asList(deputy2Pro);
                    case CROSS_EXAMINATION_3 -> Arrays.asList(deputy2Pro, deputy1Con);
                    case NEGATIVE_CONSTRUCTIVE_SPEECH_2, NEGATIVE_REBUTTAL_2 -> Arrays.asList(deputy2Con);
                    case CROSS_EXAMINATION_4 -> Arrays.asList(deputy2Con, deputy2Pro);
                    default -> new ArrayList<>();
                };
    }

    private List<String> getTeamMembersName(DebateSession debateSession, TeamType teamType) {
        return debateSession.getPlayers()
                .stream()
                .filter(debateSessionPlayer -> debateSessionPlayer.getTeam().equals(teamType))
                .map(debateSessionPlayer -> debateSessionPlayer.getUser().getUserName())
                .collect(Collectors.toList());
    }

    private DebateSessionPlayer getDeputyForTeam(DebateSession debateSession, TeamType teamType, PlayerRole playerRole) {
        return debateSessionPlayerRepository.findAll()
                .stream()
                .filter(debateSessionPlayer -> debateSessionPlayer.getTeam().equals(teamType) && debateSessionPlayer.getPlayerRole().equals(playerRole))
                .findFirst()
                .get();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(((CustomUserDetails) auth.getPrincipal()).getUsername());
    }

    private BattleInformationDTO createBattleInformationDTO(DebateSession debateSession) {
        BattleInformationDTO battleInformationDTO = new BattleInformationDTO();

        DebateSessionPlayer deputy1Pro = getDeputyForTeam(debateSession, PRO, DEPUTY1);
        DebateSessionPlayer deputy2Pro = getDeputyForTeam(debateSession, PRO, DEPUTY2);
        DebateSessionPlayer deputy1Con = getDeputyForTeam(debateSession, CON, DEPUTY1);
        DebateSessionPlayer deputy2Con = getDeputyForTeam(debateSession, CON, DEPUTY2);

        battleInformationDTO.setDeputy1Pro(deputy1Pro.getUser().getUserName());
        battleInformationDTO.setDeputy2Pro(deputy2Pro.getUser().getUserName());
        battleInformationDTO.setDeputy1Con(deputy1Con.getUser().getUserName());
        battleInformationDTO.setDeputy2Con(deputy2Con.getUser().getUserName());

        DebateSessionPhase currentPhase = debateSession.getDebateSessionPhase();

        battleInformationDTO.setCurrentPhase(currentPhase.name());
        battleInformationDTO.setNextPhase(currentPhase.getCorrespondingState().getNextDebateSessionPhaseAfterStateEnded(debateSession).name());

        List<DebateSessionPlayer> currentSpeakers = getSpeakersForGivenPhase(currentPhase, deputy1Pro, deputy2Pro, deputy1Con, deputy2Con);
        List<DebateSessionPlayer> nextSpeakers = getSpeakersForGivenPhase(currentPhase.getCorrespondingState().getNextDebateSessionPhaseAfterStateEnded(debateSession), deputy1Pro, deputy2Pro, deputy1Con, deputy2Con);

        battleInformationDTO.setInstructions(getInstructionsForPhase(currentPhase));
        battleInformationDTO.setCurrentSpeakers(getNamesOfSpeakers(currentSpeakers));
        battleInformationDTO.setNextSpeakers(getNamesOfSpeakers(nextSpeakers));

        battleInformationDTO.setIsSpeaker(isSpeaker(currentSpeakers));
        battleInformationDTO.setIsNextSpeaker(isSpeaker(nextSpeakers));

        battleInformationDTO.setProTeamMembers(getTeamMembersName(debateSession, PRO));
        battleInformationDTO.setConTeamMembers(getTeamMembersName(debateSession, CON));

        return battleInformationDTO;
    }
}
