package com.se.DebateApp.Model;

import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.PlayerState;
import com.se.DebateApp.Model.Constants.TeamType;
import com.se.DebateApp.Model.DTOs.DebateParticipantsStatus;
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

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
    @ToString.Exclude
    private DebateTemplate debateTemplate;

    @Column(nullable = false)
    private DebateSessionPhase debateSessionPhase = DebateSessionPhase.WAITING_FOR_PLAYERS;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private java.util.Date currentPhaseStartingTime;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="debateSession")
    private Set<DebateSessionPlayer> players = new HashSet<>();

    public void addNewPlayer(DebateSessionPlayer debateSessionPlayer) {
        debateSessionPlayer.setDebateSession(this);
        this.players.add(debateSessionPlayer);
    }

    public DebateParticipantsStatus computeParticipantsStatus() {
        int noWaitingToJoinPlayers =
                (int) players.stream()
                        .filter(player -> player.getPlayerState().equals(PlayerState.WAITING_TO_JOIN_TEAM))
                        .count();

        int noProTeamPlayers =
                (int) players.stream()
                        .filter(player -> player.getPlayerState().equals(PlayerState.JOINED_A_TEAM))
                        .filter(player -> player.getTeam().equals(TeamType.PRO))
                        .count();

        int noConTeamPlayers =
                (int) players.stream()
                        .filter(player -> player.getPlayerState().equals(PlayerState.JOINED_A_TEAM))
                        .filter(player -> player.getTeam().equals(TeamType.CON))
                        .count();

        return new DebateParticipantsStatus(noWaitingToJoinPlayers,
                noProTeamPlayers, noConTeamPlayers);
    }
}
