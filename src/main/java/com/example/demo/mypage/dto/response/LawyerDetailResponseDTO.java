package com.example.demo.mypage.dto.response;

import com.example.demo.member.lawyer.entity.Lawyer;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerDetailResponseDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String id;

    @Email
    @NotBlank
    private String email;

    private boolean approval;

    public LawyerDetailResponseDTO(Lawyer lawyer) {
        this.name = lawyer.getName();
        this.id = lawyer.getLawyerId();
        this.email = lawyer.getEmail();
        this.approval = lawyer.isApproval();
    }
}
