package com.example.board.integrated.controller;

import com.example.board.auth.jwt.MyJwtProvider;
import com.example.board.consts.BoardConst;
import com.example.board.integrated.AbstractRestControllerTest;
import com.example.board.module.board.entity.Board;
import com.example.board.module.board.enums.BoardStatus;
import com.example.board.module.board.request.BoardSaveRequest;
import com.example.board.module.board.request.BoardUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("게시판 통합 테스트")
public class BoardControllerTest extends AbstractRestControllerTest {


    @Test
    @DisplayName("게시판 리스트 조회")
    void getList() throws Exception {
        Pageable pageable = PageRequest.of(1, 1);
        this.mockMvc.perform(
                        get("/board")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .param("sort", "id,desc")
                                .accept(MediaTypes.HAL_JSON)
//                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board-list",
                        queryParameters(getPageParameterDescriptors),
//                        getJwtRequestHeadersSnippet(),
                        links(commonPageLinkDescriptor),
                        responseFields(
                                subsectionWithPath("_embedded")
                                        .type(JsonFieldType.OBJECT)
                                        .attributes(getFormatAttribute(""))
                                        .description("내용"),
                                subsectionWithPath("_embedded.boards")
                                        .type(JsonFieldType.ARRAY)
                                        .attributes(getFormatAttribute(""))
                                        .description("리스트")
                        )
                        .and(getBoardResponseFieldDescriptor("_embedded.boards[]."))
                        .and(getUserResponseFieldDescriptor("_embedded.boards[].user."))
                        .and(fieldWithPath("_embedded.boards[]._links.self.href")
                                .type(JsonFieldType.STRING)
                                .attributes(getFormatAttribute(""))
                                .description("게시판 상세링크")
                        )
                        .and(pageResponseFieldDescriptors)
                        .and(linkPageResponseFieldDescriptors)
                ));
    }

    @Test
    @DisplayName("게시판 상세 조회")
    void getDetail() throws Exception {
        Board board = BoardConst.board;
        this.mockMvc.perform(
                        get("/board/{id}", board.getId())
                                .accept(MediaTypes.HAL_JSON)
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board-detail",
                        pathParameters(
                                parameterWithName("id").description("게시판의 id")
                        ),
                        getJwtRequestHeadersSnippet(),
                        links(commonLinkDescriptor),
                        responseFields(
                                getBoardResponseFieldDescriptor("")
                        )
                        .and(getUserResponseFieldDescriptor("user."))
                        .and(linkResponseFieldsDescriptorsIgnored)
                ));
    }

    @Test
    @DisplayName("게시판 상세 조회 실패 - 없는 게시판")
    void getDetailFail() throws Exception {
        this.mockMvc.perform(
                        get("/board/{id}", 1000000)
                                .accept(MediaTypes.HAL_JSON)
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("board-detail-fail",
                        pathParameters(
                                parameterWithName("id").description("게시판의 id")
                        ),
                        getJwtRequestHeadersSnippet(),
                        responseFields(
                                getFailResponseField
                        )
                ));
    }
    
    @Test
    @DisplayName("게시판 등록 성공")
    void saveBoard() throws Exception {
        Board board = BoardConst.board;
        BoardSaveRequest request = new BoardSaveRequest(board.getTitle(), board.getContent());
        this.mockMvc.perform(
                        post("/board")
                                .contentType(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board-save",
                        getJwtRequestHeadersSnippet(),
                        links(commonLinkDescriptor),
                        requestFields(
                                getBoardRequestFieldDescriptor()
                        ),
                        responseFields(
                                getBoardResponseFieldDescriptor("")
                        )
                        .and(getUserResponseFieldDescriptor("user."))
                        .and(linkResponseFieldsDescriptorsIgnored)
                ));
    }

    @Test
    @DisplayName("게시판 등록 실패 - 제목이 없음")
    void saveBoardFail() throws Exception {
        Board board = BoardConst.board;
        BoardSaveRequest request = new BoardSaveRequest("", board.getContent());
        this.mockMvc.perform(
                        post("/board")
                                .contentType(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("board-save-fail",
                        getJwtRequestHeadersSnippet(),
                        responseFields(getFailResponseField)
                ));
    }

    @Test
    @DisplayName("게시판 수정 성공")
    void updateBoard() throws Exception {
        Board board = BoardConst.board;
        BoardUpdateRequest request = new BoardUpdateRequest(board.getTitle(), board.getContent(), BoardStatus.ACTIVE.name());
        this.mockMvc.perform(
                        put("/board/{id}", board.getId())
                                .contentType(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board-update",
                        pathParameters(
                                parameterWithName("id").description("게시판의 id")
                        ),
                        getJwtRequestHeadersSnippet(),
                        links(commonLinkDescriptor),
                        requestFields(
                                getBoardRequestFieldDescriptor()
                        )
                        .and(fieldWithPath("status")
                                .type(JsonFieldType.STRING)
                                .attributes(getFormatAttribute(""))
                                .description("게시판 상태")
                        ),
                        responseFields(
                                getBoardResponseFieldDescriptor("")
                        )
                        .and(getUserResponseFieldDescriptor("user."))
                        .and(linkResponseFieldsDescriptorsIgnored)
                ));
    }

    @Test
    @DisplayName("게시판 수정 실패 - 없는 게시판")
    void updateBoardFail() throws Exception {
        Board board = BoardConst.board;
        BoardUpdateRequest request = new BoardUpdateRequest(board.getTitle(), board.getContent(), BoardStatus.ACTIVE.name());
        this.mockMvc.perform(
                        put("/board/{id}", 1000000)
                                .contentType(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(MyJwtProvider.HEADER, getUserToken()
                        )
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("board-update-fail",
                        pathParameters(
                                parameterWithName("id").description("게시판의 id")
                        ),
                        getJwtRequestHeadersSnippet(),
                        responseFields(getFailResponseField)
                ));
    }



    private FieldDescriptor[] getBoardResponseFieldDescriptor(String prefix) {
        return new FieldDescriptor[] {
                fieldWithPath(prefix + "id")
                        .type(JsonFieldType.NUMBER)
                        .attributes(getFormatAttribute(""))
                        .description("게시판의 id"),
                fieldWithPath(prefix + "title")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("게시판 제목"),
                fieldWithPath(prefix + "content")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("게시판 내용"),
                fieldWithPath(prefix + "createDate")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute("2023-05-24 20:08:00"))
                        .description("가입일자")
        };
    }

    private FieldDescriptor[] getBoardRequestFieldDescriptor() {
        return new FieldDescriptor[]{
                fieldWithPath("title")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("게시글 제목"),
                fieldWithPath("content")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("게시글 내용"),
        };
    }
}
