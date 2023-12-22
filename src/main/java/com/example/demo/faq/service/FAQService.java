package com.example.demo.faq.service;

import com.example.demo.faq.dto.FAQDetailResponseDTO;
import com.example.demo.faq.dto.FAQListResponseDTO;
import com.example.demo.faq.dto.PageResponseDTO;
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

    public List<String> getDetail(String largeSection) {



        return faqRepository.findByName(largeSection);

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

    public List<FAQ> getMiddleANDQna(String largeSection, String middleSection, int qno) {
        return faqRepository.findByMiddleSecQna(largeSection, middleSection, qno);
    }
}



















