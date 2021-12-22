package com.se.DebateApp.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userName;

    @Column(nullable = false, length = 100)
    private String password;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy="owner")
    private Set<DebateTemplate> debateTemplates = new HashSet<>();

    public void addNewDebateTemplate(DebateTemplate debateTemplate) {
        debateTemplate.setOwner(this);
        debateTemplates.add(debateTemplate);
    }
}
