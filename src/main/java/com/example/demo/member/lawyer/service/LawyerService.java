package com.example.demo.member.lawyer.service;


import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.lawyer.dto.request.LawyerJoinRequestDTO;
import com.example.demo.member.lawyer.dto.response.LawyerJoinResponseDTO;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.master.dto.response.lawyerListResponseDTO;
import com.example.demo.token.auth.TokenMemberInfo;
import com.example.demo.token.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;


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

    public List<lawyerListResponseDTO> getlawyerList() {

        List<Lawyer> lawyer = lawyerRepository.findAll();
        List<lawyerListResponseDTO> lawyerListResponseDTOList = new ArrayList<>();
        for (Lawyer law : lawyer) {
            lawyerListResponseDTOList.add(new lawyerListResponseDTO(law));
        };

        log.info("lawyerList: {}", lawyerListResponseDTOList);

        return lawyerListResponseDTOList;
    }

    // 변호사 자격증 url 불러오기
    public String getLawyerImg(String lawyerId) {

        String lawyerImgUrl = lawyerRepository.findById(lawyerId).orElseThrow().getAttachedFile();
//        String fileUrlName = lawyerImgUrl.substring(lawyerImgUrl.lastIndexOf("/") + 1);
        String encodedFileName = URLDecoder.decode(lawyerImgUrl, StandardCharsets.UTF_8);

        return encodedFileName;
    }
}
