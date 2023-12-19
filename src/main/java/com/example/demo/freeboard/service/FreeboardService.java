package com.example.demo.freeboard.service;

import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.request.FreeboardUpdateRequestDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.repository.FreeboardRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FreeboardService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;


    public FreeListResponseDTO getFreeBoards(PageDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("rowNum").ascending()
        );

        Page<Freeboard> freeboards = freeboardRepository.findAll(pageable);

        List<Freeboard> freeList = freeboards.getContent();

        AtomicInteger rowNum = new AtomicInteger(1); //람다식에선 외부변수 변경이 불가하므로 AtomicInteger를 사용하여 1씩증가시킴
        List<FreeboardDetailResponseDTO> detailList
                = freeList.stream()
                .map(freeboard -> new FreeboardDetailResponseDTO(rowNum.getAndIncrement(), freeboard))
                .collect(Collectors.toList());

        return FreeListResponseDTO.builder()
                .count(detailList.size())
                .pageInfo(new PageResponseDTO(freeboards))
                .freeboards(detailList)
                .build();
    }

//    public Freeboard create(
//            final FreeboardCreateRequestDTO requestDTO
////             final TokenUserInfo userInfo
//    ) throws RuntimeException {
//
//        // 토큰에서 아이디값을 가져와야함
////        User user = getUser();
//
//        log.info("게시글 작성 완료! 제목: {}", requestDTO.getTitle());
//
////        return freeboardRepository.save(requestDTO.toEntity(user));
//    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }

    public FreeboardDetailResponseDTO modify(FreeboardUpdateRequestDTO dto) {
        
        Freeboard freeboardEntity = getFreeBoard(dto.getBno());

        freeboardEntity.setTitle("(수정됨) " + dto.getTitle());
        freeboardEntity.setContent(dto.getContent());
        freeboardEntity.setRegDate(LocalDateTime.now());

        Freeboard modifiedFreeboard = freeboardRepository.save(freeboardEntity);

        return new FreeboardDetailResponseDTO(modifiedFreeboard);
        
    }

    private Freeboard getFreeBoard(int bno) {
        return freeboardRepository.findById(bno).orElseThrow(
                () -> new RuntimeException(bno + "번 게시물이 존재하지 않습니다!")
        );
    }

    // 작성자
    public List<Freeboard> search(String search, boolean flag) {
        return freeboardRepository.findByContent(search, flag);

    }

    public Optional<Freeboard> getDetail(int bno) {
        return freeboardRepository.findById(bno);
    }
}
