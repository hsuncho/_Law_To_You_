package com.example.demo.freeboard.service;

import com.example.demo.freeboard.dto.FreeboardFileDTO;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import com.example.demo.freeboard.dto.request.FreeboardUpdateRequestDTO;
import com.example.demo.freeboard.dto.response.FreeListResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardCreateResponseDTO;
import com.example.demo.freeboard.dto.response.FreeboardDetailResponseDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import com.example.demo.freeboard.repository.FreeboardFileRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FreeboardService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;
    private final FreeboardFileRepository freeboardFileRepository;
    private final S3Service s3Service;


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

    public FreeboardCreateResponseDTO create(
            final FreeboardCreateRequestDTO requestDTO,
             final TokenMemberInfo userInfo,
            List<String> uploadedFileList) throws Exception {

        // 토큰에서 아이디값을 가져와야함
        User user = userRepository.findById(userInfo.getId()).orElseThrow();

        Freeboard freeboard = freeboardRepository.save(requestDTO.toEntity(user));

        log.info("게시글 작성 완료! 제목: {}", requestDTO.getTitle());
        List<FreeboardFile> freeboardList = new ArrayList<>();
        uploadedFileList.forEach(file -> {
            FreeboardFile freeboardFile = new FreeboardFileDTO(file).toEntity(freeboard);
            freeboardFileRepository.save(freeboardFile);
            freeboardList.add(freeboardFile);
        });
        freeboard.setFreeboardFiles(freeboardList);

        return new FreeboardCreateResponseDTO(freeboard);
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }

    /*
    public FreeListResponseDTO modify(FreeboardUpdateRequestDTO dto, String userId) {


        freeboardRepository.findById(getUser(userInfo.getId())).orElseThrow(
                () -> new RuntimeException(bno + "번 게시물이 존재하지 않습니다!")
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
        return
        );
    }
 */

    // 검색
    public List<Freeboard> search(String search, boolean flag) {
        return freeboardRepository.findByContent(search, flag);

    }

    public Optional<Freeboard> getDetail(int bno) {
        return freeboardRepository.findById(bno);
    }

    public String uploadFiles(MultipartFile multipartFile) throws IOException {

        String uniqueFilename = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String uploadFile = s3Service.uploadToS3Bucket(multipartFile.getBytes(), uniqueFilename); // 파일을 바이트로 변환후 집어넣기

        return uploadFile;

    }


    // s3 이미지 업로드
//    public String uploadImage(MultipartFile uploadFile) throws IOException {
//
//        String uniqueFilename = UUID.randomUUID() + "_" + uploadFile.getOriginalFilename();
//
//        return s3Service.uploadToS3Bucket(uploadFile.getBytes(), uniqueFilename);
//
//    }
//
//    public List<FreeboardFile> findUploadPath(int bno) {
//
//        Freeboard freeboard = freeboardRepository.findById(bno).orElseThrow();
//        return freeboard.getFreeboardFiles();
//    }





}
