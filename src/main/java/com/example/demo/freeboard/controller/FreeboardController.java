package com.example.demo.freeboard.controller;

import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import com.example.demo.freeboard.dto.request.FreeboardUpdateRequestDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardCreateResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import com.example.demo.freeboard.repository.FreeboardFileRepository;
import com.example.demo.freeboard.service.FreeboardService;
import com.example.demo.freeboard.service.S3Service;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/freeboard")
@CrossOrigin // 백과 프론트를 연결시키는 아노테이션
public class FreeboardController {

    private final FreeboardService freeboardService;
    private final S3Service s3Service;

    // 자유게시판 클릭시 글번호, 제목, 작성자, 작성일자, 리스트 요청
    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/freeboard?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        FreeListResponseDTO dto = freeboardService.getFreeBoards(pageDTO);

        return ResponseEntity.ok().body(dto);
    }

    // 게시글 등록 요청
    @PostMapping("/register")
    public ResponseEntity<?> writeFreeboard(
            @Validated @RequestPart FreeboardCreateRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo userInfo,
            @RequestPart(value = "attchedFile", required = false) List<MultipartFile> multipartFiles,
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
            multipartFiles.forEach(multipartFile -> {
                if(multipartFile != null) {
                    log.info("attached file name: {}", multipartFile.getOriginalFilename());
                    try {
                        uploadedFileList.add(freeboardService.uploadFiles(multipartFile));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            FreeboardCreateResponseDTO responseDTO = freeboardService.create(requestDTO, userInfo, uploadedFileList);
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 생성 중 에러가 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
        }
    }

    // 게시글 검색 요청
    @GetMapping("/search")
    public ResponseEntity<?> searchFreeboard(@RequestParam String search, @RequestParam String type) {
        boolean flag = true;
        log.info("/api/freeboard/search 내용 select옵션: {},  검색: {}", type, search);

        if (type.equals("writer")) {
            flag = true;

        } else if (type.equals("titleAndContent")) {
            flag = false;
        }

        List<Freeboard> freeboards = freeboardService.search(search, flag);
        List<FreeboardDetailResponseDTO> detailResponseDTO = new ArrayList<>();
        int rowNum = 1;
        for (Freeboard free : freeboards) {
            detailResponseDTO.add(new FreeboardDetailResponseDTO(rowNum, free));
            rowNum++;
        }

        if (!freeboards.isEmpty()) {
            log.info("게시물 개수: {}", freeboards.size());
            return ResponseEntity.ok().body(detailResponseDTO);
        } else {
            return ResponseEntity.ok().body("검색 결과가 없습니다!");
        }
    }

    // 게시글 상세 글 요청
    @GetMapping("/{bno}")
    public ResponseEntity<?> detailedFreeboard(@PathVariable int bno ) {
        log.info("/api/freeboard/{} GET", bno);

        Optional<Freeboard> dto = freeboardService.getDetail(bno);
        return ResponseEntity.ok().body(dto);
    }


    /*
    // 게시글 수정 요청
    @PutMapping("/update")
    public ResponseEntity<?> updateFreeboard(
            @Validated @RequestBody FreeboardUpdateRequestDTO requestDTO,
            @RequestPart(value = "attchedFile", required = false) List<MultipartFile> multipartFiles,
            BindingResult result
    ) {
        log.info("/api/freeboard/update update 내용: {}", requestDTO);
        ResponseEntity<List<FieldError>> filedErrors = getValidatedResult(result);
        if (filedErrors != null) return filedErrors;

        freeboardService.modify(requestDTO, requestDTO.getUserId());
        Freeboard freeboard = freeboardService.getFreeBoard();
        try {
            List<String> uploadedFileList = new ArrayList<>();
            multipartFiles.forEach(multipartFile -> {
                if(multipartFile != null) {
                    log.info("attached file name: {}", multipartFile.getOriginalFilename());
                    try {
                        uploadedFileList.add((String) multipartFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            s3Service.fileDelete((String) multipartFile);
            FreeboardCreateResponseDTO responseDTO = freeboardService.create(requestDTO, userInfo, uploadedFileList);
            return ResponseEntity.ok().body(responseDTO);
        }

        return ResponseEntity.ok().body(responseDTO);
    }
    */



    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if (result.hasErrors()) { // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString());
            });
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return null;
    }

}
