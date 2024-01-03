package com.example.demo.reply.controller;

import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.reply.dto.request.ReplyCreateRequestDTO;
import com.example.demo.reply.dto.response.ReplyDetailResponseDTO;
import com.example.demo.reply.dto.response.ReplyListResponseDTO;
import com.example.demo.reply.entity.Reply;
import com.example.demo.reply.service.ReplyService;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/reply")
@CrossOrigin // 백과 프론트를 연결시키는 아노테이션
public class ReplyController {

    private final ReplyService replyService;


    // 댓글 목록 불러오기
    @GetMapping("/list")
    public ResponseEntity<?> getList(PageDTO pageDTO , @RequestParam int bno,
                                     @AuthenticationPrincipal TokenMemberInfo userInfo) {
        log.info("/api/reply?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());
        ReplyListResponseDTO responseDTO = replyService.getList(pageDTO, bno, userInfo);
        return ResponseEntity.ok().body(responseDTO);
    }


    // 댓글 등록 요청
    @PostMapping("/register")
    public ResponseEntity<?> writeReply(@Validated @RequestBody ReplyCreateRequestDTO requestDTO,
                                        @AuthenticationPrincipal TokenMemberInfo userInfo,
                                        BindingResult result
    ){
        try {
                if(result.hasErrors()) {
                    log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
                    return ResponseEntity
                            .badRequest()
                            .body(result.getFieldError());
                }
                    ReplyDetailResponseDTO responseDTO = replyService.insert(requestDTO, userInfo);

                    return ResponseEntity.ok().body(responseDTO);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("댓글 생성 중 에러가 발생했습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
        }
    }

    // 댓글 삭제
    @DeleteMapping
    public ResponseEntity<?> replyDelete(int rno,
                                              @AuthenticationPrincipal TokenMemberInfo userInfo) {

        if(!replyService.userTrue(userInfo, rno)) {
            return ResponseEntity.ok().body("이 글에 권한이 없습니다.");
        }
        try{
            replyService.delete(rno);
            return ResponseEntity.ok().body("댓글 삭제가 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }

    }






}
