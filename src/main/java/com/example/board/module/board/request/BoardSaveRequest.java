package com.example.board.module.board.request;

import com.example.board.module.board.entity.Board;
import com.example.board.module.user.entity.User;
import jakarta.validation.constraints.NotBlank;

public record BoardSaveRequest(
        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @NotBlank(message = "내용을 입력해주세요")
        String content
) {
    public Board toEntity(User user) {
        return new Board(null, title, content, user, null);
    }
}
