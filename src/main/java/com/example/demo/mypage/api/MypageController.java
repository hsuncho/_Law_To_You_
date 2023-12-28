package com.example.demo.mypage.api;

import com.example.demo.member.lawyer.service.LawyerService;
import com.example.demo.member.user.entity.User;
import com.example.demo.answer.dto.response.DetailedResponseDTO;
import com.example.demo.answer.entity.Answer;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.repository.ConsultingRepository;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.member.user.service.UserService;
import com.example.demo.mypage.dto.request.LawyerUpdateRequestDTO;
import com.example.demo.mypage.dto.request.UserUpdateRequestDTO;
import com.example.demo.mypage.dto.response.*;
import com.example.demo.mypage.service.MypageService;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.consulting.entity.QConsulting.consulting;


@RestController
@Slf4j
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@CrossOrigin
public class MypageController {

    private final ConsultingRepository consultingRepository;
    private final MypageService mypageService;
    private final UserService userService;
    private final LawyerService lawyerService;

    // 사용자 정보 조회
    @GetMapping
    @PreAuthorize("hasRole('ROLE_user')")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal TokenMemberInfo tokenMemberInfo) {

        UserDetailResponseDTO responseDTO = mypageService.getUserInfo(tokenMemberInfo);

        return ResponseEntity.ok().body(responseDTO);
    }

    // 변호사 정보 조회
    @GetMapping("/lawyer")
    @PreAuthorize("hasRole('ROLE_lawyer')")
    public ResponseEntity<?> getLawyerInfo(@AuthenticationPrincipal TokenMemberInfo tokenMemberInfo) {

        LawyerDetailResponseDTO responseDTO = mypageService.getLawyerInfo(tokenMemberInfo);

        return ResponseEntity.ok().body(responseDTO);
    }

    //  사용자 정보 수정 요청
    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_user')")
    public ResponseEntity<?> updateInfo(
            @RequestBody UserUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo,
            BindingResult result
    ) {
        log.info("\n\n\n /api/mypage/update - PUT! - {}", requestDTO);

        if(!requestDTO.getId().equals(tokenMemberInfo.getId())) {
            return ResponseEntity.badRequest().body("회원 정보 수정 권한이 없습니다.");
        }

        if(userService.isDuplicateNickname(requestDTO.getNickname())) {
            return ResponseEntity.badRequest().body("닉네임이 중복되었습니다.");
        }

        if(result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        } try {
            UserDetailResponseDTO responseDTO = mypageService.updateUser(requestDTO, tokenMemberInfo);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

     // 변호사 정보 수정 요청
    @PutMapping("/update/lawyer")
    @PreAuthorize("hasRole('ROLE_lawyer')")
    public ResponseEntity<?> updateLawyerInfo(
        @RequestBody LawyerUpdateRequestDTO requestDTO,
        @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo,
        BindingResult result
    ) {
        log.info("/api/mypage/update/lawyer PUT! - {}", requestDTO);

        if(!requestDTO.getId().equals(tokenMemberInfo.getId())) {
            return ResponseEntity.badRequest().body("회원 정보 수정 권한이 없습니다.");
        }

        if(result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        } try {
            LawyerDetailResponseDTO responseDTO = mypageService.updateLawyer(requestDTO, tokenMemberInfo);

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<?> deleteMember(
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo) {
       try {
            String result = mypageService.deleteMember(tokenMemberInfo);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 자유게시판 글 목록
    @GetMapping("/api/mypage/list")
    public ResponseEntity<?> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
    ) {
        FreeListResponseDTO responseDTO = mypageService.getList(new PageDTO(page, size), tokenMemberInfo);
        return ResponseEntity.ok().body(responseDTO);
    }
    
    // 법봉 충전
    @PutMapping("/charge")
    @PreAuthorize("hasRole('ROLE_user')")
    public ResponseEntity<?> getHammerCharge(@RequestParam int hammer,
                                          @AuthenticationPrincipal TokenMemberInfo userInfo) {
        userService.getHammerCharge(hammer, userInfo);

        return ResponseEntity.ok().body("법봉 "+ hammer + "개가 정상 충전되었습니다.");

    }

    // 법봉 환급
    @PutMapping("/refund")
    @PreAuthorize("hasRole('ROLE_lawyer')")
    public ResponseEntity<?> setHammerCharge(@RequestParam int hammer,
                                             @AuthenticationPrincipal TokenMemberInfo userInfo) {

        if(lawyerService.setHammerCharge(hammer, userInfo)) { // true라면
        return ResponseEntity.ok().body("법봉 " + hammer + "개가 정상 환급 처리 되었습니다.");
        };
        return ResponseEntity.badRequest().body("법봉의 개수가 부족합니다.");
    }

    // 법봉 조회
    @GetMapping("/hammer")
    public ResponseEntity<?> hammerCnt(@AuthenticationPrincipal TokenMemberInfo userInfo) {

        int hammerCnt = userService.hammerCnt(userInfo);

        return ResponseEntity.ok().body("법봉의 개수는 " + hammerCnt + "개 입니다.");
    }

    // 사용자가 등록한 온라인 상담 내역 목록
    @GetMapping("/counsel")
    @PreAuthorize("hasRole('ROLE_user')")
    public ResponseEntity<?> getConsultingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
    ) {
        UserConsultingListResponseDTO responseDTO = mypageService.getConsultingList(new PageDTO(page, size), tokenMemberInfo);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 온라인 상담글 삭제(사용자) -> 짧은 답변이 달리기 전에만 가능
    @DeleteMapping("/counsel")
    @PreAuthorize("hasRole('ROLE_user')")
    public ResponseEntity<?> deleteConsulting(
            int consultNum,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
    ) {
        if(!mypageService.validateForDelete(consultNum, tokenMemberInfo)) {
            return ResponseEntity.badRequest().body("삭제 권한이 없습니다.");
        }
        if(!mypageService.deleteConsulting(consultNum, tokenMemberInfo)) {
            return ResponseEntity.badRequest().body("이미 답변이 달린 상담 내역은 삭제하실 수 없습니다.");
        }

        UserConsultingListResponseDTO responseDTO
                = mypageService.getConsultingList(new PageDTO(1, 10), tokenMemberInfo);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 변호사가 답변을 등록한 온라인 상담 내역 목록
    @GetMapping("/counsel/lawyer")
    @PreAuthorize("hasRole('ROLE_lawyer')")
    public ResponseEntity<?> getLawyerConsulting(
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
    ) {
        LawyerConsultingListResponseDTO responseDTO = mypageService.getLawyerConsulting(tokenMemberInfo);

        return ResponseEntity.ok().body(responseDTO);
        }

    // 깊은 상담 글 상세보기
    @GetMapping("/counsel/detail")
    public ResponseEntity<?> getDetailedConsulting(
            int consultNum,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
    ) {
        log.info("/api/mypage/counsel/detail - consultNum: {}\ntokenMemberInfo: {}", consultNum, tokenMemberInfo);

        Consulting consulting = consultingRepository.findById(consultNum).orElseThrow();

        if(!mypageService.validateDetailed(tokenMemberInfo, consultNum)) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }

        // 깊은 상담 등록 페이지로 이동하기 위해 온라인 상담 제목, 내용, 첨부파일, 게시일 반환

        List<Answer> answerList = consulting.getAnswerList();

        if(answerList.isEmpty()) {
            return ResponseEntity.badRequest().body("작성된 답변이 없어 깊은 상담글 등록이 불가능합니다.");
        }

        List<Integer> adoptList = answerList.stream().map(Answer::getAdopt).collect(Collectors.toList());

        if(!adoptList.contains(1)) {
            return ResponseEntity.badRequest().body("답변을 채택하셔야 깊은 상담글 등록이 가능합니다.");
        }
            MyPageDetailResponseDTO responseDTO = mypageService.getDetailedConsulting(consultNum);

        return ResponseEntity.ok().body(responseDTO);

    }







}
    







