package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.Member;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;

import java.util.List;

public interface FreeboardRepositoryCustom {

    List<Freeboard> findByContent(String content, boolean flag);

    List<Freeboard> findAllByUser(User user);

    List<Freeboard> findAllByLawyer(Lawyer lawyer);

    // 게시물이 해당 유저가 쓴게 맞는지
    boolean findByUserBoard(User user, int bno);
    // 게시물이 해당 변호사가 쓴게 맞는지
    boolean findByLawyerBoard(Lawyer user, int bno);

    int findByContentCNT(String search, boolean flag);
}
