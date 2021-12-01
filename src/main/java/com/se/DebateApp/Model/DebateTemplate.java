package com.se.DebateApp.Model;

import javax.persistence.*;

@Entity
@Table(name = "debate_templates")
public class DebateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User owner;

    @Column(nullable = true, length = 100)
    private String topic;

    @Column(nullable = false, length = 1000)
    private String statement;


}
