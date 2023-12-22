package com.example.demo.consulting.api;

import com.example.demo.aws.S3Service;
import com.example.demo.consulting.dto.request.ConsultingRegisterRequestDTO;
import com.example.demo.consulting.dto.request.DetailedConsultingRegisterRequestDTO;
import com.example.demo.consulting.dto.response.ConsultingDetailResponseDTO;
import com.example.demo.consulting.dto.response.ConsultingListResponseDTO;
import com.example.demo.consulting.dto.response.UpdatedConsultingDetailResponseDTO;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.service.ConsultingService;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/counsel")
@CrossOrigin
public class ConsultingController {

    private final ConsultingService consultingService;
    private final S3Service s3Service;

    // 토큰 값 얻어오기
    @GetMapping("/status")
    public ResponseEntity<?> status(
            @AuthenticationPrincipal TokenMemberInfo memberInfo
    ) {
        return ResponseEntity.ok().build();
    }

    // 온라인 상담글 목록 요청
    @GetMapping
    @PreAuthorize("hasRole('ROLE_lawyer')") // 변호사가 아니라면 인가 처리 거부
    public ResponseEntity<?> getList(PageDTO pageDTO) {
        log.info("/api/counsel?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());
        ConsultingListResponseDTO responseDTO = consultingService.getList(pageDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
    
    // 온라인 상담글 등록 요청
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_user')") // 사용자가 아니라면 인가처리 거부
    public ResponseEntity<?> register(
            @Validated @RequestPart("consulting") ConsultingRegisterRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo,
            @RequestPart(value="files", required = false)List<MultipartFile> multipartFiles,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        try {

            List<String> uploadedFileList = new ArrayList<>();
            multipartFiles.forEach( multipartFile -> {

                if(multipartFile != null) {
                    log.info("attached file name: {}", multipartFile.getOriginalFilename());
                    try {
                        uploadedFileList.add(consultingService.uploadFiles(multipartFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            ConsultingDetailResponseDTO responseDTO = consultingService.insert(requestDTO, tokenMemberInfo, uploadedFileList);
            return ResponseEntity.ok().body(responseDTO);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("온라인 상담 글 생성 중 에러가 발생했습니다.");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
        }
    }

    // 온라인 상담글 상세보기 요청
    @GetMapping("/content")
    public ResponseEntity<?> getContent(int consultNum) {
        log.info("/api/counsel/content/{} GET", consultNum);

        Consulting consulting = consultingService.getDetail(consultNum).orElseThrow();

        return ResponseEntity.ok().body(new ConsultingDetailResponseDTO(consulting));
    }

    // 깊은 상담 등록
    @PutMapping("/detail")
    @PreAuthorize("hasRole('ROLE_user')") // 사용자가 아니라면 인가처리 거부
    public ResponseEntity<?> registerDetailedConsulting(
            @Validated @RequestPart("detailedConsulting") DetailedConsultingRegisterRequestDTO requestDTO,
            @RequestPart(value="files", required = false) List<MultipartFile> multipartFiles,
            BindingResult result
    ) {
        log.info("/api/consulting/detail PUT!! - payload: {}", requestDTO);

        if(requestDTO == null) {
            return ResponseEntity.badRequest().body("등록 온라인 상담 정보를 전달해주세요!");
        }

        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        try {
            List<String> uploadedFileList = new ArrayList<>();
            multipartFiles.forEach( multipartFile -> {

                // 깊은 상담 첨부파일
                if(multipartFile != null) {
                    log.info("modified attached file name: {}", multipartFile.getOriginalFilename());
                    try {
                        uploadedFileList.add(consultingService.uploadFiles(multipartFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            UpdatedConsultingDetailResponseDTO responseDTO = consultingService.registerDetailedConsulting(requestDTO, uploadedFileList);
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("온라인 상담 글 생성 중 에러가 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
        }


    }

    // 입력값 검증(Validation)의 결과를 처리해주는 전역 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if(result.hasErrors()) { // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString());
            });
            return ResponseEntity
                    .badRequest()
                    .body(fieldErrors);
        }
        return null;
    }



    }