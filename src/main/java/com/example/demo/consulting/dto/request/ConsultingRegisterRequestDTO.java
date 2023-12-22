package com.example.demo.consulting.dto.request;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.ConsultingFile;
import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ConsultingRegisterRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String largeSection;

    public Consulting toEntity(User user) {
        return Consulting.builder()
                .writer(user.getNickname())
                .title(this.title)
                .content(this.content)
                .largeSection(this.largeSection)
                .user(user)
                .build();
    }

}
