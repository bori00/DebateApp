package com.se.DebateApp.Controller;

import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/process_register")
    public String processRegistration(User user, Model model) {
        if (!userNameIsTaken(user.getUserName())) {
            // Valid new user data.
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return "register_success";
        } else {
            // invalid username: username is taken.
            model.addAttribute("errorMessage", "This username is already taken. Please choose " +
                    "another one!");
            return "register";
        }
    }

    private boolean userNameIsTaken(String userName) {
        return userRepository.findByUserName(userName) != null;
    }
}
