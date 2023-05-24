package com.example.board.module.board.service;

import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.board.repository.BoardRepository;
import com.example.board.module.board.request.BoardSaveRequest;
import com.example.board.module.board.request.BoardUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Page<Board> getBoardListAndPage(Pageable pageable) {
        return boardRepository.findByStatusNot(pageable, BoardStatus.DELETE);
    }

    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);
    }

    public Board save(BoardSaveRequest request) {
        return boardRepository.save(request.toEntity());
    }

    public Board update(BoardUpdateRequest request, Board board) {
        board.setTitle(request.title());
        board.setContent(request.content());
        board.setStatus(BoardStatus.valueOf(request.status()));
        return boardRepository.save(board);
    }

    public void deleteBoard(Board board) {
        board.setStatus(BoardStatus.DELETE);
    }
}
