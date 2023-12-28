package com.example.demo.mypage.dto.response;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerDetailResponseDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private boolean approval;

    public LawyerDetailResponseDTO(Lawyer saved) {
        this.id = saved.getLawyerId();
        this.name = saved.getName();
        this.email = saved.getEmail();
        this.approval = saved.isApproval();
    }

}
