package com.example.demo.reply.entity;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString(exclude = {"freeboard", "user", "lawyer"})
@EqualsAndHashCode(of = "rno")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "reply")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rno;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime regDate;

    // 자유게시판
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bno")
    private Freeboard freeboard;

    // 작성자 - 사용자 닉네임 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 작성자 - 변호사 이름 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id")
    private Lawyer lawyer;

}
