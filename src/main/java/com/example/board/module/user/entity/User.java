package com.example.board.module.user.entity;

import com.example.board.module.common.jpa.BaseTime;
import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.dto.UserDTO;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.response.UserResponse;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "USERS")
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("고유 번호")
    private Long id;

    @Comment("아이디")
    private String username;

    @Comment("비밀번호")
    private String password;

    @Comment("이메일")
    private String email;

    @Comment("권한")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Comment("유저 상태")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Builder
    public User(Long id, String username, String password, String email, RoleType role, UserStatus status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public UserDTO toDTO() {
        return new UserDTO(id, username, email);
    }

    public UserResponse toResponse() {
        return new UserResponse(id, username, email);
    }
}
