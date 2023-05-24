package com.example.board.mock.user;

import com.example.board.config.SecurityConfig;
import com.example.board.consts.BoardConst;
import com.example.board.consts.UserConst;
import com.example.board.module.board.entity.Board;
import com.example.board.module.common.enums.RoleType;
import com.example.board.module.user.controller.UserController;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.service.UserService;
import com.example.board.security.WithMockCustomAdmin;
import com.example.board.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class UserMockTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
//    @WithMockCustomUser()
    void getListFail() throws Exception {
        // given
        Pageable pageable = PageRequest.of(1, 10);
        Page<User> page = new PageImpl<>(List.of(
                new User(1L, "kkr", "1234", "kkr@nate.com", RoleType.USER, UserStatus.ACTIVE),
                new User(2L, "khh", "1234", "khh@nate.com", RoleType.USER, UserStatus.ACTIVE)
        ));

        given(this.userService.getList(pageable)).willReturn(page);

        // when
        ResultActions perform = this.mvc.perform(
                get("/users?page={page}&size={size}", 1, 10)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("인증되지 않았습니다"));
    }

    @Test
    @WithMockCustomAdmin()
    void getList() throws Exception {
        // given
        User user = UserConst.user;
        LocalDateTime now = LocalDateTime.now();
        user.changeCreatedDate(now);
        Pageable pageable = UserConst.pageRequest;
        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

        given(this.userService.getList(pageable)).willReturn(page);

        // when
        ResultActions perform = this.mvc.perform(
                get("/users")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.users[0].id").value(user.getId()))
                .andExpect(jsonPath("$._embedded.users[0].username").value(user.getUsername()))
                .andExpect(jsonPath("$._embedded.users[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$._embedded.users[0].createDate").value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    @Test
    @WithMockCustomUser()
    void getUserFail() throws Exception {
        // given
        Long id = 10000000L;
        User user = UserConst.user;
        given(this.userService.getUser(user.getId()))
                .willReturn(
                        Optional.empty()
                );

        // when
        ResultActions perform = this.mvc.perform(
                get("/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("유저 정보를 찾을 수 없습니다."));
    }

    @Test
    @WithMockCustomUser()
    void getUserAuthFail() throws Exception {
        // given
        Long id = 2L;
//        User user = UserConst.user;
        given(this.userService.getUser(id))
                .willReturn(
                        Optional.of(
                                new User(2L, "khh", "1234", "khh@nate.com", RoleType.USER, UserStatus.INACTIVE)
                        )
                );

        // when
        ResultActions perform = this.mvc.perform(
                get("/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isForbidden())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("권한이 없습니다."));
    }

    @Test
    @WithMockCustomUser()
    void getUser() throws Exception {
        // given
        User user = UserConst.user;
        LocalDateTime now = LocalDateTime.now();
        user.changeCreatedDate(now);
        Long id = user.getId();
        given(this.userService.getUser(user.getId()))
            .willReturn(Optional.of(user));

        // when
        ResultActions perform = this.mvc.perform(
                get("/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.createDate").value(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$._links").exists());
    }

}
