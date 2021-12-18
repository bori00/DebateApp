package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.Constants.MeetingType;
import com.se.DebateApp.Model.DebateMeeting;
import com.se.DebateApp.Model.DebateSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DebateMeetingRepository extends JpaRepository<DebateMeeting, Long> {
    @Query("Select dm FROM DebateMeeting dm WHERE ?1 = dm.debateSession and ?2 = dm.meetingType")
    DebateMeeting findDebateMeetingOfDebateSessionByType(DebateSession debateSession, MeetingType meetingType);
}
