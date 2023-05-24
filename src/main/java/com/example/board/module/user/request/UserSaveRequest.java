package com.example.board.module.user.request;

import com.example.board.module.common.enums.RoleType;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;

public record UserSaveRequest(
        
    @NotBlank(message = "아이디를 입력해주세요")
    String username,

    @NotBlank(message = "비밀번호를 입력해주세요")
    String password,

    @NotBlank(message = "이메일을 입력해주세요")
    String email
) {
    public User toEntity() {
        return new User(
                null,
                username,
                password,
                email,
                RoleType.USER,
                UserStatus.ACTIVE
        );
    }
}
