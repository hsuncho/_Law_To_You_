package com.example.demo.mypage.dto.response;

import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDetailResponseDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

<<<<<<< HEAD
    @NotEmpty
    private int hammer;

    public UserDetailResponseDTO(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
=======
    public UserDetailResponseDTO(User saved) {
        this.id = saved.getId();
        this.nickname = saved.getNickname();
        this.email = saved.getEmail();
>>>>>>> 6cb878cdb58f44497aaa7be24b93759e3b081559
    }


}
