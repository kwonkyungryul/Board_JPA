package com.example.board.mock.board;

import com.example.board.config.SecurityConfig;
import com.example.board.consts.BoardConst;
import com.example.board.module.board.controller.BoardController;
import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.board.request.BoardSaveRequest;
import com.example.board.module.board.request.BoardUpdateRequest;
import com.example.board.module.board.service.BoardService;
import com.example.board.module.common.enums.RoleType;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@WebMvcTest(BoardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class BoardMockTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BoardService boardService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getBoardListAndPage() throws Exception {
        Board board = BoardConst.board;
        Pageable pageable = BoardConst.pageRequest;
        LocalDateTime now = LocalDateTime.now();
        board.changeCreatedDate(now);
        Page<Board> boardPage = new PageImpl<>(List.of(board), pageable, 1);

        // given
        given(this.boardService.getBoardListAndPage(pageable)).willReturn(boardPage);

        // when
        ResultActions perform = this.mvc.perform(
                get("/board")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.boards[0].id").value(board.getId()))
                .andExpect(jsonPath("$._embedded.boards[0].title").value(board.getTitle()))
                .andExpect(jsonPath("$._embedded.boards[0].content").value(board.getContent()))
                .andExpect(jsonPath("$._embedded.boards[0].createDate").value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$._embedded.boards[0].user.id").value(board.getUser().getId()))
                .andExpect(jsonPath("$._embedded.boards[0].user.username").value(board.getUser().getUsername()))
                .andExpect(jsonPath("$._embedded.boards[0].user.email").value(board.getUser().getEmail()))
                .andExpect(jsonPath("$._embedded.boards[0].user.createDate").value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    @Test
    void getBoardFail() throws Exception {
        // given
        Long id = 0L;
        given(this.boardService.getBoard(id))
                .willReturn(
                        Optional.empty()
                );

        // when
        ResultActions perform = this.mvc.perform(
                get("/board/{id}", id)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("게시물 정보를 찾을 수 없습니다."));
    }

    @Test
    void getBoard() throws Exception {
        // given
        Board board = BoardConst.board;
        LocalDateTime now = LocalDateTime.now();
        board.changeCreatedDate(now);
        Long id = board.getId();
        given(this.boardService.getBoard(id))
                .willReturn(
                        Optional.of(board)
                );

        // when
        ResultActions perform = this.mvc.perform(
                get("/board/{id}", id)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(board.getId()))
                .andExpect(jsonPath("$.title").value(board.getTitle()))
                .andExpect(jsonPath("$.content").value(board.getContent()))
                .andExpect(jsonPath("$.createDate").value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.user.id").value(board.getUser().getId()))
                .andExpect(jsonPath("$.user.username").value(board.getUser().getUsername()))
                .andExpect(jsonPath("$.user.email").value(board.getUser().getEmail()))
                .andExpect(jsonPath("$.user.createDate").value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.user._links").exists())
                .andExpect(jsonPath("$._links").exists());
    }

    @Test
    @WithMockCustomUser()
    void saveBoardFail() throws Exception {
        // given
        BoardSaveRequest request = new BoardSaveRequest("", "첫 번째 게시물 내용");

        // when
        ResultActions perform = this.mvc.perform(
                post("/board")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("제목을 입력해주세요"));
    }


    @Test
    @WithMockCustomUser()
    void saveBoard() throws Exception {
        // given
        Board board = BoardConst.board;
        BoardSaveRequest request = new BoardSaveRequest("첫 번째 게시물 입니다.", "첫 번째 게시물 내용");
        given(this.boardService.save(request, board.getUser())).willReturn(board);

        // when
        ResultActions perform = this.mvc.perform(
                post("/board")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then

        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("첫 번째 게시물 입니다."))
                .andExpect(jsonPath("$.content").value("첫 번째 게시물 내용"));
    }

    @Test
    @WithMockCustomUser()
    void updateBoardValidFail() throws Exception {
        // given
        Long id = 0L;
        BoardUpdateRequest request = new BoardUpdateRequest("", "첫 번째 게시물 수정 내용.", "DELETE");

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then

        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("제목을 입력해주세요"));
    }

    @Test
    @WithMockCustomUser()
    void updateBoardEnumValidFail() throws Exception {
        // given
        Long id = 0L;
        BoardUpdateRequest request = new BoardUpdateRequest("첫 번째 게시물 입니다.", "첫 번째 게시물 수정 내용.", "aasd");

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then

        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("게시물 상태 값에 이상이 있습니다."));
    }

    @Test
    @WithMockCustomUser()
    void updateNotFoundBoard() throws Exception {
        // given
        Long id = 0L;
        BoardUpdateRequest request = new BoardUpdateRequest("첫 번째 게시물 입니다.", "첫 번째 게시물 수정 내용.", "DELETE");
        given(this.boardService.getBoard(id)).willReturn(Optional.empty());

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("게시물 정보를 찾을 수 없습니다."));
    }

    @Test
    @WithMockCustomUser()
    void updateBoard() throws Exception {
        // given
        Board board = BoardConst.board;
        Long id = board.getId();
        BoardUpdateRequest request = new BoardUpdateRequest("첫 번째 게시물 수정된 제목입니다.", "첫 번째 게시물 수정된 내용입니다.", "DELETE");
        Optional<Board> optionalBoard = Optional.of(board);
        given(this.boardService.getBoard(id)).willReturn(optionalBoard);
        given(this.boardService.update(request, optionalBoard.get())).willReturn(
                new Board(
                        1L,
                        "첫 번째 게시물 수정된 제목입니다.",
                        "첫 번째 게시물 수정된 내용입니다.",
                        new User(
                                1L,
                                "kkr",
                                "1234",
                                "kkr@nate.com",
                                RoleType.USER,
                                UserStatus.ACTIVE
                        ),
                        BoardStatus.ACTIVE
                )
        );

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("첫 번째 게시물 수정된 제목입니다."))
                .andExpect(jsonPath("$.content").value("첫 번째 게시물 수정된 내용입니다."));
    }

    @Test
    @WithMockCustomUser()
    void deleteBoardFail() throws Exception {
        // given
        Long id = 0L;
        given(this.boardService.getBoard(id)).willReturn(Optional.empty());

        // when
        ResultActions perform = this.mvc.perform(
                delete("/board/{id}", id)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("게시물 정보를 찾을 수 없습니다."));
    }

    @Test
    @WithMockCustomUser()
    void deleteBoard() throws Exception {
        // given
        Board board = BoardConst.board;
        Long id = 1L;
        given(this.boardService.getBoard(id)).willReturn(
                Optional.of(board)
        );

        // when
        ResultActions perform = this.mvc.perform(
                delete("/board/{id}", id)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("게시물 삭제가 완료되었습니다."));
    }
}
