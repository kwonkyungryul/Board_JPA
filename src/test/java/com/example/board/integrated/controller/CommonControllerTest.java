package com.example.board.integrated.controller;

import com.example.board.integrated.AbstractRestControllerTest;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.user.request.UserSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("공통 통합 테스트")
public class CommonControllerTest extends AbstractRestControllerTest {

    @Test
    @DisplayName("로그인")
    void signIn() throws Exception {
        LoginRequest loginRequest = new LoginRequest("kkr", "1234");
        this.mockMvc.perform(
                        post("/signIn")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("signIn",
                        requestFields(
                                fieldWithPath("username")
                                        .type(JsonFieldType.STRING)
                                        .attributes(getFormatAttribute(""))
                                        .description("아이디"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .attributes(getFormatAttribute(""))
                                        .description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("로그인 성공 메세지")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 없음")
    void signInFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("kkr", "");
        this.mockMvc.perform(
                        post("/signIn")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("signIn-fail",
                        responseFields(
                                getFailResponseField
                        )
                ));
    }

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {
        UserSaveRequest request = new UserSaveRequest("kkr", "1234", "kkr@nate.com");
        this.mockMvc.perform(
                        post("/signup")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("signUp",
                        requestFields(
                                fieldWithPath("username")
                                        .type(JsonFieldType.STRING)
                                        .attributes(getFormatAttribute(""))
                                        .description("아이디"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .attributes(getFormatAttribute(""))
                                        .description("비밀번호"),
                                fieldWithPath("email")
                                        .type(JsonFieldType.STRING)
                                        .attributes(getFormatAttribute(""))
                                        .description("이메일")
                        ),
                        responseFields(
                                getUserResponseFieldDescriptor("")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디가 없음")
    void signUpFail() throws Exception {
        UserSaveRequest request = new UserSaveRequest("", "1234", "kkr@nate.com");
        this.mockMvc.perform(
                        post("/signup")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("signUp-fail",
                        responseFields(
                                getFailResponseField
                        )
                ));
    }
}
