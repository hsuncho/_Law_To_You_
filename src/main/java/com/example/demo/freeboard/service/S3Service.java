package com.example.demo.freeboard.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class S3Service {

    private S3Client s3;

    @Value("${aws.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucketName}")
    private String bucketName;

    @PostConstruct
    private void initializeAmazon() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * 버킷에 파일을 업로드하고, 업로드한 버킷의 url 정보를 리턴
     * @param uploadFile - 업로드 할 파일의 실제 raw 데이터
     * @param fileName - 업로드 할 파일명
     * @return - 버킷에 업로드 된 버킷 경로(url)
     */
    public String uploadToS3Bucket(byte[] uploadFile, String fileName) {

        // 업로드 할 파일을 s3 오브젝트로 생성
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName) // 버킷 이름
                .key(fileName) // 파일명
                .build();

        // 오브젝트를 버킷에 업로드(위에서 생성한 오브젝트, 업로드 하고자 하는 파일(바이트 배열)
        s3.putObject(request, RequestBody.fromBytes(uploadFile));

        return uploadFileName(fileName);
    }

    // s3에 저장된 이미지의 url을 생성
    public URL getURL(String attachedFile) {

        log.info("attachedFile: {}", attachedFile);
        return s3.utilities().getUrl(GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(attachedFile)
                        .build());

    }



    // 업로드 된 파일의 url을 반환
    public String uploadFileName(String fileName) {
        return   s3.utilities()
                .getUrl(b -> b.bucket(bucketName).key(fileName))
                .toString();
    }

    // 업로드 된 파일 수정(삭제)

    public void deleteS3Object(List<String> fileName) {
        try{

         fileName.forEach(url -> {
             String fileUrlName = url.substring(url.lastIndexOf("/") + 1);
             String encodedFileName = URLDecoder.decode(fileUrlName, StandardCharsets.UTF_8);
             DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                     .bucket(bucketName)
                     .key(encodedFileName)
                     .build();


             log.info("File delete Success!! {}", encodedFileName);
             s3.deleteObject(deleteObjectRequest);
         });

        } catch (S3Exception e) {
            log.info(e.getMessage());
        }
    }


}
