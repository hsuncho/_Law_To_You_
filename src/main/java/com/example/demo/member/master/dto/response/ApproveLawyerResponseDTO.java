package com.example.demo.member.master.dto.response;

import com.example.demo.member.lawyer.entity.Lawyer;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApproveLawyerResponseDTO {

    private String lawyerId;
    private String name;
    private String lawyerNum;
    private String route;
    private boolean ifApproval;

    public ApproveLawyerResponseDTO(Lawyer saved) {
        this.lawyerId = saved.getLawyerId();
        this.name = saved.getName();
        this.lawyerNum = saved.getName();
        this.route = saved.getAttachedFile();
        this.ifApproval = saved.isApproval();
    }

}
