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
@Table(name = "debate_role_vote")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DebateRoleVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private DebateSession debateSession;

    @ManyToOne(optional = false)
    @ToString.Exclude
    private DebateSessionPlayer forPlayer;

    @Column(nullable = false)
    private PlayerRole forPlayerRole = PlayerRole.NONE;
}
