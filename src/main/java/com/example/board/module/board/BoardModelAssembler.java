package com.example.board.module.board;

import com.example.board.module.board.controller.BoardController;
import com.example.board.module.board.dto.BoardModel;
import com.example.board.module.board.entity.Board;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class BoardModelAssembler extends RepresentationModelAssemblerSupport<Board, BoardModel> {
    public BoardModelAssembler() {
        super(BoardController.class, BoardModel.class);
    }

    @Override
    public BoardModel toModel(Board board) {
        BoardModel resource = new BoardModel(board);
        resource.add(linkTo(methodOn(BoardController.class).getBoard(board.getId())).withSelfRel());
        return resource;
    }

    @Override
    public CollectionModel<BoardModel> toCollectionModel(Iterable<? extends Board> entities) {
        return super.toCollectionModel(entities);
    }
}
