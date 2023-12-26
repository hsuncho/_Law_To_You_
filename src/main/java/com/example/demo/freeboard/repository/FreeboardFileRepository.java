package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.FreeboardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeboardFileRepository extends JpaRepository<FreeboardFile, Integer> {

    @Query("SELECT f FROM FreeboardFile f WHERE f.freeboard.bno= :bno")
    List<FreeboardFile> findByBno(@Param("bno") int bno);

}
