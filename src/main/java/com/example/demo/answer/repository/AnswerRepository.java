package com.example.demo.answer.repository;

import com.example.demo.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository
        extends JpaRepository<Answer, Integer> {


}
