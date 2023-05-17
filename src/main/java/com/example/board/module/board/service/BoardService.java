package com.example.board.module.board.service;

import com.example.board.module.board.entity.Board;
import com.example.board.module.board.repository.BoardRepository;
import com.example.board.module.board.request.BoardSaveRequest;
import com.example.board.module.board.request.BoardUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Page<Board> getBoardListAndPage(Pageable pageable) {
        return null;
    }

    public Optional<Board> getBoard(Long id) {
        return null;
    }

    public Board save(BoardSaveRequest request) {
        return null;
    }

    public Board update(BoardUpdateRequest request) {
        return null;
    }

    public void deleteBoard(Board board) {
        
    }
}
