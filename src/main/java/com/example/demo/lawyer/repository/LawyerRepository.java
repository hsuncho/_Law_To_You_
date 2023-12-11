package com.example.demo.lawyer.repository;

import com.example.demo.lawyer.entity.Lawyer;
import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LawyerRepository
        extends JpaRepository<Lawyer, String> {

    boolean existsByEmail(String email);

}
