package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebateSessionRepository extends JpaRepository<DebateSession, Long> {
    @Query("Select s FROM DebateSession s WHERE s.debateTemplate.owner = ?1 and s" +
            ".debateSessionPhase <> '-'")
    List<DebateSession> findActiveDebateSessionsOfJudge(User user);

    @Query("Select s FROM DebateSession s, IN(s.players) p WHERE p.user = ?1 and s" +
            ".debateSessionPhase <> '-'")
    List<DebateSession> findActiveDebateSessionsOfPlayer(User user);
}
