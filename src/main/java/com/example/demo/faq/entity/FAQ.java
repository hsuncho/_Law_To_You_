package com.example.demo.faq.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class FAQ {

    @Id
    private int qno;

    private String answer;

    @Column(name = "large_section")
    private String largeSection;

    @Column(name = "large_section_num")
    private int largeSectionNum;

    @Column(name = "middle_section")
    private String middleSection;

    private String question;

    @Column(name = "middle_section_num")
    private int middleSectionNum;

    @Column(name = "subject")
    private String subject;


}
