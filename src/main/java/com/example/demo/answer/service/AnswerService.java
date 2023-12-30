package com.example.demo.answer.service;

import com.example.demo.answer.dto.request.AnswerRegisterRequestDTO;
import com.example.demo.answer.dto.request.DetailedRegisterRequestDTO;
import com.example.demo.answer.dto.response.AnswerDetailResponseDTO;
import com.example.demo.answer.dto.response.AnswerListResponseDTO;
import com.example.demo.answer.dto.response.DetailedAnswerFileDTO;
import com.example.demo.answer.dto.response.DetailedResponseDTO;
import com.example.demo.answer.entity.Answer;
import com.example.demo.answer.entity.AnswerFile;
import com.example.demo.answer.repository.AnswerFileRepository;
import com.example.demo.answer.repository.AnswerRepository;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.repository.ConsultingRepository;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AnswerService {

    private final ConsultingRepository consultingRepository;
    private final AnswerRepository answerRepository;
    private final LawyerRepository lawyerRepository;
    private final AnswerFileRepository answerFileRepository;
    private final EntityManager em;

    @Value("${aws.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucketName}")
    private String bucketName;

    public AnswerListResponseDTO getList(int consultNum, PageDTO pageDTO) {

        // offset = (현재 페이지번호 - 1) * 페이지당 요청하는 자료 개수
        Pageable pageable = PageRequest.of(
                pageDTO.getPage() - 1,
                pageDTO.getSize(),
                Sort.by("regDate").descending()
        );

        Consulting foundConsulting = consultingRepository.findById(consultNum).orElseThrow();

        Page<Answer> answers = answerRepository.findAll(pageable);

        List<Answer> answerList = answers.stream().filter(
                answer -> answer.getConsulting().equals(foundConsulting))
                .collect(Collectors.toList());

        List<AnswerDetailResponseDTO> detailList
                = answerList.stream()
                .map(AnswerDetailResponseDTO::new)
                .collect(Collectors.toList());

        return AnswerListResponseDTO.builder()
                .count(detailList.size())
                .answerList(detailList)
                .build();
    }

    public AnswerDetailResponseDTO insert(int consultNum, AnswerRegisterRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo) {

        Lawyer lawyer = lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow();
        Consulting consulting = consultingRepository.findById(consultNum).orElseThrow();

        Answer saved = answerRepository.save(requestDTO.toEntity(consulting, lawyer));

        return new AnswerDetailResponseDTO(saved);
    }


    public AnswerListResponseDTO adoptShortAns(int answerNum) {

        Answer answer = answerRepository.findById(answerNum).orElseThrow();
        answer.setAdopt(1);

        return getList(answer.getConsulting().getConsultNum(),
                PageDTO.builder()
                        .page(1)
                        .size(10)
                        .build());
    }


    public DetailedResponseDTO registerDetailed(DetailedRegisterRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo ,List<String> uploadedFileList) {

        Answer answer = answerRepository.findById(requestDTO.getAnswerNum()).orElseThrow();
        answer.setDetailAns(requestDTO.getDetailedAns());
        answer.setWriter(lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow().getName());

        uploadedFileList.forEach(file -> {
            AnswerFile answerFile = new DetailedAnswerFileDTO(file).toEntity(answer);
            answerFileRepository.save(answerFile);
        });

        em.flush();
        em.clear();

        Answer saved = answerRepository.save(answer);

        return new DetailedResponseDTO(saved);
    }

    public Answer getDetail(int answerNum) {
        return answerRepository.findById(answerNum).orElseThrow();
    }


    public Boolean validateForAdopt(TokenMemberInfo tokenMemberInfo, int answerNum) {
        // 이미 채택된 답변이 있다면 거부
        List<Answer> answerList = answerRepository.findById(answerNum).orElseThrow().getConsulting().getAnswerList();
        for(Answer answer : answerList) {
            if(answer.getAdopt() == 1) return false;
        }
        // 변호사는 모두 거부
        if(tokenMemberInfo.getAuthority().equals("lawyer")) return false;

        // 요청 보낸 token의 user와 온라인 상담글을 작성한 사용자가 동일인물인가
        else if(tokenMemberInfo.getAuthority().equals("user")) {
            return tokenMemberInfo.getId().equals(
                    answerRepository.findById(answerNum).orElseThrow()
                            .getConsulting().getUser().getId()
            );
        }
        return false;
    }

    public boolean validateForDetail(TokenMemberInfo tokenMemberInfo, int answerNum) {
        Answer answer = answerRepository.findById(answerNum).orElseThrow();

        if(tokenMemberInfo.getAuthority().equals("lawyer")) {
            return answer.getLawyer().getLawyerId().equals(
                    tokenMemberInfo.getId()
            );
        } else if(tokenMemberInfo.getAuthority().equals("user")) {
            return answer.getConsulting().getUser().getId().equals(
                    tokenMemberInfo.getId()
            );
        }
        return false;
    }

    public boolean validateForRegister(TokenMemberInfo tokenMemberInfo, int consultNum) {

        List<Answer> answerList = consultingRepository.findById(consultNum).orElseThrow()
                .getAnswerList();
        for(Answer answer : answerList) {
           if(answer.getLawyer().getLawyerId()
                   .equals(
                           tokenMemberInfo.getId()
                   )) return false;
        }
        return true;
    }
}
