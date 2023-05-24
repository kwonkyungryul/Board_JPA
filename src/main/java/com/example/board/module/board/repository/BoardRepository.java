package com.example.board.module.board.repository;

import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByStatusNot(Pageable pageable, BoardStatus boardStatus);
}
