package com.example.demo.freeboard.controller;

import com.example.demo.freeboard.dto.FreeboardCreateDTO;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import com.example.demo.freeboard.dto.request.FreeboardUpdateRequestDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.service.FreeboardService;
import com.example.demo.token.auth.TokenMemberInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/freeboard")
@CrossOrigin // 백과 프론트를 연결시키는 아노테이션
public class FreeboardController {

    private final FreeboardService freeboardService;

    @Value("${upload.path}")
    private String uploadPath;


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
            @Validated @RequestBody FreeboardCreateDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo userInfo,
            MultipartFile[] multipartFiles,
            BindingResult result
    ) {

        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        try {

            FreeboardCreateRequestDTO fileCreate = new ObjectMapper().readValue(requestDTO.getRoute(), FreeboardCreateRequestDTO.class);
            log.info("fileCreate: {}", fileCreate);

            for(int j=0; j < multipartFiles.length; j++) {
                    MultipartFile file = multipartFiles[j];

                    String fileId = (new Date().getTime()) + "" + (new Random().ints(1000, 9999).findAny().getAsInt());
                    String originName = file.getOriginalFilename();
                    String fileExtension = originName.substring(originName.lastIndexOf(".") +  1);
                    originName = originName.substring(0, originName.lastIndexOf("."));
                    long fileSize = file.getSize();
                File fileSave = new File(uploadPath, fileId + "." + fileExtension);
                if(!fileSave.exists()) {
                    fileSave.mkdirs();
                }

                file.transferTo(fileSave);

                log.info("\n\n\n");
                log.info("fileId= {}", fileId);
                log.info("originName= {}", originName);
                log.info("fileExtentsion= {}", fileExtension);
                log.info("fileSize= {}", fileSize);
                log.info("\n\n\n");

            }

            freeboardService.create(requestDTO, userInfo); // userInfo 값 넣기
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 생성 중 에러가 발생했습니다.");
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok().body("/api/freeboard"); // 게시글 목록 페이지 이동
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
            return ResponseEntity.badRequest().body("게시물이 존재하지 않습니다!");
        }
    }

    // 게시글 상세 글 요청
    @GetMapping("/{bno}")
    public ResponseEntity<?> detailedFreeboard(@PathVariable int bno ) {
        log.info("/api/freeboard/{} GET", bno);

        Optional<Freeboard> dto = freeboardService.getDetail(bno);
        return ResponseEntity.ok().body(dto);
    }


    // 게시글 수정 요청
    @PutMapping("/update")
    public ResponseEntity<?> updateFreeboard(
            @AuthenticationPrincipal TokenMemberInfo userInfo,
            @Validated @RequestBody FreeboardUpdateRequestDTO requestDTO,
            BindingResult result
    ) {
        log.info("/api/freeboard/update update 내용: {}", requestDTO);
        ResponseEntity<List<FieldError>> filedErrors = getValidatedResult(result);
        if (filedErrors != null) return filedErrors;

        FreeListResponseDTO responseDTO = freeboardService.modify(requestDTO, userInfo.getId());

        return ResponseEntity.ok().body(responseDTO);
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
