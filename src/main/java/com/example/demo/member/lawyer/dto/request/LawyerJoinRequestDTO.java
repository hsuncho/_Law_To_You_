package com.example.demo.member.lawyer.dto.request;

import com.example.demo.member.lawyer.entity.Lawyer;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString @EqualsAndHashCode(of = "lawyerId")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerJoinRequestDTO {

    @NotBlank
    private String lawyerId;

    @NotBlank
    private String lawyerPw;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private int lawyerNum;


    public Lawyer toEntity(String uploadFile) {
        return Lawyer.builder()
                .lawyerId(this.lawyerId)
                .lawyerPw(this.lawyerPw)
                .name(this.name)
                .email(this.email)
                .lawyerNum(this.lawyerNum)
                .attachedFile(uploadFile)
                .build();
    }




}


