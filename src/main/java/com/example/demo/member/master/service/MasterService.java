package com.example.demo.member.master.service;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.master.dto.response.ApproveLawyerResponseDTO;
import com.example.demo.member.master.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MasterService {

    private final LawyerRepository lawyerRepository;

    public void approval(String lawyerId) {

        Lawyer lawyer = lawyerRepository.findById(lawyerId).orElseThrow();
        if(!lawyer.isApproval()) {
            lawyer.setApproval();
        }
            lawyerRepository.save(lawyer);
    }
}
