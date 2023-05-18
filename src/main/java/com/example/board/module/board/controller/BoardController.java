package com.example.board.module.board.controller;

import com.example.board.module.board.dto.BoardDTO;
import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardConst;
import com.example.board.module.board.request.BoardSaveRequest;
import com.example.board.module.board.request.BoardUpdateRequest;
import com.example.board.module.board.response.BoardResponse;
import com.example.board.module.board.service.BoardService;
import com.example.board.module.common.exception.Exception400;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<Page<BoardDTO>> getBoardListAndPage(Pageable pageable) {
        Page<Board> page = boardService.getBoardListAndPage(pageable);
        List<BoardDTO> content = page.getContent()
                .stream()
                .map(Board::toDTO)
                .toList();

        return ResponseEntity.ok(new PageImpl<>(content, pageable, page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long id) {
        Optional<Board> optionalBoard = boardService.getBoard(id);

        if (optionalBoard.isEmpty()) {
            throw new Exception400(BoardConst.notFound);
        }

        return ResponseEntity.ok(optionalBoard.get().toDTO());
    }

    @PostMapping
    public ResponseEntity<BoardResponse> saveBoard(
            @Valid BoardSaveRequest request,
            Errors errors
    ) {

        if (errors.hasErrors()) {
            throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
        }

        Board board = boardService.save(request);

        return ResponseEntity.ok(board.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> saveBoard(
            @PathVariable Long id,
            @Valid BoardUpdateRequest request,
            Errors errors
    ) {

        if (errors.hasErrors()) {
            throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<Board> optionalBoard = boardService.getBoard(id);
        if (optionalBoard.isEmpty()) {
            throw new Exception400(BoardConst.notFound);
        }

        Board board = boardService.update(request);


        return ResponseEntity.ok(board.toResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(
            @PathVariable Long id
    ) {

        Optional<Board> optionalBoard = boardService.getBoard(id);
        if (optionalBoard.isEmpty()) {
            throw new Exception400(BoardConst.notFound);
        }

        boardService.deleteBoard(optionalBoard.get());
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}
