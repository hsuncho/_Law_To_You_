package com.example.demo.member.lawyer.repository;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.master.dto.response.lawyerListResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LawyerRepository
        extends JpaRepository<Lawyer, String> {

    boolean existsByEmail(String email);

}
