package com.se.DebateApp.Model;

import javax.persistence.*;

@Entity
@Table(name = "links_to_resources")
public class LinkToResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String link;
}
