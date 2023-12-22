package com.example.demo.freeboard.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class PageDTO {


    private int page;
    private int size;

    public PageDTO(int page, int size) {
        this.page = page;
        this.size = size;
    }

}
