package com.example.board.integrated.controller;

import com.example.board.auth.jwt.MyJwtProvider;
import com.example.board.consts.UserConst;
import com.example.board.integrated.AbstractRestControllerTest;
import com.example.board.module.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("유저 통합 테스트")
public class UserControllerTest extends AbstractRestControllerTest {

    @Test
    @DisplayName("유저 리스트 조회")
    void getList() throws Exception {
        this.mockMvc.perform(
                        get("/users")
                                .param("page", "1")
                                .param("size", "1")
                                .param("sort", "id,desc")
                                .accept(MediaTypes.HAL_JSON)
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-list",
                        queryParameters(getPageParameterDescriptors),
                        getJwtRequestHeadersSnippet(),
                        links(commonPageLinkDescriptor),
                        responseFields(
                                subsectionWithPath("_embedded")
                                        .type(JsonFieldType.OBJECT)
                                        .attributes(getFormatAttribute(""))
                                        .description("내용"),
                                subsectionWithPath("_embedded.users")
                                        .type(JsonFieldType.ARRAY)
                                        .attributes(getFormatAttribute(""))
                                        .description("리스트")
                        )
                        .and(getUserResponseFieldDescriptor("_embedded.users[]."))
                        .and(fieldWithPath("_embedded.users[]._links.self.href")
                                .type(JsonFieldType.STRING)
                                .attributes(getFormatAttribute(""))
                                .description("유저 상세링크")
                        )
                        .and(pageResponseFieldDescriptors)
                        .and(linkPageResponseFieldDescriptors)
                ));
    }

    @Test
    @DisplayName("유저 상세 조회")
    void getUserDetail() throws Exception {
        User user = UserConst.user;
        this.mockMvc.perform(
                        get("/users/{id}", user.getId())
                                .accept(MediaTypes.HAL_JSON)
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-detail",
                        pathParameters(
                                parameterWithName("id").description("고유번호")
                        ),
                        getJwtRequestHeadersSnippet(),
                        links(commonLinkDescriptor),
                        responseFields(
                                getUserResponseFieldDescriptor("")
                        )
                ));
    }

    @Test
    @DisplayName("유저 상세 조회 실패 - 없는 유저")
    void getUserDetailFail() throws Exception {
        this.mockMvc.perform(
                        get("/users/{id}", 1000000)
                                .accept(MediaTypes.HAL_JSON)
                                .header(MyJwtProvider.HEADER, getUserToken())
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("user-detail-fail",
                        pathParameters(
                                parameterWithName("id").description("고유번호")
                        ),
                        getJwtRequestHeadersSnippet(),
                        responseFields(getFailResponseField)
                ));
    }
}
