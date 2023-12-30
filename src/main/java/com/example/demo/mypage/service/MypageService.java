package com.example.demo.mypage.service;

import com.example.demo.answer.entity.Answer;
import com.example.demo.answer.repository.AnswerRepository;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.repository.ConsultingRepository;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.repository.FreeboardRepository;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.mypage.dto.request.LawyerUpdateRequestDTO;
import com.example.demo.mypage.dto.request.UserUpdateRequestDTO;
import com.example.demo.mypage.dto.response.*;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MypageService {

    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final FreeboardRepository freeboardRepository;
    private final ConsultingRepository consultingRepository;
    private final AnswerRepository answerRepository;

    public UserDetailResponseDTO getUserInfo(TokenMemberInfo tokenMemberInfo) {

        User user = userRepository.findById(tokenMemberInfo.getId()).orElseThrow();

        return new UserDetailResponseDTO(user);
    }

    public LawyerDetailResponseDTO getLawyerInfo(TokenMemberInfo tokenMemberInfo
    ) {

        Lawyer lawyer = lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow();

        return new LawyerDetailResponseDTO(lawyer);
    }

    public UserDetailResponseDTO updateUser(final UserUpdateRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo) {

        String encoded = passwordEncoder.encode(requestDTO.getPassword());
        requestDTO.setPassword(encoded);

        User foundUser = userRepository.findById(tokenMemberInfo.getId()).orElseThrow();
        foundUser.setPassword(requestDTO.getPassword());
        foundUser.setNickname(requestDTO.getNickname());
        User saved = userRepository.save(foundUser);
        log.info("\n\n\n사용자 회원 정보 수정 정상 수행됨! - updated user - {}", saved);

        return new UserDetailResponseDTO(saved);
    }

    public LawyerDetailResponseDTO updateLawyer(final LawyerUpdateRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo) {

        String encoded = passwordEncoder.encode(requestDTO.getPassword());
        requestDTO.setPassword(encoded);

        Lawyer foundLawyer = lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow();
        foundLawyer.setLawyerPw(requestDTO.getPassword());
        Lawyer saved = lawyerRepository.save(foundLawyer);
        log.info("\n\n\n변호사 회원 정보 수정 정상 수행됨! - updated lawyer - {}", saved);

        return new LawyerDetailResponseDTO(saved);
    }

    public String deleteMember(TokenMemberInfo tokenMemberInfo) {
        log.info("탈퇴 요청 들어온 회원: - {}", tokenMemberInfo);

        memberRepository.deleteById(tokenMemberInfo.getId());

        if(tokenMemberInfo.getAuthority().equals("user")) {
        userRepository.deleteById(tokenMemberInfo.getId());
        } else if(tokenMemberInfo.getAuthority().equals("lawyer")) {
            lawyerRepository.deleteById(tokenMemberInfo.getId());
        }

        return "회원 탈퇴되었습니다.";
    }

    public MyPageFreeListResponseDTO getList(TokenMemberInfo tokenMemberInfo) {

        List<MyPageFreeboardDetailDTO> list = new ArrayList<>();
        List<Freeboard> freeList = new ArrayList<>();

        if(tokenMemberInfo.getAuthority().equals("user")) {
             freeList = freeboardRepository.findAllByUser(
                    userRepository.findById(tokenMemberInfo.getId()).orElseThrow()
            );
        } else if(tokenMemberInfo.getAuthority().equals("lawyer")) {
            freeList = freeboardRepository.findAllByLawyer(
                    lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow()
            );
        }

        for(Freeboard freeboard : freeList ) {
            MyPageFreeboardDetailDTO detailDTO = new MyPageFreeboardDetailDTO(freeboard);
            list.add(detailDTO);
        }

        return MyPageFreeListResponseDTO.builder()
                .count(list.size())
                .freeboardList(list)
                .build();

    }

    public UserConsultingListResponseDTO getConsultingList(PageDTO pageDTO, TokenMemberInfo tokenMemberInfo) {

        // offset = (현재 페이지번호 - 1) * 페이지당 요청하는 자료 개수
        Pageable pageable = PageRequest.of(
                pageDTO.getPage() - 1,
                pageDTO.getSize(),
                Sort.by("regDate").descending()
        );

        Page<Consulting> consultings = consultingRepository.findAll(pageable);
        List<Consulting> contentList = consultings.getContent();

        List<UserConsultingDetailDTO> detailList
                = contentList.stream()
                .filter(consulting -> consulting.getUser().getId().equals(tokenMemberInfo.getId()))
                .map(UserConsultingDetailDTO::new)
                .collect(Collectors.toList());

        return UserConsultingListResponseDTO.builder()
                .count(detailList.size())
                .consultingList(detailList)
                .build();
    }

    // 짧은 답변이 달리지 않은 온라인 상담 글만 삭제 가능
    public boolean deleteConsulting(int consultNum, TokenMemberInfo tokenMemberInfo) {

        List<Answer> answerList = consultingRepository.findById(consultNum).orElseThrow()
                .getAnswerList();
        if(!answerList.isEmpty()) { // 답변이 달린 상태라면 삭제 불가
            return false;
        }
        consultingRepository.deleteById(consultNum);
        return true;

    }

    public boolean validateForDelete(int consultNum, TokenMemberInfo tokenMemberInfo) {

        // 삭제하려는 상담 글의 작성자와 요청 보낸 토큰에서 사용자가 일치하는지 검증
        if(consultingRepository.findById(consultNum).orElseThrow()
                .getUser().getId()
                .equals(tokenMemberInfo.getId())) return true;
        return false;
    }

    public LawyerConsultingListResponseDTO getLawyerConsulting(TokenMemberInfo tokenMemberInfo) {

        // 토큰으로 변호사가 작성했던 답변 조회
        List<Answer> answerList = answerRepository.findByLawyer(
                lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow()
        );

        log.info("\n\n\nanswerList - {}", answerList);

        List<LawyerConsultingDetailDTO> list = new ArrayList<>();
        // 답변에서 온라인 상담글 뽑아내기
        for(Answer answer : answerList) {
            LawyerConsultingDetailDTO detailResponseDTO = new LawyerConsultingDetailDTO(answer);
            list.add(detailResponseDTO);
        }

        return LawyerConsultingListResponseDTO.builder()
                .count(list.size())
                .consultingList(list)
                .build();
    }


    public MyPageDetailResponseDTO getDetailedConsulting(int consultNum) {

        Consulting consulting = consultingRepository.findById(consultNum).orElseThrow();
        return new MyPageDetailResponseDTO(consulting);

    }

    // 깊은 상담글 상세 보기 요청 권한 검증 메서드
    public Boolean validateDetailed(TokenMemberInfo tokenMemberInfo, int consultNum) {

        log.info("\n\n\ntokenMemberInfo - {}\n\n\n", tokenMemberInfo);
        log.info("\n\n\ntokenMemberInfo -authority - {}\n\n\n", tokenMemberInfo.getAuthority());

        if(tokenMemberInfo.getAuthority().equals("lawyer")) {
            List<Lawyer> lawyerList = consultingRepository.findById(consultNum).orElseThrow()
                    .getAnswerList().stream().map(Answer::getLawyer).collect(Collectors.toList());

            return lawyerList.contains(
                    lawyerRepository.findById(tokenMemberInfo.getId()).orElseThrow());
        } else if(tokenMemberInfo.getAuthority().equals("user")) {

            return consultingRepository.findById(consultNum).orElseThrow()
                    .getUser().getId().equals(
                            tokenMemberInfo.getId());
        }
        return false;

    }

}










