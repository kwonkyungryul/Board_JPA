package com.example.board.module.user.repository;

import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndStatusNot(Long id, UserStatus status);

    Optional<User> findByUsernameAndStatusNot(String username, UserStatus status);

    Page<User> findByStatusNot(Pageable pageable, UserStatus status);
}
