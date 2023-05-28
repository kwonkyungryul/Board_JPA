package com.example.board.module.common.service;

import com.example.board.auth.jwt.MyJwtProvider;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CommonService {

    private final UserRepository userRepository;


    public CommonService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUser(LoginRequest request) {
        return userRepository.findByUsernameAndStatusNot(request.username(), UserStatus.DELETE);
    }

    public String getToken(User user) {
        return MyJwtProvider.create(user);
    }
}
