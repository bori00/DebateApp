package com.se.DebateApp.Controller;

import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private static final String errorMessageName = "errorMessage";
    private static final String usernameTakenErrorMessage = "This username is already taken. " +
            "Please choose another one!";

    @Autowired
    private UserRepository userRepository;

    @PostMapping(SupportedMappings.REGISTER_AND_GO_TO_DESTINATION)
    public String processRegistration(User user, Model model) {
        if (!userNameIsTaken(user.getUserName())) {
            // Valid new user data.
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return SupportedMappings.REGISTER_SUCCESS_PAGE;
        } else {
            // invalid username: username is taken.
            model.addAttribute(errorMessageName, usernameTakenErrorMessage);
            return SupportedMappings.REGISTER_PAGE;
        }
    }

    private boolean userNameIsTaken(String userName) {
        return userRepository.findByUserName(userName) != null;
    }
}
