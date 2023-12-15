package com.example.demo.member.lawyer.api;

import com.example.demo.member.lawyer.dto.response.LawyerJoinResponseDTO;
import com.example.demo.member.lawyer.service.LawyerService;
import com.example.demo.member.lawyer.dto.request.LawyerJoinRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/lawyer")
@CrossOrigin
public class LawyerController {

    private final LawyerService lawyerService;

    // 아이디와 이메일 중복 확인 요청 처리는 /api/user로 진행됨

    @GetMapping("/test")
    public String test() {
        return "test 작동된단다";
    }

    // 회원가입 요청
    @PostMapping("/join")
    public ResponseEntity<?> joinLawyer(
            @Validated @RequestPart("lawyer") LawyerJoinRequestDTO dto,
            @RequestPart(value="attachedFile") MultipartFile attachedFile,
            BindingResult result
    ) {
        log.info("/api/lawyer POST! - {}", dto);

        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }

            try {
                String uploadedFilePath = null;
                if (attachedFile != null) {
                    log.info("attached file name: {}", attachedFile.getOriginalFilename());

                    // 전달 받은 프로필 이미지를 먼저 지정된 경로에 저장한 후 DB 저장을 위해 경로를 받아오자
                    uploadedFilePath = lawyerService.uploadAttachedFile(attachedFile);
                }

                LawyerJoinResponseDTO responseDTO = lawyerService.createLawyer(dto, uploadedFilePath);
                return ResponseEntity.ok().body(responseDTO);

            } catch (RuntimeException e) {
                log.warn("중복된 값이 있습니다!");
                return ResponseEntity.badRequest().body(e.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
                log.warn("기타 예외가 발생했습니다!");
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }

    }





































    private MediaType findExtensionAndGetMediaType(String filePath) {

        // 파일 경로에서 확장자 추출하기
        String ext
                = filePath.substring(filePath.lastIndexOf(".") + 1);

        // 추출한 확장자를 바탕으로 MediaType을 설정. -> Header에 들어갈 Content-type이 됨.
        switch (ext.toUpperCase()) {
            case "JPG": case "JPEG":
                return MediaType.IMAGE_JPEG;
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "GIF":
                return MediaType.IMAGE_GIF;
            default:
                return null;
        }
    }
}
