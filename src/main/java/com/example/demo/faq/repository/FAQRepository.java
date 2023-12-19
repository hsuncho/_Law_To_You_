package com.example.demo.faq.repository;

import com.example.demo.faq.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FAQRepository extends JpaRepository<FAQ, Integer>,
        FAQRepositoryCustom {

}
