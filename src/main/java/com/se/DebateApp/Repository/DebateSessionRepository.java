package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateSession;
import com.se.DebateApp.Model.DebateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateSessionRepository extends JpaRepository<DebateSession, Long> {
}
