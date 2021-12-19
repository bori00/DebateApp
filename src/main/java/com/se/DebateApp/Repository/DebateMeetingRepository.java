package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.Constants.MeetingType;
import com.se.DebateApp.Model.DebateMeeting;
import com.se.DebateApp.Model.DebateSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DebateMeetingRepository extends JpaRepository<DebateMeeting, Long> {
    @Query("SELECT dm FROM DebateMeeting dm WHERE ?1 = dm.debateSession")
    List<DebateMeeting> findByDebateSession(DebateSession debateSession);

    @Query("SELECT dm FROM DebateMeeting dm WHERE ?1 = dm.debateSession AND ?2 = dm.meetingType")
    Optional<DebateMeeting> findByDebateSessionAndMeetingType(DebateSession debateSession, MeetingType meetingType);
}
