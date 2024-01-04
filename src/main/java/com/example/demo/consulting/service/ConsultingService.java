package com.example.demo.consulting.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.example.demo.answer.entity.Answer;
import com.example.demo.answer.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.consulting.dto.request.ConsultingRegisterRequestDTO;
import com.example.demo.consulting.dto.request.DetailedConsultingRegisterRequestDTO;
import com.example.demo.consulting.dto.response.ConsultingDetailResponseDTO;
import com.example.demo.consulting.dto.response.ConsultingFileDTO;
import com.example.demo.consulting.dto.response.ConsultingListResponseDTO;
import com.example.demo.consulting.dto.response.DetailedConsultingFileDTO;
import com.example.demo.consulting.dto.response.UpdatedConsultingDetailResponseDTO;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.ConsultingFile;
import com.example.demo.consulting.entity.DetailedConsultingFile;
import com.example.demo.consulting.repository.ConsultingFileRepository;
import com.example.demo.consulting.repository.ConsultingRepository;
import com.example.demo.consulting.repository.DetailedConsultingFileRepository;
import com.example.demo.freeboard.dto.PageDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import com.example.demo.freeboard.service.S3Service;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.token.auth.TokenMemberInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ConsultingService {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final ConsultingRepository consultingRepository;
    private final ConsultingFileRepository consultingFileRepository;
    private final DetailedConsultingFileRepository detailedConsultingFileRepository;
    private final S3Service s3Service;
    private S3Client s3;

    @Value("${aws.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucketName}")
    private String bucketName;

    public ConsultingListResponseDTO getList(PageDTO pageDTO) {

        // offset = (현재 페이지번호 - 1) * 페이지당 요청하는 자료 개수
        Pageable pageable = PageRequest.of(
                pageDTO.getPage() - 1,
                pageDTO.getSize(),
                Sort.by("regDate").descending()
        );

        Page<Consulting> consultings = consultingRepository.findAll(pageable);
        List<Consulting> contentList = consultings.getContent();

        List<ConsultingDetailResponseDTO> detailList
                = contentList.stream()
                .map(ConsultingDetailResponseDTO::new)
                .collect(Collectors.toList());

       return ConsultingListResponseDTO.builder()
                .count(detailList.size())
                .pageInfo(new PageResponseDTO(consultings))
               .consultingList(detailList)
                .build();

    }

    public Optional<Consulting> getDetail(int consultNum) {
        return consultingRepository.findById(consultNum);
    }

    public ConsultingDetailResponseDTO insert(final ConsultingRegisterRequestDTO requestDTO, TokenMemberInfo tokenMemberInfo, final List<String> uploadedFileList) throws Exception{

        User user = userRepository.findById(tokenMemberInfo.getId()).orElseThrow();
        user.setHammer(user.getHammer() - 1);

        Consulting saved = consultingRepository.save(requestDTO.toEntity(user));

        log.info("온라인 상담 글 작성 정상 수행됨! - saved consulting - {}", saved);

        List<ConsultingFile> consultingFileList = new ArrayList<>();
        uploadedFileList.forEach(file -> {
            ConsultingFile consultingFile = new ConsultingFileDTO(file).toEntity(saved);
            consultingFileRepository.save(consultingFile);
            consultingFileList.add(consultingFile);
        });

        em.flush();
        em.clear();

        saved.setConsultingFiles(consultingFileList);

        consultingRepository.save(saved);
//        Consulting foundConsulting
//                = consultingRepository.findById(saved.getConsultNum()).orElseThrow();
//        log.info("foundConsulting: {}", foundConsulting);

//        return new ConsultingDetailResponseDTO(foundConsulting);

        return new ConsultingDetailResponseDTO(saved);
    }

    /**
     * 업로드 된 파일을 서버에 저장하고 저장 경로를 리턴
     * @param multipartFile - 업로드 된 파일의 정보
     * @return - 실제로 저장된 파일 경로
     */

    public String uploadFiles(final MultipartFile multipartFile) throws IOException {

        String uniqueFileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String uploadFile = s3Service.uploadToS3Bucket(multipartFile.getBytes(), uniqueFileName);
        return uploadFile;
    }

    /**
     * 버킷에 파일을 업로드 하고 업로드 한 버킷의 url 정보를 리턴
     * @param uploadFile
     * @param fileName
     * @return 버킷에  업로드 된 버킷 경로
     */

    public String uploadToS3Buckekt(byte[] uploadFile, String fileName) {
        // 업로드 할 파일을 S3 오브젝트로 생성
        PutObjectRequest request
                = PutObjectRequest.builder()
                .bucket(bucketName) // 버킷 이름
                .key(fileName) // 파일명
                .build();

        // 오브젝트를 버킷에 업로드(위에서 생성한 오브젝트, 업로드 하고자 하는 파일(바이트 배열)
        s3.putObject(request, RequestBody.fromBytes(uploadFile));

        // 업로드 된 파일의 url을 반환
        return s3.utilities()
                .getUrl(b -> b.bucket(bucketName).key(fileName))
                .toString();
    }

    public UpdatedConsultingDetailResponseDTO registerDetailedConsulting(final DetailedConsultingRegisterRequestDTO requestDTO, final List<String> uploadedFileList) {

        Consulting consulting = consultingRepository.findById(requestDTO.getConsultNum()).orElseThrow();
        consulting.setUpdateTitle(requestDTO.getTitle());
        consulting.setUpdateContent(requestDTO.getContent());

        uploadedFileList.forEach(file -> {
            DetailedConsultingFile detailedConsultingFile = new DetailedConsultingFileDTO(file).toEntity(consulting);
            detailedConsultingFileRepository.save(detailedConsultingFile);
        });

        Consulting saved = consultingRepository.save(consulting);
        log.info("깊은 상담 글 작성 정상 수행됨! - saved consulting - {}", saved);
        saved.setUpdateDate(LocalDateTime.now());

        return new UpdatedConsultingDetailResponseDTO(saved);

    }

    public Boolean validateWriter(TokenMemberInfo tokenMemberInfo, int consultNum) {

        log.info("\n\n\ntokenMemberInfo - {}\n\n\n", tokenMemberInfo);
        log.info("\n\n\ntokenMemberInfo -authority - {}\n\n\n", tokenMemberInfo.getAuthority());

        if(tokenMemberInfo.getAuthority().equals("lawyer")) {
            return true;
        }
        else if(tokenMemberInfo.getAuthority().equals("user")) {
            return consultingRepository.findById(consultNum).orElseThrow()
                    .getUser().getId().equals(
                            tokenMemberInfo.getId());
        }
        return false;

    }



}
