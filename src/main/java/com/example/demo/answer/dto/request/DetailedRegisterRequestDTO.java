package com.example.demo.answer.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder


public class DetailedRegisterRequestDTO {

    private int answerNum;

    @NotBlank
    private String detailedAns;


}
