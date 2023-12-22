package com.example.demo.freeboard.dto.response;

import com.example.demo.faq.entity.FAQ;
import com.example.demo.freeboard.entity.Freeboard;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Setter
@Getter
@ToString
public class PageResponseDTO {

    private int startPage;
    private int endPage;
    private int currentPage;

    private boolean prev;
    private boolean next;


    private int totalCount;

    private static final int PAGE_COUNT = 10;

    public <T> PageResponseDTO(Page<T> pageDate) {

        this.totalCount = (int) pageDate.getTotalElements();
        this.currentPage = pageDate.getPageable().getPageNumber() + 1;
        this.endPage
                = (int) (Math.ceil((double) currentPage / PAGE_COUNT) * PAGE_COUNT);
        this.startPage = endPage - PAGE_COUNT + 1;

        int realEnd = pageDate.getTotalPages();

        if(realEnd < this.endPage) this.endPage = realEnd;

        this.prev = startPage > 1;
        this.next = endPage < realEnd;
    }

}

