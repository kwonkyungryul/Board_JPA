package com.example.board.module.board.entity;

import com.example.board.module.board.dto.BoardDTO;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.board.response.BoardResponse;
import com.example.board.module.common.jpa.BaseTime;
import com.example.board.module.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "BOARD_LIST")
public class Board extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("고유번호")
    private Long id;

    @Comment("게시물 제목")
    private String title;

    @Comment("게시물 내용")
    private String content;

    @Comment("작성자")
    @ManyToOne
    private User user;

    @Comment("게시물 상태")
    @Enumerated(EnumType.STRING)
    private BoardStatus status;

    @Builder
    public Board(Long id, String title, String content, User user, BoardStatus status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.status = status;
    }

    public BoardDTO toDTO() {
        return new BoardDTO(id, title, content);
    }

    public BoardResponse toResponse() {
        return new BoardResponse(id, title, content);
    }
}
