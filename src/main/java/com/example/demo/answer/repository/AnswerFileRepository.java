package com.example.demo.answer.repository;

import com.example.demo.answer.entity.AnswerFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerFileRepository
        extends JpaRepository<AnswerFile, Integer> {
}
