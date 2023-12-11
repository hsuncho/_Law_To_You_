package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreeboardRepository  extends JpaRepository<Freeboard, Integer>,
    FreeboardRepositoryCustom{

}
