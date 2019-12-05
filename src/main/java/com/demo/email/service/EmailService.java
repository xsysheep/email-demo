package com.demo.email.service;

import com.demo.email.entity.Resume;
import com.demo.email.util.Result;

public interface EmailService {

    public Resume getResumeById(Integer id);
}
