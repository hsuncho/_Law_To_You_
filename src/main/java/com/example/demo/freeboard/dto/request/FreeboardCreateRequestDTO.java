package com.example.demo.freeboard.dto.request;


import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardCreateRequestDTO {

    @NotBlank
    private String content;

    @NotBlank
    private String title;

    public Freeboard toEntity(User user) {
        return Freeboard.builder()
                .content(this.content)
                .title(this.title)
                .user(user)
                .build();
    }

}
