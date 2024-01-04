package com.example.demo.answer.api;

import com.example.demo.answer.dto.request.DetailedRegisterRequestDTO;
import com.example.demo.answer.dto.response.DetailedResponseDTO;
import com.example.demo.answer.entity.Answer;
import com.example.demo.answer.repository.AnswerRepository;
import com.example.demo.consulting.repository.ConsultingRepository;
import com.example.demo.consulting.service.ConsultingService;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.demo.answer.dto.request.AnswerRegisterRequestDTO;
import com.example.demo.answer.dto.response.AnswerDetailResponseDTO;
import com.example.demo.answer.dto.response.AnswerListResponseDTO;
import com.example.demo.answer.service.AnswerService;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.service.S3Service;
import com.example.demo.token.auth.TokenMemberInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/answer")
@CrossOrigin
public class AnswerController {

    private final ConsultingService consultingService;
    private final AnswerService answerService;
    private final S3Service s3Service;
    private final AnswerRepository answerRepository;
    private final LawyerRepository lawyerRepository;
    private final UserRepository userRepository;

    // 토큰 값 얻어오기
    @GetMapping("/status")
    public ResponseEntity<?> status(
            @AuthenticationPrincipal TokenMemberInfo memberInfo
    ) {
        return ResponseEntity.ok().build();
    }

    //짧은 답변 목록 요청(해당 상담 글 작성자 + 모든 변호사)
    @GetMapping
    public ResponseEntity<?> getList(@RequestParam int consultNum, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo) {

        PageDTO pageDTO = new PageDTO(page, size);

        log.info("/api/counsel?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        if(!consultingService.validateWriter(tokenMemberInfo, consultNum)) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }

        AnswerListResponseDTO responseDTO = answerService.getList(consultNum, pageDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 짧은 답변 등록(하나의 온라인 상담 글에는 변호사당 하나의 짧은 답변 등록만 허용)
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_lawyer')") // 변호사가 아니라면 인가처리 거부
    public ResponseEntity<?> registerShortAns(
            @Validated @RequestBody AnswerRegisterRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo,
            BindingResult result
    ){
        if(!answerService.validateForRegister(tokenMemberInfo, requestDTO.getConsultNum())) {
            return ResponseEntity.badRequest().body("하나의 온라인 상담 글에는 하나의 답변만 작성 가능합니다.");
        }

        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        AnswerDetailResponseDTO responseDTO = answerService.insert(requestDTO, tokenMemberInfo);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 답변 채택 (사용자)
    @PutMapping("/adopt")
    @PreAuthorize("hasRole('ROLE_user')") // 사용자가 아니라면 인가처리 거부
    public ResponseEntity<?> adoptAnswer(@RequestParam int answerNum, @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo) {

        if(!answerService.validateForAdopt(tokenMemberInfo, answerNum)) {
            return ResponseEntity.badRequest().body("답변 채택에 대한 요청이 승인되지 않았습니다.");
        }

        Answer answer = answerRepository.findById(answerNum).orElseThrow();

        User user = userRepository.findById(tokenMemberInfo.getId()).orElseThrow();

        int newHammer = user.getHammer() - answer.getReqHammer();
        if(newHammer < 0) return ResponseEntity.badRequest().body("shortage-hammer");
        user.setHammer(newHammer);

       AnswerListResponseDTO responseDTO = answerService.adoptShortAns(answerNum);
       return ResponseEntity.ok().body(responseDTO);
    }

    // 깊은 답변 등록 (채택된 변호사) + 단 1회만 수정 가능하도록
    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_lawyer')") // 변호사가 아니라면 인가처리 거부
    public ResponseEntity<?> registerDetailedAns(
            @Validated @RequestPart("detailedAnswer") DetailedRegisterRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo,
            @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles,
            BindingResult result
    ) {
        log.info("/api/answer/update PUT!! - payload: {}", requestDTO);

        if(requestDTO == null) {
            return ResponseEntity.badRequest().body("등록할 깊은 답변 정보를 전달해 주세요!");
        }

        if(result.hasErrors()) {
            log.warn("DTO detailedAns(검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        if(answerRepository.findById(requestDTO.getAnswerNum()).orElseThrow().getAdopt() == 0) {
            return ResponseEntity.badRequest().body("채택되었을 시에만 깊은 답변 등록이 가능합니다.");
        }

        if(answerRepository.findById(requestDTO.getAnswerNum()).orElseThrow().getDetailAns() != null) {
            return ResponseEntity.badRequest().body("깊은 답변은 등록 이후에는 수정 불가능한 영역입니다.");
        }

        if(!(answerRepository.findById(requestDTO.getAnswerNum()).orElseThrow().getLawyer().equals(
            lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow())
        )) {
            return ResponseEntity.badRequest().body("wrong-authority");
        }

        try {
            List<String> uploadedFileList = new ArrayList<>();
            multipartFiles.forEach( multipartFile -> {

                // 깊은 답변 첨부파일
                if(multipartFile != null) {
                    log.info("answer file name: {}", multipartFile.getOriginalFilename());

                    try {
                        uploadedFileList.add(consultingService.uploadFiles(multipartFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            DetailedResponseDTO responseDTO = answerService.registerDetailed(requestDTO, tokenMemberInfo, uploadedFileList);
            return ResponseEntity.ok().body(responseDTO);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("깊은 답변 등록 중 에러가 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
        }

    }

    // 깊은 답변 상세보기 요청
    @GetMapping("/detail")
    public ResponseEntity<?> getDetailedAns(@RequestParam int consultNum, @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo) {
        log.info("/api/answer/detial/{} GET", consultNum);

        // 깊은 상담 작성자 + 깊은 답변 변호사가 아닐 경우 인가처리 거부하는 메서드
//        if(!answerService.validateForDetail(tokenMemberInfo, consultNum)) {
//            return ResponseEntity.badRequest().body("wrong-authority-request");
//        }
       Answer answer = answerService.getDetail(consultNum, tokenMemberInfo);

        if(answer.getDetailAns()== null) {
            return ResponseEntity.badRequest().body("no-detailed-answer");
        }

       return ResponseEntity.ok().body(new DetailedResponseDTO(answer));
    }



}
