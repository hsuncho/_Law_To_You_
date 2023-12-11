package com.example.demo.consulting.entity;

import lombok.*;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import javax.persistence.*;
import java.beans.ExceptionListener;

@Getter
@Setter
@ToString(exclude = "consulting")
@EqualsAndHashCode(of = "fileNum")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "detailed_consulting_file")
public class DetailedConsultingFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileNum;

    @Column(nullable = false)
    private String route;

    // 온라인 상담에서 상담번호 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consult_num")
    private Consulting consulting;
}