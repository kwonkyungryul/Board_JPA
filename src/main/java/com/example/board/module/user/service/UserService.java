package com.example.board.module.user.service;

import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.repository.UserRepository;
import com.example.board.module.user.request.UserSaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> getList(Pageable pageable) {
        return userRepository.findByStatusNot(pageable, UserStatus.DELETE);
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findByIdAndStatusNot(userId, UserStatus.DELETE);
    }

    public User save(UserSaveRequest request) {
        return userRepository.save(request.toEntity());
    }

}
