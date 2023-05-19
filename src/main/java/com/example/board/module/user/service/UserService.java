package com.example.board.module.user.service;

import com.example.board.auth.session.MyUserDetails;
import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.dto.UserDTO;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.repository.UserRepository;
import com.example.board.module.user.request.UserSaveRequest;
import com.example.board.module.user.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(User user) {
        return userRepository.findById(user.getId());
    }

    public User save(UserSaveRequest request) {
        return null;
    }
}
