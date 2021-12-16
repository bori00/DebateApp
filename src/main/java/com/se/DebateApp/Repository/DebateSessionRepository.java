package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebateSessionRepository extends JpaRepository<DebateSession, Long> {

    @Query("Select s FROM DebateSession s WHERE " +
            "s.debateTemplate.owner = ?1 and s" +
            ".debateSessionPhase <> ?2")
    List<DebateSession> findDebateSessionsOfJudgeWithStateDifferentFrom(User user,
                                                                        DebateSessionPhase phase);

    @Query("Select s FROM DebateSession s, IN(s.players) p WHERE " +
            " p.user = ?1 and s" +
            ".debateSessionPhase <> ?2")
    List<DebateSession> findDebateSessionsOfPlayerWithStateDifferentFrom(User user,
                                                                         DebateSessionPhase phase);

    @Query("Select s FROM DebateSession s, IN(s.players) p WHERE " +
            " p.user = ?1 and s" +
            ".debateSessionPhase = ?2")
    List<DebateSession> findDebateSessionOfPlayerWithGivenState(User user,
                                                                DebateSessionPhase phase);

    @Query("Select s FROM DebateSession s WHERE " +
            "s.debateTemplate.owner = ?1 and s" +
            ".debateSessionPhase = ?2")
    List<DebateSession> findDebateSessionOfJudgeWithGivenState(User user,
                                                                DebateSessionPhase phase);
}
