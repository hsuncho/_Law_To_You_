package com.example.demo.member.lawyer.repository;

import com.example.demo.member.lawyer.entity.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LawyerRepository
        extends JpaRepository<Lawyer, String> {

    boolean existsByEmail(String email);

}
