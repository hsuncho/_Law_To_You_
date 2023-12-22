package com.example.demo.consulting.dto.response;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.ConsultingFile;
import lombok.*;


@Getter @Setter
@ToString @EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ConsultingFileDTO {

    private String route;

    public ConsultingFile toEntity(Consulting saved) {
        return ConsultingFile.builder()
                .route(this.route)
                .consulting(saved)
                .build();
    }
}
