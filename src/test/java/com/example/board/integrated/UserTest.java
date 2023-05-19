package com.example.board.integrated;

import com.example.board.security.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserTest extends AbstractIntegrated {

    @Test
//    @WithMockCustomUser()
    void getUserFail() throws Exception {
        this.mockMvc.perform(
                get("/users").accept(MediaType.APPLICATION_JSON_VALUE)
        )
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andDo(document("user-detail-fail", PayloadDocumentation.responseFields(getFailResponseField())));
    }
}
