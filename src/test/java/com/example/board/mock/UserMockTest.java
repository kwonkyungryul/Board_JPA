package com.example.board.mock;

import com.example.board.auth.session.MyUserDetails;
import com.example.board.config.SecurityConfig;
import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.controller.UserController;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.request.UserSaveRequest;
import com.example.board.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class UserMockTest {

    @Autowired
    private WebApplicationContext context;

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
//    @WithMockUser(username = "kkr", roles = "USER")
    void getUserFail() throws Exception {
        // given
        MyUserDetails myUserDetails = new MyUserDetails(new User(0L, "kkr", "1234", "kkr@nate.com", RoleType.USER, UserStatus.ACTIVE));

        given(this.userService.getUser(myUserDetails)).willReturn(
                Optional.empty()
        );

        // when
        ResultActions perform = this.mvc.perform(
                get("/users")
//                        .header("Authorization", jwt)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.detail").value("유저 정보를 찾을 수 없습니다."));
    }

    @Test
//    @WithUserDetails(value="customUsername", userDetailsServiceBeanName="userDetailsService")
    @WithMockUser(username = "kkr", roles = "USER")
    void getUser() throws Exception {
        // given
        MyUserDetails myUserDetails = createMockUserDetails();

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(myUserDetails, null));
        SecurityContextHolder.setContext(securityContext);

        // when
        ResultActions perform = this.mvc.perform(
                get("/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print());
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.username").value("kkr"))
//                .andExpect(jsonPath("$.email").value("kkr@nate.com"));
    }

    @Test
    void saveUserFail() throws Exception {
        // given
        UserSaveRequest request = new UserSaveRequest("", "1234", "derek@nate.com");

        // when
        ResultActions perform = this.mvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void saveUser() throws Exception {

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
