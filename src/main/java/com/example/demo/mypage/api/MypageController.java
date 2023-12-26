package com.example.demo.mypage.api;

import com.example.demo.member.user.service.UserService;
import com.example.demo.mypage.dto.request.UserUpdateRequestDTO;
import com.example.demo.mypage.dto.response.LawyerDetailResponseDTO;
import com.example.demo.mypage.dto.response.UserDetailResponseDTO;
import com.example.demo.mypage.service.MypageService;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@CrossOrigin
public class MypageController {

    private final MypageService mypageService;
    private final UserService userService;

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
            UserUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
    ) {
        if(!mypageService.validateUser(requestDTO, tokenMemberInfo)) {
            return ResponseEntity.badRequest().body("마이페이지 회원 정보 수정의 권한이 없습니다.");
        }

        if(userService.isDuplicateNickname(requestDTO.getNickname())) {
            return ResponseEntity.badRequest().body("닉네임이 중복되었습니다.");
        }

        UserDetailResponseDTO responseDTO = mypageService.updateUser(requestDTO, tokenMemberInfo);

        return ResponseEntity.ok().body(responseDTO);
    }
    
    // 변호사 정보 수정 요청
//    @PutMapping("/update/lawyer")
//    @PreAuthorize("hasRole('ROLE_lawyer')")
//    public ResponseEntity<?> updateLawyerInfo(
//        LawyerUpdateRequestDTO requestDTO,
//        @AuthenticationPrincipal TokenMemberInfo tokenMemberInfo
//    ) {
//        if(!mypageService.validateLawyer(requestDTO, tokenMemberInfo)) {
//            return ResponseEntity.badRequest().body("마이페이지 회원 정보 수정의 권한이 없습니다.");
//        }
//
//        LawyerDetailResponseDTO responseDTO = mypageService.updateLawyer(requestDTO, tokenMemberInfo);
//
//        return ResponseEntity.ok().body(responseDTO);
//    }
    
}
