package com.example.demo.freeboard.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class PageDTO {


    private int page;
    private int size;

    public PageDTO() {
        this.page = 1;
        this.size = 10;
    }

}
