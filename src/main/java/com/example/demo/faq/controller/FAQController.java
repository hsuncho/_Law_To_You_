package com.example.demo.faq.controller;

import com.example.demo.faq.dto.*;
import com.example.demo.faq.entity.FAQ;
import com.example.demo.faq.service.FAQService;
import com.example.demo.freeboard.dto.PageDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/faq")
@CrossOrigin
@Slf4j
public class FAQController {

    private final FAQService faqService;

    // 백문백답 전체 요청
    @GetMapping
    public ResponseEntity<?> FAQList(PageDTO pageDTO) {
        log.info("/api/faq?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        FAQListResponseDTO dto = faqService.getFAQs(pageDTO);

        return ResponseEntity.ok().body(dto);

    }

    // 백문백답 대분류 클릭시 중분류 리스트 값, 대분류 기준 전체 리스트(번호, 중분류, subject) 출력
    @GetMapping("/{largeSection}")
    public ResponseEntity<?> FAQLargeSection(@PathVariable String largeSection) {
        log.info("/api/faq/{} GET", largeSection);

        try {
            List<String> middleList = faqService.getDetail(largeSection); // 중분류 리스트

            // 대분류 기준 리스트 List<FAQ>
            List<FAQ> list = faqService.getLargeSecANDMiddleList(largeSection);
            List<FAQMiddleAndQMSDTO> response = new ArrayList<>();
            int rowNum = 1;
            for(FAQ faq : list) {
                response.add(new FAQMiddleAndQMSDTO(rowNum, faq.getQno(), faq.getMiddleSection(), faq.getSubject()));
                rowNum++;
            }

            FAQMiddleSecAndList dto = new FAQMiddleSecAndList(middleList, response);


            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 백문백답 중분류 클릭시 중분류 기준 전체 리스트(번호, 중분류, subject) 출력
    @GetMapping("/{largeSection}/{middleSection}")
    public ResponseEntity<?> FAQMiddleSection(@PathVariable String largeSection, @PathVariable String middleSection) {
        log.info("/api/faq/{}/{} GET!", largeSection, middleSection);

        try {
            List<FAQ> qnaList = faqService.getMiddleANDList(largeSection, middleSection);
            List<FAQMiddleSecAndSubjectDTO> qna = new ArrayList<>();
            int rowNum = 1;
            for(FAQ faq : qnaList) {
                qna.add(new FAQMiddleSecAndSubjectDTO(rowNum, faq.getMiddleSection(), faq.getSubject()));
                rowNum++;
            }

            return ResponseEntity.ok().body(qna);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 특정 백문백답 클릭시 질문, 답변 출력
    @GetMapping("/{largeSection}/{middleSection}/{qno}")
    public ResponseEntity<?> FAQMiddleSectionQna(@PathVariable String largeSection, @PathVariable String middleSection,
                                                 @PathVariable int qno) {
        log.info("/api/faq/{}/{}/{} GET!", largeSection, middleSection, qno);

        try{
            List<FAQ> qnaList = faqService.getMiddleANDQna(largeSection, middleSection, qno);
            List<QuestionAnswerDTO> qna = new ArrayList<>();
            for (FAQ f : qnaList) {
                qna.add(new QuestionAnswerDTO(f.getQuestion(), f.getAnswer()));
            }
            return ResponseEntity.ok().body(qna);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
