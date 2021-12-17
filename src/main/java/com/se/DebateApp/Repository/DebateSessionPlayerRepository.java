package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateSessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateSessionPlayerRepository extends JpaRepository<DebateSessionPlayer, Long> {
}
