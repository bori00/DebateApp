package com.se.DebateApp.Model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "debate_templates")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DebateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private User owner;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, length = 100)
    private String topic;

    @Column(nullable = false, length = 1000)
    private String statement;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy="debateTemplate")
    private Set<LinkToResource> resourceLinks = new HashSet<>();

    @Column(nullable = false)
    private Integer prepTimeSeconds;

    @Column(nullable = false)
    private Integer constSpeechSeconds;

    @Column(nullable = false)
    private Integer rebuttalSpeechSeconds;

    @Column(nullable = false)
    private Integer crossExaminationSeconds;

    // Fields used to simplify form submission.
    @Transient
    private Integer prepTimeMins = 30;

    @Transient
    private Integer prepTimeSecs = 0;

    @Transient
    private Integer constSpeechMins = 5;

    @Transient
    private Integer constSpeechSecs = 0;

    @Transient
    private Integer rebuttalSpeechMins = 3;

    @Transient
    private Integer rebuttalSpeechSecs = 0;

    @Transient
    private Integer crossExaminationMins = 2;

    @Transient
    private Integer crossExaminationSecs = 0;

    @PrePersist
    public void computeSecondsBasedOnMinsAndSecs() {
        prepTimeSeconds = prepTimeMins * 60 + prepTimeSecs;
        constSpeechSeconds = constSpeechMins * 60 + constSpeechSecs;
        rebuttalSpeechSeconds = rebuttalSpeechMins * 60 + rebuttalSpeechSecs;
        crossExaminationSeconds = crossExaminationMins * 60 + crossExaminationSecs;
    }

    @PostLoad
    public void computeMinsAndSecsBasedOnSeconds() {
        prepTimeMins = prepTimeSeconds / 60;
        prepTimeSecs = prepTimeSeconds% 60;
        constSpeechMins = constSpeechSeconds / 60;
        constSpeechSecs = constSpeechSeconds % 60;
        rebuttalSpeechMins = rebuttalSpeechSeconds / 60;
        rebuttalSpeechSecs = rebuttalSpeechSeconds % 60;
        crossExaminationMins = crossExaminationSeconds / 60;
        crossExaminationSecs = crossExaminationSeconds % 60;
    }

    public void addNewDLinkToResource(LinkToResource linkToResource) {
        linkToResource.setDebateTemplate(this);
        this.resourceLinks.add(linkToResource);
    }
}
