package com.example.board.mock.common;

import com.example.board.module.common.controller.CommonController;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.common.service.CommonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommonController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class CommonMockTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommonService commonService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO ?
    @Test
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("kkr", "1234");
        given(this.commonService.login(request)).willReturn("jwtToken");

        // when
        ResultActions perform = this.mvc.perform(
                post("/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print());
    }
}
