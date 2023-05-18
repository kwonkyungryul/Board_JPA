package com.example.board.mock.user;

import com.example.board.auth.session.MyUserDetails;
import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.controller.UserController;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.request.UserSaveRequest;
import com.example.board.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebAppConfiguration
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = SecurityConfig.class)
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class UserMockTest {

//    @Autowired
//    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    String jwt = "";

    @BeforeEach
    public void setup() {
//        jwt = MyJwtProvider.create(new User(1L, "kkr", "1234", "kkr@nate.com", RoleType.USER, UserStatus.ACTIVE));
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
    }

    @Test
    @WithMockUser(username = "kkr", roles = "USER")
    void getUserFail() throws Exception {
        // given
        Long id = 0L;
        given(this.userService.getUser(id))
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
    @WithMockUser(username = "kkr", roles = "USER")
    void getUser() throws Exception {
        // given
        Long id = 1L;
        given(this.userService.getUser(id))
                .willReturn(
                        Optional.of(
                                new User(1L, "kkr", "1234", "kkr@nate.com", RoleType.USER, UserStatus.ACTIVE)
                        )
                );

        // when
        ResultActions perform = this.mvc.perform(
                get("/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("kkr"))
                .andExpect(jsonPath("$.email").value("kkr@nate.com"));
    }

    @Test
    @WithMockUser(username = "kkr", roles = "USER")
    void saveUserFail() throws Exception {
        // given
        UserSaveRequest request = new UserSaveRequest("", "1234", "derek@nate.com");

        // when
        ResultActions perform = this.mvc.perform(
                post("/users")
                        .with(csrf())
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
    @WithMockUser(username = "kkr", roles = "USER")
    void saveUser() throws Exception {
        // given
        UserSaveRequest request = new UserSaveRequest("derek", "1234", "derek@nate.com");
        given(this.userService.save(request))
                .willReturn(
                        new User(1L, "derek", "1234", "derek@nate.com", RoleType.USER, UserStatus.ACTIVE)
                );
        // when
        ResultActions perform = this.mvc.perform(
                post("/users")
                        .with(csrf())
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

    private MyUserDetails createMockUserDetails() {
        User user = new User();
        user.setId(1L);
        user.setUsername("kkr");
        user.setPassword("1234");
        // 필요한 사용자 정보 설정

        return new MyUserDetails(user);
    }

}
