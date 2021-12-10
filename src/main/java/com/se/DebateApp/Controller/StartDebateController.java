package com.se.DebateApp.Controller;

import com.se.DebateApp.Config.CustomUserDetails;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.DebateSessionRepository;
import com.se.DebateApp.Repository.DebateTemplateRepository;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class StartDebateController {

    @Autowired
    private DebateSessionRepository debateSessionRepository;

    @Autowired
    private DebateTemplateRepository debateTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/process_start_debate")
    public String processStartDebateSession(@RequestParam Long debateTemplateId, Model model) {
        Optional<DebateTemplate> optDebateTemplate =
                debateTemplateRepository.findById(debateTemplateId);
        if (optDebateTemplate.isEmpty()) {
            throw new IllegalArgumentException();
        }
        DebateTemplate debateTemplate = optDebateTemplate.get();
        DebateSession debateSession = new DebateSession();
        Date currentTime = new Date(System.currentTimeMillis());
        debateSession.setCurrentPhaseStartingTime(currentTime);
        debateTemplate.addNewSession(debateSession);
        DebateSession savedSession = debateSessionRepository.save(debateSession);
        model.addAttribute("debateCode", savedSession.getId());
        return "redirect:/start_debate";
    }
}
