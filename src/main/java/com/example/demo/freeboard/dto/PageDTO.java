package com.example.demo.freeboard.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class PageDTO {


    private int page = 1;
    private int size = 10;

    public PageDTO(int page, int size) {
        this.page = page;
        this.size = size;
    }

}
