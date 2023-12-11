package com.example.demo.freeboard.controller;

import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import com.example.demo.freeboard.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/freeboard")
@CrossOrigin // 백과 프론트를 연결시키는 아노테이션
public class FreeboardController {

    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/freeboard?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        return null;
    }

    ;

    @PostMapping("/register")
    public ResponseEntity<?> writeFreeboard(
            @Validated @RequestBody FreeboardCreateRequestDTO requestDTO,
            BindingResult result
    ) {
        /*
        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body()
        }



    }
         */
        return null;
    }
}
