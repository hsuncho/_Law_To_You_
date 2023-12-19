package com.example.demo.freeboard.service;

import com.example.demo.freeboard.dto.FreeboardCreateDTO;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import com.example.demo.freeboard.dto.request.FreeboardUpdateRequestDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import com.example.demo.freeboard.repository.FreeboardRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.token.auth.TokenMemberInfo;
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

    public FreeListResponseDTO retrieve(String userId) {
        User user = getUser(userId);

        List<Freeboard> entityList = freeboardRepository.findAllByUser(user);

        List<FreeboardDetailResponseDTO> dtoList = entityList.stream()
                .map(FreeboardDetailResponseDTO::new)
                .collect(Collectors.toList());

        return FreeListResponseDTO.builder()
                .freeboards(dtoList)
                .build();
    }

    public FreeListResponseDTO create(
            final FreeboardCreateDTO requestDTO,
             final TokenMemberInfo userInfo
    ) throws RuntimeException {

        // 토큰에서 아이디값을 가져와야함
        User user = getUser(userInfo.getId());
        Freeboard freeboard = freeboardRepository.save(requestDTO.getRequestDTO().toEntity(user));

        log.info("게시글 작성 완료! 제목: {}", requestDTO.getRequestDTO().getTitle());
        freeboardRepository.save(freeboard);
        return retrieve(userInfo.getId());
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }

    public FreeListResponseDTO modify(FreeboardUpdateRequestDTO dto, String userId) {

        Optional<Freeboard> byId = freeboardRepository.findById(dto.getBno());


        byId.ifPresent(freeboard -> {
            freeboard.setTitle("(수정됨) " + dto.getTitle());
            freeboard.setContent(dto.getContent());
            freeboard.setRegDate(LocalDateTime.now());

            freeboardRepository.save(freeboard);
        });


        return retrieve(userId);
        
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
