package com.se.DebateApp.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "links_to_resources")
@Getter
@Setter
@NoArgsConstructor
public class LinkToResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String link;
}
