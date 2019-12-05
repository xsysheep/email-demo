package com.demo.email.service.serviceimpl;

import com.demo.email.entity.Resume;
import com.demo.email.mapper.ResumeMapper;
import com.demo.email.service.EmailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    public ResumeMapper resumeMapper;

    @Override
    public Resume getResumeById(Integer id) {
        Resume resume = resumeMapper.selectByPrimaryKey(id);
        return resume;
    }
}
