package com.example.board.module.common.service;

import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    private final UserRepository userRepository;

    public CommonService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(LoginRequest request) {
        return null;
    }
}
