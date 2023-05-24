package com.example.board.jpa.board;

import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.board.repository.BoardRepository;
import com.example.board.module.common.enums.RoleType;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void init() {
        setUp(
                "게시물 제목",
                "내용",
                new User(null, "derek", "1234", "derek@nate.com", RoleType.USER, UserStatus.ACTIVE),
                BoardStatus.ACTIVE
        );
    }

    @Test
    void selectAll() {
        var boardList = boardRepository.findAll();
        Assertions.assertNotEquals(boardList.size(), 0);

        var board = boardList.get(0);
        Assertions.assertEquals(board.getId(), 1L);
        Assertions.assertEquals(board.getTitle(), "첫 번째 게시물 입니다.");
        Assertions.assertEquals(board.getContent(), "첫 번째 게시물 내용.");

        Assertions.assertEquals(board.getUser().getUsername(), "kkr");
        Assertions.assertEquals(board.getUser().getPassword(), "1234");
        Assertions.assertEquals(board.getUser().getEmail(), "kkr@nate.com");
        Assertions.assertEquals(board.getUser().getRole(), RoleType.USER);
        Assertions.assertEquals(board.getUser().getStatus(), UserStatus.ACTIVE);

        Assertions.assertEquals(board.getStatus(), BoardStatus.ACTIVE);
    }

    @Test
    void selectAndUpdate() {
        Long id = 1L;
        var optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isPresent()) {
            var result = optionalBoard.get();
            Assertions.assertEquals(result.getId(), 1L);
            Assertions.assertEquals(result.getTitle(), "첫 번째 게시물 입니다.");
            Assertions.assertEquals(result.getContent(), "첫 번째 게시물 내용.");

            Assertions.assertEquals(result.getUser().getUsername(), "kkr");
            Assertions.assertEquals(result.getUser().getPassword(), "1234");
            Assertions.assertEquals(result.getUser().getEmail(), "kkr@nate.com");
            Assertions.assertEquals(result.getUser().getRole(), RoleType.USER);
            Assertions.assertEquals(result.getUser().getStatus(), UserStatus.ACTIVE);

            Assertions.assertEquals(result.getStatus(), BoardStatus.ACTIVE);

            var content = "첫 번째 게시물 수정 내용.";
            result.setContent(content);
            Board merge = entityManager.merge(result);
            Assertions.assertEquals(merge.getId(), 1L);
            Assertions.assertEquals(merge.getTitle(), "첫 번째 게시물 입니다.");
            Assertions.assertEquals(merge.getContent(), "첫 번째 게시물 수정 내용.");

            Assertions.assertEquals(merge.getUser().getUsername(), "kkr");
            Assertions.assertEquals(merge.getUser().getPassword(), "1234");
            Assertions.assertEquals(merge.getUser().getEmail(), "kkr@nate.com");
            Assertions.assertEquals(merge.getUser().getRole(), RoleType.USER);
            Assertions.assertEquals(merge.getUser().getStatus(), UserStatus.ACTIVE);

            Assertions.assertEquals(merge.getStatus(), BoardStatus.ACTIVE);

        } else {
            Assertions.assertNotNull(optionalBoard.get());
        }
    }

    @Test
    void insertAndDelete() {
        var board = setUp(
                "새로운 게시물 입니다.",
                "새로운 게시물 내용.",
                new User(null, "crazydeveloper", "1234", "crazydeveloper@nate.com", RoleType.USER, UserStatus.ACTIVE),
                BoardStatus.ACTIVE
        );
        Optional<Board> optionalBoard = boardRepository.findById(board.getId());
        if (optionalBoard.isPresent()) {
            var result = optionalBoard.get();
            Assertions.assertEquals(result.getId(), 7L);
            Assertions.assertEquals(result.getTitle(), "새로운 게시물 입니다.");
            Assertions.assertEquals(result.getContent(), "새로운 게시물 내용.");

            Assertions.assertEquals(result.getUser().getUsername(), "crazydeveloper");
            Assertions.assertEquals(result.getUser().getPassword(), "1234");
            Assertions.assertEquals(result.getUser().getEmail(), "crazydeveloper@nate.com");
            Assertions.assertEquals(result.getUser().getRole(), RoleType.USER);
            Assertions.assertEquals(result.getUser().getStatus(), UserStatus.ACTIVE);

            Assertions.assertEquals(result.getStatus(), BoardStatus.ACTIVE);

            entityManager.remove(result);
            Optional<Board> deleteBoard = boardRepository.findById(result.getId());

            deleteBoard.ifPresent(Assertions::assertNull);
        }
    }

    private Board setUp(String title, String content, User user, BoardStatus status) {
        User persist = entityManager.persist(user);

        Board board = Board.builder()
                .title(title)
                .content(content)
                .user(persist)
                .status(status)
                .build();

        return entityManager.persist(board);
    }
}
