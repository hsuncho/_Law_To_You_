package com.example.demo.member.master.controller;

import com.example.demo.freeboard.service.S3Service;
import com.example.demo.member.lawyer.service.LawyerService;
import com.example.demo.member.master.dto.response.ListResponseDTO;
import com.example.demo.member.master.dto.response.lawyerListResponseDTO;
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
}
