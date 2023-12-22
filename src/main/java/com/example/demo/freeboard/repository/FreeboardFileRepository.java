package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.FreeboardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeboardFileRepository extends JpaRepository<FreeboardFile, Integer> {
}
