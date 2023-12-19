//package com.example.demo.freeboard.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//@Service
//public class FileService {
//
//    private final Path root = Paths.get("uploads");
//
//    public String save(MultipartFile file) {
//        try {
//            if (!Files.exists(root)) {
//                Files.createDirectory(root);
//            }
//            String filename = file.getOriginalFilename();
//            Files.copy(file.getInputStream(), this.root.resolve(filename));
//            return filename;
//        } catch (Exception e) {
//            throw new RuntimeException("파일 저장에 실패했습니다.", e);
//        }
//    }
//}
