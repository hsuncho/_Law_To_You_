package com.example.demo.member.util.controller;

import com.example.demo.member.util.DTO.EmailCheckDTO;
import com.example.demo.member.util.DTO.EmailRequestDTO;
import com.example.demo.member.util.service.MailSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class MailController {
    private final MailSendService mailSendService;

    @PostMapping("/mailSend")
    private String mailSend(@RequestBody @Valid EmailRequestDTO emailDTO) {
        log.info("이메일 인증 요청이 들어옴");
        log.info("이메일 인증 이메일 : {}", emailDTO.getEmail());
        return mailSendService.joinEmail(emailDTO.getEmail());
    }

    @PostMapping("/mailauthCheck")
    public ResponseEntity<?> AuthCheck(@RequestBody @Valid EmailCheckDTO emailCheckDTO) {
        Boolean Checked = mailSendService.CheckAuthNum(emailCheckDTO.getEmail(), emailCheckDTO.getAuthNum());
        if(Checked) {
            return ResponseEntity.ok().body("ok");
        } else {
            return ResponseEntity.badRequest().body("기타 예외가 발생했습니다.");
        }
    }
}
