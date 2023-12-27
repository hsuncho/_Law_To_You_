package com.example.demo.mypage.service;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.mypage.dto.request.UserUpdateRequestDTO;
import com.example.demo.mypage.dto.response.LawyerDetailResponseDTO;
import com.example.demo.mypage.dto.response.UserDetailResponseDTO;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MypageService {

    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailResponseDTO getUserInfo(TokenMemberInfo tokenMemberInfo) {

        User user = userRepository.findById(tokenMemberInfo.getId()).orElseThrow();

        return new UserDetailResponseDTO(user);
    }

    public LawyerDetailResponseDTO getLawyerInfo(TokenMemberInfo tokenMemberInfo
    ) {

        Lawyer lawyer = lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow();

        return new LawyerDetailResponseDTO(lawyer);
    }

    public boolean validateUser(final UserUpdateRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo) {

        if(requestDTO.getId().equals(tokenMemberInfo.getId())) return true;
        else if(requestDTO.getNickname().isBlank()) return false;
        return false;
    }

    public boolean validateLawyer(TokenMemberInfo tokenMemberInfo) {

        return false;
    }

    public UserDetailResponseDTO updateUser(final UserUpdateRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo) {

        String encoded = passwordEncoder.encode(requestDTO.getPassword());
        requestDTO.setPassword(encoded);

        User saved = userRepository.findById(tokenMemberInfo.getId()).orElseThrow();
        return  new UserDetailResponseDTO(saved);

    }

    public void hammerCharge(int hammer, TokenMemberInfo userInfo) {


    }
}
