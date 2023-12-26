package com.example.demo.reply.repository;

import com.example.demo.freeboard.repository.FreeboardRepositoryCustom;
import com.example.demo.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer>,
        ReplyRepositoryCustom{

}
