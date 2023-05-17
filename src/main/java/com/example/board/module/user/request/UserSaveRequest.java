package com.example.board.module.user.request;

import jakarta.validation.constraints.NotBlank;

public record UserSaveRequest(
        
    @NotBlank(message = "아이디를 입력해주세요")
    String username,

    @NotBlank(message = "비밀번호를 입력해주세요")
    String password,

    @NotBlank(message = "이메일을 입력해주세요")
    String email
) {
}
