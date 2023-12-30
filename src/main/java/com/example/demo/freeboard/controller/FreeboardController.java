package com.example.demo.freeboard.controller;

import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import com.example.demo.freeboard.dto.request.FreeboardUpdateRequestDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardCreateResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetaileResponseCountDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.service.FreeboardService;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/freeboard")
@CrossOrigin // 백과 프론트를 연결시키는 아노테이션
public class FreeboardController {

    private final FreeboardService freeboardService;

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
            @Validated @RequestPart(value = "freeboard") FreeboardCreateRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo userInfo,
            @RequestPart(value = "attachedFile", required = false) List<MultipartFile> multipartFiles,
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
        int count = freeboardService.searchCNT(search, flag);
        List<FreeboardDetailResponseDTO> detailResponseDTO = new ArrayList<>();
        for (Freeboard free : freeboards) {
            detailResponseDTO.add(new FreeboardDetailResponseDTO( "검색시 요청", free));
        }

        if (!freeboards.isEmpty()) {
            log.info("게시물 개수: {}", freeboards.size());
            return ResponseEntity.ok().body(new FreeboardDetaileResponseCountDTO(count, detailResponseDTO));
        } else {
            return ResponseEntity.ok().body("검색 결과가 없습니다!");
        }
    }

    // 게시글 상세 글 요청
    @GetMapping("/content")
    public ResponseEntity<?> detailedFreeboard(int bno,
                                               @AuthenticationPrincipal TokenMemberInfo MemberInfo) {
        log.info("/api/freeboard/{} GET", bno);
            Freeboard dto = freeboardService.getDetail(bno).orElseThrow();
        if(freeboardService.userTrue(MemberInfo, bno)) { // 게시글 작성자가 맞다면 1 아니면 0 반환

        return ResponseEntity.ok().body(new FreeboardDetailResponseDTO(1, dto));
        }
        return ResponseEntity.ok().body(new FreeboardDetailResponseDTO(0, dto));
    }



    // 게시글 수정 요청
    @PutMapping("/update")
    public ResponseEntity<?> updateFreeboard(
            @Validated @RequestPart(value = "freeboard") FreeboardUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo MemberInfo,
            @RequestPart(value = "attchedFile", required = false) List<MultipartFile> multipartFiles,
            BindingResult result
    ) {

        if(!freeboardService.userTrue(MemberInfo, requestDTO.getBno())) {
            return ResponseEntity.ok().body("이 글에 권한이 없습니다.");
        }

            log.info("/api/freeboard/update update 내용: {}", requestDTO);
            ResponseEntity<List<FieldError>> filedErrors = getValidatedResult(result);
            if (filedErrors != null) return filedErrors;

            try {
                List<String> uploadedFileList = new ArrayList<>();
                if (!multipartFiles.isEmpty()) {
                    multipartFiles.forEach(multipartFile -> {
                        if (multipartFile != null && !multipartFile.isEmpty()) {
                            log.info("attached file name: {}", multipartFile.getOriginalFilename());
                            try {
                                uploadedFileList.add(freeboardService.uploadFiles(multipartFile));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                FreeboardDetailResponseDTO responseDTO = freeboardService.modify(requestDTO, uploadedFileList);
                return ResponseEntity.ok().body(responseDTO);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
            }


    }

    // 게시글 삭제 요청
    @DeleteMapping("/delete")
    public ResponseEntity<?> freeboardDelete(int bno,
                                             @AuthenticationPrincipal TokenMemberInfo userInfo) {
        if(!freeboardService.userTrue(userInfo, bno)) {
            return ResponseEntity.ok().body("이 글에 권한이 없습니다.");
        }
            try {
                freeboardService.delete(bno);
                return ResponseEntity.ok().body("게시글 삭제가 완료되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError()
                        .body(e.getMessage());
            }
    }

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
