package com.se.DebateApp.Model;

import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.PlayerState;
import com.se.DebateApp.Model.Constants.TeamType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "debate_session_players")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DebateSessionPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private DebateSession debateSession;

    @ManyToOne(optional = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private PlayerState playerState = PlayerState.WAITING_TO_JOIN_TEAM;

    @Column(nullable = false)
    private PlayerRole playerRole = PlayerRole.NONE;

    @Column(nullable = true)
    private TeamType team = null;
}
