package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateSessionPlayer;
import com.se.DebateApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DebateSessionPlayerRepository extends JpaRepository<DebateSessionPlayer, Long> {

    @Query("SELECT p FROM DebateSessionPlayer p WHERE p.user = ?1 AND p.debateSession = ?2")
    Optional<DebateSessionPlayer> findDebateSessionPlayerByUserAndDebateSession(User user, DebateSession debateSession);
}
