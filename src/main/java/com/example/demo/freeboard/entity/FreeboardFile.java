package com.example.demo.freeboard.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString @EqualsAndHashCode(of = "fileNum")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "freeboard_file")
public class FreeboardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileNum;

    @Column(nullable = false)
    private String route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bno")
    private Freeboard freeboard;

}
