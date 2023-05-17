package com.example.board.module.board.request;

import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.common.custom_annotation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;

public record BoardUpdateRequest(
        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @NotBlank(message = "내용을 입력해주세요")
        String content,

        @ValueOfEnum(enumClass = BoardStatus.class, message = "게시물 상태 값에 이상이 있습니다.")
        String status
) {
}
