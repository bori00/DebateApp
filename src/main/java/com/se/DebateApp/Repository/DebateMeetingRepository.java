package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateMeetingRepository extends JpaRepository<DebateMeeting, Long> {
}
