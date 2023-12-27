package com.example.demo.member.lawyer.service;


import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.lawyer.dto.request.LawyerJoinRequestDTO;
import com.example.demo.member.lawyer.dto.response.LawyerJoinResponseDTO;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.service.UserService;
import com.example.demo.token.auth.TokenMemberInfo;
import com.example.demo.token.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Value("${upload.path}")
    private String uploadRootPath;

    // 회원가입(변호사)
    public LawyerJoinResponseDTO createLawyer(
            final LawyerJoinRequestDTO dto,
            final String uploadedFilePath
    ) {

        String encoded = passwordEncoder.encode(dto.getLawyerPw());
        dto.setLawyerPw(encoded);

        Lawyer saved = lawyerRepository.save(dto.toEntity(uploadedFilePath));
        log.info("변호사 회원 가입 정상 수행됨! - saved lawyer - {}", saved);

        LawyerJoinResponseDTO responseDTO = new LawyerJoinResponseDTO(saved);
        Member member = responseDTO.insertMember(saved);
        memberRepository.save(member);

        return responseDTO;
    }
    
    // 업로드된 파일을 서버에 저장하고 저장 경로를 리턴
    public String uploadAttachedFile(MultipartFile attachedFile) throws IOException {

        File rootDir = new File(uploadRootPath);
        if(!rootDir.exists()) rootDir.mkdirs();

        String uniqueFileName
                = UUID.randomUUID() + "_" + attachedFile.getOriginalFilename();

        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        attachedFile.transferTo(uploadFile);

        return uniqueFileName;
    }

    // 법봉 환급
    public boolean setHammerCharge(int hammer, TokenMemberInfo userInfo) {
        Lawyer lawyer = lawyerRepository.findById(userInfo.getId()).orElseThrow();
        if (lawyer.getHammer() < hammer) {
            return false;
        }

        lawyer.setHammer(lawyer.getHammer() - hammer);
        lawyerRepository.save(lawyer);
        return true;
    }
}
