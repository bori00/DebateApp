package com.se.DebateApp.Service;

import com.se.DebateApp.Model.DTOs.DebateParticipantsStatus;
import com.se.DebateApp.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public static final String DEBATE_SESSION_PARTICIPANTS_STATUS_SOCKET_DEST = "/queue/debate" +
            "-session-participants-status";

    public static final String DEBATE_SESSION_ACTIVATED_SOCKET_DEST = "/queue/debate-session" +
            "-activated";
    public static final String DEBATE_SESSION_CLOSED_SOCKET_DEST = "queue/debate-closed";
    public static final String DEBATE_TIMED_INTERVAL_IS_UP_SOCKET_TEMPLATE = "/queue/debate" +
            "-%s-times-up";

    public void notifyUser(
            User user,
            Object message,
            String socketDest) {
        simpMessagingTemplate.convertAndSendToUser(
                user.getUserName(),
                socketDest,
                message);

    }

    public void notifyUsers(
            Collection<User> users,
            Object message,
            String socketDest) {
        for (User user : users) {
            notifyUser(user, message, socketDest);
        }
    }
}
