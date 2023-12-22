package com.example.demo.freeboard.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
