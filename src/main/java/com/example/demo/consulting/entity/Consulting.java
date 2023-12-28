package com.example.demo.consulting.entity;

import com.example.demo.answer.entity.Answer;
import com.example.demo.member.user.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter @Setter
@ToString(exclude = {"user"}) @EqualsAndHashCode(of = "consultNum")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "consulting")
public class Consulting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int consultNum;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime regDate;

    private LocalDateTime updateDate;
//    @UpdateTimestamp
//    @Column(insertable = false)
//    private LocalDateTime updateDate;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String largeSection;

    private String updateTitle;

    private String updateContent;

    // 작성자 - 사용자 id 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 답변
    @OneToMany(mappedBy = "consulting", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answerList = new ArrayList<>();

    // 온라인 상담 요청 글 첨부파일
    @Builder.Default
    @OneToMany(mappedBy = "consulting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultingFile> consultingFiles = new ArrayList<>();

    // 깊은 상담 첨부파일
    @Builder.Default
    @OneToMany(mappedBy = "consulting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailedConsultingFile> detailedConsultingFiles = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        this.updateDate = null;
    }

    @PreUpdate
    private void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }









}