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

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy="debateTemplate")
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
    private Integer prepTimeMins;

    @Transient
    private Integer prepTimeSecs;

    @Transient
    private Integer constSpeechMins;

    @Transient
    private Integer constSpeechSecs;

    @Transient
    private Integer rebuttalSpeechMins;

    @Transient
    private Integer rebuttalSpeechSecs;

    @Transient
    private Integer crossExaminationMins;

    @Transient
    private Integer crossExaminationSecs;

    public void computeSecondsBasedOnMinsAndSecs() {
        prepTimeSeconds = prepTimeMins * 60 + prepTimeSecs;
        constSpeechSeconds = constSpeechMins * 60 + constSpeechSecs;
        rebuttalSpeechSeconds = rebuttalSpeechMins * 60 + rebuttalSpeechSecs;
        crossExaminationSeconds = crossExaminationMins * 60 + crossExaminationSecs;
    }

    public void computeMinsAndSecsBasedOnSeconds() {
        prepTimeMins = prepTimeSeconds / 60;
        prepTimeSecs = prepTimeSeconds% 60;
        constSpeechMins = prepTimeSeconds / 60;
        constSpeechSecs = prepTimeSeconds % 60;
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
