package com.se.DebateApp.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
    private String topic;

    @Column(nullable = false, length = 1000)
    private String statement;


}
