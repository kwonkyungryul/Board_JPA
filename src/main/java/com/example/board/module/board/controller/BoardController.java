package com.example.board.module.board.controller;

import com.example.board.module.board.BoardModelAssembler;
import com.example.board.module.board.dto.BoardDTO;
import com.example.board.module.board.dto.BoardModel;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<BoardModel>> getBoardListAndPage(
            Pageable pageable,
            PagedResourcesAssembler<Board> assembler
    ) {
        Page<Board> boardListAndPage = boardService.getBoardListAndPage(pageable);
        System.out.println(boardListAndPage.getContent().get(0).getContent());

        return ResponseEntity.ok(
                assembler.toModel(
                        boardListAndPage,
                        new BoardModelAssembler()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardModel> getBoard(@PathVariable Long id) {
        Optional<Board> optionalBoard = boardService.getBoard(id);

        if (optionalBoard.isEmpty()) {
            throw new Exception400(BoardConst.notFound);
        }

        return ResponseEntity.ok(new BoardModelAssembler().toModel(optionalBoard.get()));
    }

    @PostMapping
    public ResponseEntity<BoardModel> saveBoard(
            @Valid @RequestBody BoardSaveRequest request,
            Errors errors
    ) {

        if (errors.hasErrors()) {
            throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
        }

        return ResponseEntity.ok(new BoardModelAssembler().toModel(boardService.save(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardModel> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateRequest request,
            Errors errors
    ) {

        if (errors.hasErrors()) {
            throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<Board> optionalBoard = boardService.getBoard(id);
        if (optionalBoard.isEmpty()) {
            throw new Exception400(BoardConst.notFound);
        }

        return ResponseEntity.ok(
                new BoardModelAssembler().toModel(
                        boardService.update(request, optionalBoard.get())
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBoard(
            @PathVariable Long id
    ) {

        Optional<Board> optionalBoard = boardService.getBoard(id);
        if (optionalBoard.isEmpty()) {
            throw new Exception400(BoardConst.notFound);
        }

        boardService.deleteBoard(optionalBoard.get());
        return ResponseEntity.ok(Map.of("message", "게시물 삭제가 완료되었습니다."));
    }
}
