package com.example.demo.faq.service;

import com.example.demo.faq.dto.*;
import com.example.demo.faq.entity.FAQ;
import com.example.demo.faq.repository.FAQRepository;
import com.example.demo.freeboard.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;


    public FAQListResponseDTO getFAQs(PageDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("qno").ascending()
        );

        Page<FAQ> faqs = faqRepository.findAll(pageable);

        List<FAQ> faqList = faqs.getContent();

        List<FAQDetailResponseDTO> detailList
                = faqList.stream()
                .map(FAQDetailResponseDTO::new)
                .collect(Collectors.toList());

        return FAQListResponseDTO.builder()
                .count(detailList.size())
                .pageInfo(new PageResponseDTO(faqs))
                .posts(detailList)
                .build();
    }

    public FAQMiddleSecAndList getDetail(String largeSection, List<FAQMiddleAndQMSDTO> listSearchedByMiddle) {

        return FAQMiddleSecAndList.builder()
                .largeCount(faqRepository.findByLargeSecCnt(largeSection)) // 대분류 클릭시 전체 행 개수
                .listSearchedByLargeSec(listSearchedByMiddle)
                .middleSection(faqRepository.findByName(largeSection))
                .build();

    }


    public List<FAQ> getLargeSecANDMiddleList(String largeSection, PageDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("qno").ascending()
        );

        return faqRepository.findByLargeSec(largeSection, pageable);
    }

    public List<FAQ> getMiddleANDList(String largeSection, String middleSection, PageDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("qno").ascending()
        );

        return faqRepository.findByMiddleSec(largeSection, middleSection, pageable);


    }

    public int getMiddleSecCnt(String largeSection, String middleSection) {
        return faqRepository.findByMiddleSecCnt(largeSection,middleSection);
    }

    public List<FAQ> getMiddleANDQna(String largeSection, String middleSection, int qno) {
        return faqRepository.findByMiddleSecQna(largeSection, middleSection, qno);
    }

}



















