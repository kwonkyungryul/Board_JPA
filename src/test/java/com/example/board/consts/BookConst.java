package com.example.board.consts;

import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.user.entity.User;

public interface BookConst {
    Board board = new Board(1L, "첫 번째 게시물 입니다.", "첫 번째 게시물 내용", UserConst.user, BoardStatus.ACTIVE);
}
