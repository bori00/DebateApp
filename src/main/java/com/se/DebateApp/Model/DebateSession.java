package com.se.DebateApp.Model;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.PlayerState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "debate_sessions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DebateSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @ToString.Exclude
    private DebateTemplate debateTemplate;

    @Column(nullable = false)
    private DebateSessionPhase debateSessionPhase = DebateSessionPhase.WAITING_FOR_PLAYERS;

    @Column(nullable = true)
    private String debateUrl;

    @Column(nullable = true)
    private String preparationPhaseUrlProTeam;

    @Column(nullable = true)
    private String preparationPhaseUrlContraTeam;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private java.util.Date currentPhaseStartingTime;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="debateSession")
    private Set<DebateSessionPlayer> players = new HashSet<>();
}
