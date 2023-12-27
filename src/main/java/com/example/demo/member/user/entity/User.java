package com.example.demo.member.user.entity;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.reply.entity.Reply;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    private String id;

    @Column(nullable = false, name = "user_pw")
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String joinMethod;

    @Builder.Default
    private int hammer = 0;

    @Builder.Default
    private String authority = "user";

    private String accessToken;

    private String refreshToken;
    
    // 온라인 상담 글
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consulting> consultingList = new ArrayList<>();

    // 자유게시판
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Freeboard> freeboardList = new ArrayList<>();

    // 댓글
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();

    // refreshToken 수정 가능한 setter 메서드
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public void setHammer(int hammer) {
        this.hammer = hammer;
    }
}
