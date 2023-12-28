package com.example.demo.consulting.entity;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString(exclude = "consulting")
@EqualsAndHashCode(of = "fileNum")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class ConsultingFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileNum;
    private String route;

    // 온라인 상담에서 상담번호 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consult_num")
    private Consulting consulting;
}