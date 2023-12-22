package com.example.demo.consulting.repository;

import com.example.demo.consulting.entity.ConsultingFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultingFileRepository
        extends JpaRepository<ConsultingFile, Integer> {
}
