package com.example.demo.answer.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.answer.dto.request.AnswerRegisterRequestDTO;
import com.example.demo.answer.dto.response.AnswerDetailResponseDTO;
import com.example.demo.answer.dto.response.AnswerListResponseDTO;
import com.example.demo.answer.service.AnswerService;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.service.S3Service;
import com.example.demo.token.auth.TokenMemberInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/answer")
@CrossOrigin
public class AnswerController {

    private final AnswerService answerService;
    private final S3Service s3Service;

    // 토큰 값 얻어오기
    @GetMapping("/status")
    public ResponseEntity<?> status(
            @AuthenticationPrincipal TokenMemberInfo memberInfo
    ) {
        return ResponseEntity.ok().build();
    }

    //답변 목록 요청
    @GetMapping
    public ResponseEntity<?> getList(int consultNum, PageDTO pageDTO) {
        log.info("/api/counsel?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());
        AnswerListResponseDTO responseDTO = answerService.getList(consultNum, pageDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 짧은 답변 등록
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_lawyer')") // 변호사가 아니라면 인가처리 거부
    public ResponseEntity<?> registerShortAns(
            int consultNum,
            @Validated @RequestPart("answer") AnswerRegisterRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo,
            BindingResult result
    ){
        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        AnswerDetailResponseDTO responseDTO = answerService.insert(consultNum, requestDTO, tokenMemberInfo);
        return null;
    }

    /*
    // 답변 채택 (사용자)
    @PutMapping("/adopt")
    @PreAuthorize("hasRole('Role_user')") // 사용자가 아니라면 인가처리 거부
    public ResponseEntity<?> adoptAnswer(int answerNum) {

       AnswerListResponseDTO responseDTO = answerService.adoptShortAns(answerNum);

       return ResponseEntity.ok().body(responseDTO);
    }

     */


//    // 깊은 답변 등록
//    @PutMapping("/update")
//    @PreAuthorize("hasRole('Role_lawyer')")















}
