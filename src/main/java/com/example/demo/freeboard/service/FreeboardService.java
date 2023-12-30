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
import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FreeboardService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final MemberRepository memberRepository;
    private final FreeboardFileRepository freeboardFileRepository;
    private final S3Service s3Service;

    public FreeListResponseDTO getFreeBoards(PageDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("regDate").descending()
        );

        Page<Freeboard> freeboards = freeboardRepository.findAll(pageable);

        List<Freeboard> freeList = freeboards.getContent();

        List<FreeboardDetailResponseDTO> detailList
                = freeList.stream()
                .map(freeboard -> new FreeboardDetailResponseDTO("전체 요청",freeboard))
                .collect(Collectors.toList());

        return FreeListResponseDTO.builder()
                .count(detailList.size())
                .pageInfo(new PageResponseDTO(freeboards))
                .freeboards(detailList)
                .build();
    }

    public FreeboardCreateResponseDTO create(
            final FreeboardCreateRequestDTO requestDTO,
             final TokenMemberInfo userInfo,
            List<String> uploadedFileList) throws Exception {

        // 토큰에서 아이디값을 가져와야함
        Optional<User> userOptional = userRepository.findById(userInfo.getId());
        Optional<Lawyer> lawyerOptional = lawyerRepository.findById(userInfo.getId());
        Freeboard freeboard;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
             freeboard = freeboardRepository.save(requestDTO.toEntity(user));
        } else {
            Lawyer lawyer = lawyerOptional.get();
            freeboard = freeboardRepository.save(requestDTO.toEntity(lawyer));
        }

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

    @Transactional
    public FreeboardDetailResponseDTO modify(FreeboardUpdateRequestDTO dto,
                                             List<String> uploadedFileList) {

            List<FreeboardFile> filesToDelete = freeboardFileRepository.findByBno(dto.getBno());
            List<String> deleteFile = filesToDelete.stream()
                    .map(FreeboardFile::getRoute)
                    .collect(Collectors.toList());
            s3Service.deleteS3Object(deleteFile);

            filesToDelete.forEach(freeboardFile -> {
                freeboardFileRepository.delete(freeboardFile);
                freeboardFileRepository.flush();
            });

            Freeboard freeboard = freeboardRepository.findById(dto.getBno()).orElseThrow();
            freeboard.setTitle("(수정됨) " + dto.getTitle());
            freeboard.setContent(dto.getContent());
            freeboard.setRegDate(LocalDateTime.now());

            if (uploadedFileList != null) {
                uploadedFileList.forEach(file -> {
                    FreeboardFile freeboardFile = new FreeboardFileDTO(file).toEntity(freeboard);
                    freeboardFileRepository.save(freeboardFile);
                });
            }
            Freeboard saved = freeboardRepository.save(freeboard);

            log.info("게시글 수정 정상 작동! {}", saved);
            return new FreeboardDetailResponseDTO(saved);
    }

    // 게시글 작성자가 맞는지 여부 확인
    public boolean userTrue(TokenMemberInfo memberInfo, int bno) {
        Member member = memberRepository.findById(memberInfo.getId()).orElseThrow();
        User user = null;
        Lawyer lawyer = null;
        if (member.getAuthority().equals("user")) {
            user = userRepository.findById(memberInfo.getId()).orElseThrow();
            return freeboardRepository.findByUserBoard(user, bno);
        } else {
            lawyer = lawyerRepository.findById(memberInfo.getId()).orElseThrow();
            return freeboardRepository.findByLawyerBoard(lawyer, bno);
        }
    }

    // 검색
    public List<Freeboard> search(String search, boolean flag) {
        return freeboardRepository.findByContent(search, flag);
    }

    // 검색시 게시물 수
    public int searchCNT(String search, boolean flag) {
        return freeboardRepository.findByContentCNT(search, flag);
    }

    public Optional<Freeboard> getDetail(int bno) {
        return freeboardRepository.findById(bno);
    }

    public String uploadFiles(MultipartFile multipartFile) throws IOException {

        String uniqueFilename = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String uploadFile = s3Service.uploadToS3Bucket(multipartFile.getBytes(), uniqueFilename); // 파일을 바이트로 변환후 집어넣기

        return uploadFile;

    }

    public void delete(int bno) {
        Freeboard freeboard = freeboardRepository.findById(bno).orElseThrow();
        List<FreeboardFile> filesToDelete = freeboardFileRepository.findByBno(bno);
        List<String> deleteFile = filesToDelete.stream()
                .map(FreeboardFile::getRoute)
                .collect(Collectors.toList());
        s3Service.deleteS3Object(deleteFile);

        filesToDelete.forEach(freeboardFile -> {
            freeboardFileRepository.delete(freeboardFile);
            freeboardFileRepository.flush();
        });

        freeboardRepository.delete(freeboard);

    }
}
