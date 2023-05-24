package com.example.board.mock.common;

import com.example.board.config.SecurityConfig;
import com.example.board.consts.UserConst;
import com.example.board.module.common.controller.CommonController;
import com.example.board.module.common.enums.RoleType;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.common.service.CommonService;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.request.UserSaveRequest;
import com.example.board.module.user.service.UserService;
import com.example.board.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommonController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class CommonMockTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommonService commonService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO ?
    @Test
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("kkr", "1234");
        given(this.commonService.findUser(request)).willReturn(
                Optional.of(UserConst.user)
        );

        given(this.commonService.getToken(UserConst.user)).willReturn("jwtToken");

        // when
        ResultActions perform = this.mvc.perform(
                post("/signIn")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."));
    }

    @Test
    @WithMockCustomUser()
    void saveUserFail() throws Exception {
        // given
        UserSaveRequest request = new UserSaveRequest("", "1234", "derek@nate.com");

        // when
        ResultActions perform = this.mvc.perform(
                post("/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("아이디를 입력해주세요"));
    }

    @Test
    @WithMockCustomUser()
    void saveUser() throws Exception {
        // given
        UserSaveRequest request = new UserSaveRequest("derek", "1234", "derek@nate.com");
        given(this.userService.save(request))
                .willReturn(
                        new User(1L, "derek", "1234", "derek@nate.com", RoleType.USER, UserStatus.ACTIVE)
                );
        // when
        ResultActions perform = this.mvc.perform(
                post("/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("derek"))
                .andExpect(jsonPath("$.email").value("derek@nate.com"));
    }
}
