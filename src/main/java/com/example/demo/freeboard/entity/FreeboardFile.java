package com.example.demo.freeboard.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString @EqualsAndHashCode(of = "fileNum")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "freeboardfile")
public class FreeboardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileNum;

    private String route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bno")
    private Freeboard freeboard;

}
