package com.example.demo.consulting.repository;

import com.example.demo.consulting.entity.Consulting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsultingRepository
        extends JpaRepository<Consulting, Integer>{

}
