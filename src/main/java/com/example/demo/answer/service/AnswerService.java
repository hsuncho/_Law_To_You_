package com.example.demo.answer.service;

import com.example.demo.answer.dto.request.AnswerRegisterRequestDTO;
import com.example.demo.answer.dto.response.AnswerDetailResponseDTO;
import com.example.demo.answer.dto.response.AnswerListResponseDTO;
import com.example.demo.answer.entity.Answer;
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
                .pageInfo(new PageResponseDTO(answers))
                .answerList(detailList)
                .build();
    }

    public AnswerDetailResponseDTO insert(int consultNum, AnswerRegisterRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo) {

        Lawyer lawyer = lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow();
        Consulting consulting = consultingRepository.findById(consultNum).orElseThrow();

        answerRepository.save(requestDTO.toEntity(consulting, lawyer));


        return null;
    }

/*
    public AnswerListResponseDTO adoptShortAns(int answerNum) {

        Answer answer = answerRepository.findById(answerNum).orElseThrow();

        answer.setAdopt(1);

        answer.getConsulting().getConsultNum();


    }
 */
}
