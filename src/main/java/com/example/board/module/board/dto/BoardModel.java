package com.example.board.module.board.dto;

import com.example.board.module.board.entity.Board;
import com.example.board.module.user.UserModelAssembler;
import com.example.board.module.user.dto.UserModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.format.DateTimeFormatter;

@Relation(value = "board", collectionRelation = "boards")
@Getter
@Setter
public class BoardModel extends RepresentationModel<BoardModel> {
    Long id;
    String title;
    String content;
    String createDate;

    UserModel user;

    public BoardModel(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        if (board.getCreatedDate() == null ) {
            board.changeCreatedDate(null);
        }
        this.user = new UserModelAssembler().toModel(board.getUser());
        this.createDate = board.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
