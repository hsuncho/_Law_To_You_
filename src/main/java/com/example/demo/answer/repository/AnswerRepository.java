package com.example.demo.answer.repository;

import com.example.demo.answer.entity.Answer;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.member.lawyer.entity.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository
        extends JpaRepository<Answer, Integer> {

    List<Answer> findByLawyer(Lawyer lawyer);


}
