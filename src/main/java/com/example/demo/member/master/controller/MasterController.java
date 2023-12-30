package com.example.demo.member.master.controller;

import com.example.demo.member.master.dto.response.ApproveLawyerResponseDTO;
import com.example.demo.member.master.service.MasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/master")
@CrossOrigin
public class MasterController {

    private final MasterService masterService;

    @GetMapping("/history")
    public ResponseEntity<?> approveLawyer(
            @RequestParam String authority,
            @RequestParam String lawyerId
    ) {

        // 관리자인지 검증
        if(!authority.equals("master")) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }
        
        ApproveLawyerResponseDTO responseDTO = masterService.approve(lawyerId);

        return ResponseEntity.ok().body(responseDTO);

    }


}
