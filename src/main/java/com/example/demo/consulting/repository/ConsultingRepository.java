package com.example.demo.consulting.repository;

import com.example.demo.consulting.entity.Consulting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultingRepository
        extends JpaRepository<Consulting, Integer>{

}
