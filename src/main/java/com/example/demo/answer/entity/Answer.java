package com.example.demo.answer.entity;


import com.example.demo.consulting.entity.Consulting;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.lawyer.entity.Lawyer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = "answerNum")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int answerNum;

    @Column(nullable = false)
    private int adopt;

    @Column(nullable = false)
    private String shortAns;

    private String detailAns;

    @Column(nullable = false)
    private String writer;

    @CreationTimestamp
    private LocalDateTime regDate;

    private String attachedFile;

    @Column(nullable = false)
    private int reqHammer;

    // 작성자 - 변호사 이름 조회
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id")
    private Lawyer lawyer;

    // 온라인 상담
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consult_num")
    private Consulting consulting;
    
    // 답변 첨부파일
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AnswerFile> answerFiles = new ArrayList<>();

}