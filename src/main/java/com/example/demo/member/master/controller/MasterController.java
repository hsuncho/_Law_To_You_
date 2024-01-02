package com.example.demo.member.master.controller;

import com.example.demo.freeboard.service.S3Service;
import com.example.demo.member.lawyer.service.LawyerService;
import com.example.demo.member.master.dto.response.ListResponseDTO;
import com.example.demo.member.master.dto.response.lawyerListResponseDTO;
import com.example.demo.member.master.service.MasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/master")
@CrossOrigin
public class MasterController {

    private final LawyerService lawyerService;

    // 변호사 회원가입 요청 내역
    @GetMapping("/history")
//    @PreAuthorize("hasRole('ROLE_master')")
    public ResponseEntity<?> lawyerList(@RequestParam String authority) {

        // 관리자인지 검증
        if(!authority.equals("master")) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }

        List<lawyerListResponseDTO> listDTO = lawyerService.getlawyerList();
                log.info("listDTO: {}", listDTO);
                log.info("listDTO.size: {}",listDTO.size());
                return ResponseEntity.ok().body(new ListResponseDTO(listDTO.size(), listDTO));
    }

    // 변호사 등록번호 클릭시 이미지 상세보기
    @GetMapping("/history/img")
//    @PreAuthorize("hasRole('ROLE_master')")
    public ResponseEntity<?> lawyerURL(@RequestParam String lawyerId) {
            String lawyerImgUrl = lawyerService.getLawyerImg(lawyerId);

            log.info("lawyerImg: {}", lawyerImgUrl);

        return ResponseEntity.ok().body(lawyerImgUrl);
    }

    private final MasterService masterService;

    @PutMapping("/history")
    public ResponseEntity<?> approveLawyer(
            @RequestParam String authority,
            @RequestParam String lawyerId
    ) {

        // 관리자인지 검증
        if(!authority.equals("master")) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }
        masterService.approval(lawyerId);

        return ResponseEntity.ok().body("승인 되었습니다.");
    }
}
