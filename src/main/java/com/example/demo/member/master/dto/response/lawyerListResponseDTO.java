package com.example.demo.member.master.dto.response;


import com.example.demo.member.lawyer.entity.Lawyer;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class lawyerListResponseDTO {


    private String lawyerId;
    private String name;
    private int lawyerNum;
    private boolean approval;
    private String attachedFile;

    public lawyerListResponseDTO(Lawyer lawyer) {
        this.lawyerId = lawyer.getLawyerId();
        this.name = lawyer.getName();
        this.lawyerNum = lawyer.getLawyerNum();
        this.approval = lawyer.isApproval();
        this.attachedFile = lawyer.getAttachedFile();
    }








}
