package com.example.board.mock.board;

import com.example.board.config.SecurityConfig;
import com.example.board.module.board.controller.BoardController;
import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.board.request.BoardSaveRequest;
import com.example.board.module.board.request.BoardUpdateRequest;
import com.example.board.module.board.service.BoardService;
import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.controller.UserController;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.service.UserService;
import com.example.board.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        Pageable pageable = PageRequest.of(1, 10);
        Page<Board> page = new PageImpl<>(
                List.of(
                        new Board(
                                1L,
                                "첫 번째 게시물 입니다.",
                                "첫 번째 게시물 내용",
                                new User(
                                        1L,
                                        "kkr",
                                        "1234",
                                        "kkr@nate.com",
                                        RoleType.USER,
                                        UserStatus.ACTIVE
                                ),
                                BoardStatus.ACTIVE
                        ),
                        new Board(
                                2L,
                                "두 번째 게시물 입니다.",
                                "두 번째 게시물 내용",
                                new User(
                                        2L,
                                        "derek",
                                        "1234",
                                        "derek@nate.com",
                                        RoleType.USER,
                                        UserStatus.ACTIVE
                                ),
                                BoardStatus.ACTIVE
                        )
                )
        );

        // given
        Long id = 1L;
        given(this.boardService.getBoardListAndPage(pageable)).willReturn(page);

        // when
        ResultActions perform = this.mvc.perform(
                get("/board?page={page}&size={size}", 1, 10)
        );

        // then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("첫 번째 게시물 입니다."))
                .andExpect(jsonPath("$.content[0].content").value("첫 번째 게시물 내용"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("두 번째 게시물 입니다."))
                .andExpect(jsonPath("$.content[1].content").value("두 번째 게시물 내용"));
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
        Long id = 1L;
        given(this.boardService.getBoard(id))
                .willReturn(
                        Optional.of(
                                new Board(
                                        1L,
                                        "첫 번째 게시물 입니다.",
                                        "첫 번째 게시물 내용",
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
                        )
                );

        // when
        ResultActions perform = this.mvc.perform(
                get("/board/{id}", id)
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
    void saveBoardFail() throws Exception {
        // given
        BoardSaveRequest request = new BoardSaveRequest("", "첫 번째 게시물 내용");

        // when
        ResultActions perform = this.mvc.perform(
                post("/board")
                        .with(csrf())
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
        BoardSaveRequest request = new BoardSaveRequest("첫 번째 게시물 입니다.", "첫 번째 게시물 내용");
        given(this.boardService.save(request))
                .willReturn(
                        new Board(
                                1L,
                                "첫 번째 게시물 입니다.",
                                "첫 번째 게시물 내용",
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
                post("/board")
                        .with(csrf())
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
    @WithMockUser(username = "kkr", roles = "USER")
    void updateBoardValidFail() throws Exception {
        // given
        Long id = 0L;
        BoardUpdateRequest request = new BoardUpdateRequest("", "첫 번째 게시물 수정 내용.", "DELETE");

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .with(csrf())
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
    @WithMockUser(username = "kkr", roles = "USER")
    void updateBoardEnumValidFail() throws Exception {
        // given
        Long id = 0L;
        BoardUpdateRequest request = new BoardUpdateRequest("첫 번째 게시물 입니다.", "첫 번째 게시물 수정 내용.", "aasd");

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .with(csrf())
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
    @WithMockUser(username = "kkr", roles = "USER")
    void updateNotFoundBoard() throws Exception {
        // given
        Long id = 0L;
        BoardUpdateRequest request = new BoardUpdateRequest("첫 번째 게시물 입니다.", "첫 번째 게시물 수정 내용.", "DELETE");
        given(this.boardService.getBoard(id)).willReturn(Optional.empty());

        // when
        ResultActions perform = this.mvc.perform(
                put("/board/{id}", id)
                        .with(csrf())
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
    @WithMockUser(username = "kkr", roles = "USER")
    void updateBoard() throws Exception {
        // given
        Long id = 1L;
        BoardUpdateRequest request = new BoardUpdateRequest("첫 번째 게시물 수정된 제목입니다.", "첫 번째 게시물 수정된 내용입니다.", "DELETE");
        Optional<Board> optionalBoard = Optional.of(
                new Board(
                        1L,
                        "첫 번째 게시물 입니다.",
                        "첫 번째 게시물 내용",
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
                        .with(csrf())
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

//    @Test
//    @WithMockUser(username = "kkr", roles = "USER")
//    void deleteBoardFail() throws Exception {
//        // given
//        Long id = 0L;
//        given(this.boardService.getBoard(id)).willReturn(Optional.empty());
//
//        // when
//        ResultActions perform = this.mvc.perform(
//                delete("/board/{id}", id).with(csrf())
//        );
//
//        // then
//        perform
//                .andExpect(status().isBadRequest())
//                .andDo(print())
//                .andExpect(jsonPath("$.detail").value("게시물 정보를 찾을 수 없습니다."));
//    }
//
//    @Test
//    @WithMockUser(username = "kkr", roles = "USER")
//    void deleteBoard() throws Exception {
//        // given
//        Long id = 1L;
//        given(this.boardService.getBoard(id)).willReturn(Optional.empty());
//
//        // when
//        ResultActions perform = this.mvc.perform(
//                delete("/board/{id}", id).with(csrf())
//        );
//
//        // then
//        perform
//                .andExpect(status().isBadRequest())
//                .andDo(print());
////                .andExpect(jsonPath("$.detail".value(""));
//    }
}
