package com.example.demo.member.user.api;

import com.example.demo.member.user.dto.request.LoginRequestDTO;
import com.example.demo.member.user.dto.request.UserJoinRequestDTO;
import com.example.demo.member.user.dto.response.LoginResponseDTO;
import com.example.demo.member.user.dto.response.UserJoinResponseDTO;
import com.example.demo.member.user.service.UserService;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String test() {
        return "test 작동된단다";
    }


    // 아이디 중복 확인 요청 처리
    @GetMapping("/checkId")
    public ResponseEntity<?> check(String id) {
        if(id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("작성된 아이디가 없습니다!");
        }


       boolean resultFlag = userService.isDuplicateId(id);
        log.info("{} 중복 ? - {}", id, resultFlag);

        return ResponseEntity.ok().body(resultFlag);
    }
    
    // 이메일 중복 확인 요청 처리(레거시 더 보기)
    @GetMapping("/email")
    public ResponseEntity<?> checkEmail(String email) {
        if(email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("작성된 이메일이 없습니다!");
        }

        boolean resultFlag = userService.isDuplicateEmail(email);
        log.info("{} 중복 ? - {}", email, resultFlag);

        return ResponseEntity.ok().body(resultFlag);
    }

    @GetMapping("/checkName")
    public ResponseEntity<?> checkName(String nickname) {
        if(nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("작성된 닉네임이 없습니다!");
        }

        boolean resultFlag = userService.isDuplicateNickname(nickname);
        log.info("{} 중복 ? - {}", nickname, resultFlag);

        return ResponseEntity.ok().body(resultFlag);
    }

    // 회원 가입 요청 처리(사용자)
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(
            @RequestBody UserJoinRequestDTO dto,
            BindingResult result
    ) {
        log.info("/api/auth POST! - {}", dto);
        if(result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }
        try {
            UserJoinResponseDTO responseDTO = userService.createUser(dto);
            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("중복된 값이 있습니다!");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
           @RequestBody LoginRequestDTO dto
    ) {
        try{
            log.info("dto: {}", dto);
            LoginResponseDTO responseDTO
                    = userService.authenticate(dto);

            return  ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 카카오 로그인
    @GetMapping("/kakaoLogin")
    public ResponseEntity<?> kakaoLogin(String code) {
        log.info("/api/user/kakaoLogin - GET! -code: {}", code);
        LoginResponseDTO responseDTO = userService.kakaoService(code);

        return ResponseEntity.ok().body(responseDTO);
    }


    // 로그아웃 처리
    @GetMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal TokenMemberInfo memberInfo
    ) {
        // 엑세스 만료 후 로그아웃
        log.info("/api/user/logout - GET! - member: {}", memberInfo.getId());

        String result = userService.logout(memberInfo);

        return ResponseEntity.ok().body(result);
    }

    // refreshToken 유효성 검사 및 재발급








}






















