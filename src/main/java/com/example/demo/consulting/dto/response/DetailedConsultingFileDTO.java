package com.example.demo.consulting.dto.response;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.ConsultingFile;
import com.example.demo.consulting.entity.DetailedConsultingFile;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetailedConsultingFileDTO {

    private String route;

    public DetailedConsultingFile toEntity(Consulting saved) {
        return DetailedConsultingFile.builder()
                .route(this.route)
                .consulting(saved)
                .build();
    }
}
