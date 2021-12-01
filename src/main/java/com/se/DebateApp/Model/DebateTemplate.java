package com.se.DebateApp.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "debate_templates")
@Getter
@Setter
@NoArgsConstructor
public class DebateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User owner;

    @Column(nullable = true, length = 100)
    private String title;

    @Column(nullable = true, length = 100)
    private String topic;

    @Column(nullable = false, length = 1000)
    private String statement;

    @OneToMany
    private Set<LinkToResource> resourceLinks;

    @Column(nullable = false)
    private Integer prepTimeSeconds;

    @Column(nullable = false)
    private Integer constSpeechSeconds;

    @Column(nullable = false)
    private Integer rebuttalSpeechSeconds;

    @Column(nullable = false)
    private Integer crossExaminationSeconds;
}
