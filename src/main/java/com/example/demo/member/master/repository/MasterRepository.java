package com.example.demo.member.master.repository;

import com.example.demo.member.master.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterRepository extends JpaRepository<Master, String> {
}
