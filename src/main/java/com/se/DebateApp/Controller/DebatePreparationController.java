package com.se.DebateApp.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.PreparationPhaseAttributes;
import com.se.DebateApp.Repository.DebateSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class DebatePreparationController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @PostMapping("/process_start_preparation")
    public DebateSession processStartPreparation(@RequestParam(name = "debateSessionId") Long debateSessionId, @RequestBody String attributes) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);

        try {
            PreparationPhaseAttributes preparationPhaseAttributes = (new ObjectMapper()).readValue(attributes, PreparationPhaseAttributes.class);
            debateSession.setPreparationPhaseUrlProTeam(preparationPhaseAttributes.getPreparationPhaseUrlProTeam());
            debateSession.setPreparationPhaseUrlContraTeam(preparationPhaseAttributes.getPreparationPhaseUrlContraTeam());
        }catch(JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        debateSession.setDebateSessionPhase(DebateSessionPhase.PREP_TIME);
        debateSession.setCurrentPhaseStartingTime(new Date());

        return debateSessionRepository.save(debateSession);
    }

}
