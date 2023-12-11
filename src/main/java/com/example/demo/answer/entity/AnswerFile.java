package com.example.demo.answer.entity;


import lombok.*;

import javax.persistence.*;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = "fileNum")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "answer_file")
public class AnswerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileNum;

    @Column(nullable = false)
    private String route;

    // 답변번호 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_num")
    private Answer answer;
}