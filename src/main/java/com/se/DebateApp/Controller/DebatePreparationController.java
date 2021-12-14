package com.se.DebateApp.Controller;

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
    public DebateSession processStartPreparation(@RequestParam(name = "debateSessionId") Long debateSessionId, @RequestBody PreparationPhaseAttributes preparationPhaseAttributes) {
        DebateSession debateSession = debateSessionRepository.getById(debateSessionId);

        debateSession.setPreparationPhaseUrlProTeam(preparationPhaseAttributes.getPreparationPhaseUrlProTeam());
        debateSession.setPreparationPhaseUrlContraTeam(preparationPhaseAttributes.getPreparationPhaseUrlContraTeam());
        debateSession.setDebateSessionPhase(DebateSessionPhase.PREP_TIME);
        debateSession.setCurrentPhaseStartingTime(new Date());

        return debateSessionRepository.save(debateSession);
    }

}
