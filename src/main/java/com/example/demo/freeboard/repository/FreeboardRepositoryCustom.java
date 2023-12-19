package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.user.entity.User;

import java.util.List;

public interface FreeboardRepositoryCustom {

    List<Freeboard> findByContent(String content, boolean flag);

    List<Freeboard> findAllByUser(User user);

}
