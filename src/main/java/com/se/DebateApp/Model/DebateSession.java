package com.se.DebateApp.Model;

import com.se.DebateApp.Controller.StartDebate.DTOs.DebateParticipantsStatusDTO;
import com.se.DebateApp.Model.Constants.DebateSessionPhase;
import com.se.DebateApp.Model.Constants.PlayerRole;
import com.se.DebateApp.Model.Constants.PlayerState;
import com.se.DebateApp.Model.Constants.TeamType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="debateSession", orphanRemoval=true)
    private Set<DebateSessionPlayer> players = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="debateSession", orphanRemoval=true)
    private Set<DebateMeeting> meetings = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="debateSession", orphanRemoval=true)
    private Set<DebateRoleVote> roleVotes = new HashSet<>();

    public void addNewPlayer(DebateSessionPlayer debateSessionPlayer) {
        debateSessionPlayer.setDebateSession(this);
        this.players.add(debateSessionPlayer);
    }

    public void addNewMeeting(DebateMeeting debateMeeting) {
        debateMeeting.setDebateSession(this);
        this.meetings.add(debateMeeting);
    }

    public void addNewRoleVote(DebateRoleVote roleVote) {
        roleVote.setDebateSession(this);
        roleVotes.add(roleVote);
    }

    public DebateParticipantsStatusDTO computeParticipantsStatus() {
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

        return new DebateParticipantsStatusDTO(noWaitingToJoinPlayers,
                noProTeamPlayers, noConTeamPlayers);
    }

    public Optional<DebateSessionPlayer> getDeputy(TeamType teamType, PlayerRole playerRole) {
        return players
                .stream()
                .filter(p -> p.getPlayerRole().equals(playerRole))
                .filter(p -> p.getTeam().equals(teamType))
                .findFirst();
    }

    public Map<DebateSessionPlayer, Long> getPlayersToNoVotesForDeputyRole(TeamType teamType,
                                                                           PlayerRole playerRole) {
        return players.stream()
                        .filter(p -> p.getTeam().equals(teamType))
                        .map(DebateSessionPlayer::getRoleVotes)
                        .flatMap(Collection::stream)
                        .filter(roleVote -> roleVote.getForPlayerRole().equals(playerRole))
                        .collect(Collectors.groupingBy(DebateRoleVote::getForPlayer,
                                Collectors.counting()));
    }
}
