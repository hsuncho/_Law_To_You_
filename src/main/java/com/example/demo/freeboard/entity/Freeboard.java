package com.example.demo.freeboard.entity;

import com.example.demo.lawyer.entity.Lawyer;
import com.example.demo.reply.entity.Reply;
import com.example.demo.user.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"user", "lawyer", "freeboardFiles", "replyList"})
@EqualsAndHashCode(of = "bno")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class Freeboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bno;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime regDate;

    @Column(nullable = false)
    private String title;

    // 작성자 - 사용자 닉네임 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 작성자 - 변호사 이름 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id")
    private Lawyer lawyer;

    // 첨부파일
    @OneToMany(mappedBy = "freeboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FreeboardFile> freeboardFiles = new ArrayList<>();

    // 댓글
    @OneToMany(mappedBy = "freeboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reply> replyList = new ArrayList<>();

}
