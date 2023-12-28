package com.example.demo.member.lawyer.entity;

import com.example.demo.answer.entity.Answer;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.Member;
import com.example.demo.reply.entity.Reply;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(of = "lawyerId")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "lawyer")
public class Lawyer {

    @Id
    @Column(name = "lawyer_id")
    private String lawyerId;

    @Builder.Default
    @Column(nullable = false)
    private boolean approval = false;

    @Builder.Default
    private String authority = "lawyer";

    @Column(name = "lawyer_pw", nullable = false)
    private String lawyerPw;

    @Column(name = "lawyer_num", nullable = false)
    private int lawyerNum;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    private int hammer = 0;

    private String accessToken;

    private String refreshToken;

    @Column(nullable = false)
    private String attachedFile;


    // 자유게시판 (freeboard)
    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Freeboard> freeboardList = new ArrayList<>();

    // 댓글 (reply)
    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reply> replyList = new ArrayList<>();

    // 답변(answer)
    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;

    public void setLawyerPw(String password) {
        this.lawyerPw = password;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public void setHammer(int hammer) {
        this.hammer = hammer;
    }
}
