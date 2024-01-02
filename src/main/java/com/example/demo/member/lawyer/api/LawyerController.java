package com.example.demo.member.lawyer.api;

import com.example.demo.freeboard.service.S3Service;
import com.example.demo.member.lawyer.dto.response.LawyerJoinResponseDTO;
import com.example.demo.member.lawyer.service.LawyerService;
import com.example.demo.member.lawyer.dto.request.LawyerJoinRequestDTO;
import com.example.demo.member.user.service.UserService;
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
    private final UserService userService;
    private final S3Service s3Service;

    // 아이디와 이메일 중복 확인 요청 처리는 /api/user로 진행됨

    @GetMapping("/test")
    public String test() {
        return "test 작동된단다";
    }

    // 회원가입 요청
    @PostMapping("/join")
    public ResponseEntity<?> joinLawyer(
            @Validated @RequestPart(value = "lawyer") LawyerJoinRequestDTO dto,
            @RequestPart(value = "attachedFile") MultipartFile attachedFile,
            BindingResult result
    ) {
        log.info("/api/lawyer POST! - {}", dto);

        String id = dto.getLawyerId();
        String email = dto.getEmail();

        if(id.trim().isEmpty()) return ResponseEntity.badRequest().body("아이디는 필수값입니다!");
        if(email.trim().isEmpty()) return ResponseEntity.badRequest().body("이메일은 필수값입니다!");

        if(userService.isDuplicateId(id)) {
            log.warn("아이디가 중복되었습니다. - {}", id);
            return ResponseEntity.badRequest().body("아이디가 중복되었습니다.");
        }

        if(userService.isDuplicateEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
            return ResponseEntity.badRequest().body("이메일이 중복되었습니다.");

        }

        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }

            try {
                String uploadedFilePath = null;
                if (attachedFile != null) {
                    log.info("attached file name: {}", attachedFile.getOriginalFilename());

                    uploadedFilePath = s3Service.uploadFiles(attachedFile);
                }

                LawyerJoinResponseDTO responseDTO = lawyerService.createLawyer(dto, uploadedFilePath);
                return ResponseEntity.ok().body(responseDTO);

            } catch (RuntimeException e) {
                log.warn("중복된 값이 있습니다!");
                return ResponseEntity.badRequest().body(e.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
                log.warn("기타 예외가 발생했습니다!");
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
