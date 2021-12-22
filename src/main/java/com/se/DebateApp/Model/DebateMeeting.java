package com.se.DebateApp.Model;

import com.se.DebateApp.Model.Constants.MeetingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "debate_meetings")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DebateMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private DebateSession debateSession;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String url;

    @Column
    private MeetingType meetingType;
}
