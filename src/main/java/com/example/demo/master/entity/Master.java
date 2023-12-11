package com.example.demo.master.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@ToString @EqualsAndHashCode(of = "masterId")
@NoArgsConstructor @AllArgsConstructor
@Builder

@Table(name = "master")
@Entity
public class Master {

    @Id
    private String masterId;

    @Column(name = "master_pw", nullable = false)
    private String masterPw;

    @Column(nullable = false)
    @Builder.Default
    private String authority = "master";

    private String accessToken;



}
