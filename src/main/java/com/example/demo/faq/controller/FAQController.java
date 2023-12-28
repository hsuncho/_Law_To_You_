package com.example.demo.faq.controller;

import com.example.demo.faq.dto.*;
import com.example.demo.faq.entity.FAQ;
import com.example.demo.faq.service.FAQService;
import com.example.demo.freeboard.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    public ResponseEntity<?> FAQList(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        log.info("/api/faq?page={}&size={}", page, size);

        PageDTO pageDTO = new PageDTO(page, size);
        FAQListResponseDTO dto = faqService.getFAQs(pageDTO);

        return ResponseEntity.ok().body(dto);

    }

    // 백문백답 대분류 클릭시 중분류 리스트 값, 대분류 기준 전체 리스트(번호, 중분류, subject, question, answer) 출력
    @GetMapping("/{largeSection}")
    public ResponseEntity<?> FAQLargeSection(@PathVariable String largeSection,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("/api/faq/{} GET", largeSection);

        try {
            PageDTO pageDTO = new PageDTO(page, size);
            // 대분류 기준 리스트 List<FAQ>
            List<FAQ> list = faqService.getLargeSecANDMiddleList(largeSection, pageDTO);
            List<FAQMiddleAndQMSDTO> response = new ArrayList<>();
            for(FAQ faq : list) {
                response.add(new FAQMiddleAndQMSDTO(faq.getQno(), faq.getMiddleSection(),
                                                    faq.getSubject(), faq.getQuestion(), faq.getAnswer()));
            }

            FAQMiddleSecAndList dto = faqService.getDetail(largeSection, response);
            return ResponseEntity.ok().body(new FAQMiddleSecAndList(dto.getLargeCount(), dto.getMiddleSection(), dto.getListSearchedByLargeSec()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 백문백답 중분류 클릭시 중분류 기준 전체 리스트(번호, 중분류, subject, question, answer) 출력
    @GetMapping("/{largeSection}/{middleSection}")
    public ResponseEntity<?> FAQMiddleSection(@PathVariable String largeSection,
                                              @PathVariable String middleSection,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size
    ) {
        log.info("/api/faq/{}/{} GET!", largeSection, middleSection);

        try {
            PageDTO pageDTO = new PageDTO(page, size);

            List<FAQ> qnaList = faqService.getMiddleANDList(largeSection, middleSection, pageDTO);
            int middleCount = faqService.getMiddleSecCnt(largeSection, middleSection);
            List<FAQMiddleSecAndSubjectDTO> qna = new ArrayList<>();
            for(FAQ faq : qnaList) {
                qna.add(new FAQMiddleSecAndSubjectDTO(faq.getMiddleSection(), faq.getSubject()
                                                        , faq.getQuestion(), faq.getAnswer()));
            }
            return ResponseEntity.ok().body(new FAQListCountDTO(middleCount, qna));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
