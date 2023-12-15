package com.example.demo.member;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString(exclude = {"user", "lawyer"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
public class Member {

    @Id
    @Column(name = "member_id")
    private String id;

    @Column(name="member_authority", nullable = false)
    private String authority;

    // 사용자 정보 가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    // 변호사 정보 가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lawyer_id")
    private Lawyer lawyer;

}
