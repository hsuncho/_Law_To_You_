package com.example.demo.member.user.api;

import com.example.demo.member.user.dto.request.LoginRequestDTO;
import com.example.demo.member.user.dto.request.UserJoinRequestDTO;
import com.example.demo.member.user.dto.response.LoginResponseDTO;
import com.example.demo.member.user.dto.response.UserJoinResponseDTO;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.member.user.service.UserService;
import com.example.demo.token.auth.TokenMemberInfo;
import com.example.demo.token.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

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

        String id = dto.getId();
        String email = dto.getEmail();
        String nickname = dto.getNickname();

        if(userService.isDuplicateId(id)) {
            log.warn("아이디가 중복되었습니다. - {}", id);
            return ResponseEntity.badRequest().body("아이디가 중복되었습니다.");
        }

        if(userService.isDuplicateEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
            return ResponseEntity.badRequest().body("이메일이 중복되었습니다.");
        }

        if(userService.isDuplicateNickname(nickname)) {
            log.warn("닉네임이 중복되었습니다. - {}", nickname);
            return ResponseEntity.badRequest().body("닉네임이 중복되었습니다.");
        }

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
            HttpServletResponse response,
           @RequestBody LoginRequestDTO dto
    ) {
        try{
            log.info("dto: {}", dto);
            LoginResponseDTO responseDTO
                    = userService.authenticate(response, dto);

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

    private final UserRepository userRepository;

     //카카오 토큰 갱신 API
    @GetMapping("/kakao")
    public ResponseEntity<?> updateKakaoToken(String code) {
        Map<String, Object> responseData = userService.updateKakaoToken(code);
        return ResponseEntity.ok().body(responseData);
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            @AuthenticationPrincipal TokenMemberInfo memberInfo
    ) {
        // 엑세스 만료 후 로그아웃
        log.info("/api/user/logout - GET! - member: {}", memberInfo.getId());

        String result = userService.logout(request, memberInfo);

        return ResponseEntity.ok().body(result);
    }


}






















