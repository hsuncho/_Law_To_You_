package com.example.demo.member.master.controller;

import com.example.demo.freeboard.service.S3Service;
import com.example.demo.member.lawyer.service.LawyerService;
import com.example.demo.member.master.dto.response.ApproveLawyerResponseDTO;
import com.example.demo.member.master.dto.response.ListResponseDTO;
import com.example.demo.member.master.dto.response.lawyerListResponseDTO;
import com.example.demo.member.master.service.MasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/master")
@CrossOrigin
public class MasterController {

    private final LawyerService lawyerService;
    private final S3Service s3Service;

    // 변호사 회원가입 요청 내역
    @GetMapping("/history")
//    @PreAuthorize("hasRole('ROLE_master')")
    public ResponseEntity<?> lawyerList(@RequestParam String authority) {

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

            URL lawyerImg = s3Service.getURL(lawyerImgUrl);
            log.info("lawyerImg: {}", lawyerImg);

        return new ResponseEntity<>(lawyerImg, HttpStatus.OK);
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

        ApproveLawyerResponseDTO responseDTO = masterService.approve(lawyerId);

        return ResponseEntity.ok().body(responseDTO);

    }

}
