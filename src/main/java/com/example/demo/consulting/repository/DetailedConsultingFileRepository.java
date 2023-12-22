package com.example.demo.consulting.repository;

import com.example.demo.consulting.entity.DetailedConsultingFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailedConsultingFileRepository
        extends JpaRepository<DetailedConsultingFile, Integer> {
}
