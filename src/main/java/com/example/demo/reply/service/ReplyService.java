package com.example.demo.reply.service;

import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.repository.FreeboardRepository;
import com.example.demo.freeboard.service.FreeboardService;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.reply.dto.request.ReplyCreateRequestDTO;
import com.example.demo.reply.dto.response.ReplyDetailResponseDTO;
import com.example.demo.reply.dto.response.ReplyListResponseDTO;
import com.example.demo.reply.entity.Reply;
import com.example.demo.reply.repository.ReplyRepository;
import com.example.demo.token.auth.TokenMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final ReplyRepository replyRepository;

    public ReplyListResponseDTO getList(PageDTO pageDTO, int bno, TokenMemberInfo userInfo) {
        Pageable pageable = PageRequest.of(
                pageDTO.getPage() - 1,
                pageDTO.getSize(),
                Sort.by("regDate").descending()
        );
        List<Reply> replyList = replyRepository.findByBno(bno, pageable);

  log.info("replyList : {}", replyList);
        List<ReplyDetailResponseDTO> detailList
                = replyList.stream()
                .map(reply -> {
                    ReplyDetailResponseDTO dto = new ReplyDetailResponseDTO(reply);
                    if(!userInfo.getAuthority().equals("user")) {
                        if(reply.getLawyer() != null) {
                            if (reply.getLawyer().getLawyerId().equals(userInfo.getId())) {
                                dto.setDeleteButton(true);
                            }
                        }
                    } else {
                        if (reply.getUser() != null) {
                            dto.setDeleteButton(reply.getUser().getId().equals(userInfo.getId()));
                        }
                    }
                    return dto;
                })

                .collect(Collectors.toList());

                log.info("dto에 들어있는 값 {}", detailList);

        return ReplyListResponseDTO.builder()
                .count(detailList.size())
                .replyList(detailList)
                .build();
    }

    public ReplyDetailResponseDTO insert(ReplyCreateRequestDTO requestDTO, TokenMemberInfo userInfo) {

        Optional<User> userOptional = userRepository.findById(userInfo.getId());
        Optional<Lawyer> lawyerOptional = lawyerRepository.findById(userInfo.getId());
        Freeboard freeboard = freeboardRepository.findById(requestDTO.getBno()).orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));
        Reply reply;
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            reply = replyRepository.save(requestDTO.toEntity(user, freeboard));
        } else {
            Lawyer lawyer = lawyerOptional.get();
            reply = replyRepository.save(requestDTO.toEntity(lawyer, freeboard));
        }

        log.info("댓글 작성 완료! {}", reply);

        return new ReplyDetailResponseDTO(reply);

    }

    // 댓글 작성자가 맞는지 여부 확인
    public boolean userTrue(TokenMemberInfo memberInfo, int rno) {
        String writerId = null;
        if(lawyerRepository.findById(memberInfo.getId()).isPresent()){
            writerId = lawyerRepository.findById(memberInfo.getId()).orElseThrow().getLawyerId();
        } else {
            writerId = userRepository.findById(memberInfo.getId()).orElseThrow().getId();
        }
        return replyRepository.findByUserReply(writerId, rno);
    }

    public void delete(int rno) {
        Reply reply = replyRepository.findById(rno).orElseThrow();

        replyRepository.delete(reply);
    }
}
