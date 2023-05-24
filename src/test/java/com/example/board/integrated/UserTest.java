package com.example.board.integrated;

import com.example.board.security.WithMockCustomAdmin;
import com.example.board.security.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserTest extends AbstractIntegrated {

    // TODO 얘 좀 이상함;;
//    @Test
//    void getListFail() throws Exception {
//        var pageable = PageRequest.of(1, 1);
//        this.mockMvc.perform(
//                get("/users")
//                        .param("page", "1")
//                        .param("size", "1")
//                        .param("sort", "id,desc")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//        )
//                .andExpect(status().isUnauthorized())
//                .andDo(print())
//                .andDo(document("user-detail-fail", PayloadDocumentation.responseFields(getFailResponseField())));
//    }

    @Test
    @WithMockCustomAdmin()
    void getList() throws Exception {

        this.mockMvc.perform(
                        get("/users")
                                .param("page", "1")
                                .param("size", "1")
                                .param("sort", "id,desc")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-list",
                        queryParameters(getPageParameterDescriptors()),
                        responseFields(getUserResponseField("content[]")).and(getPageResponseField())
                ));
    }

    @Test
    @WithMockCustomUser()
    void getUserDetail() throws Exception {

        this.mockMvc.perform(
                        get("/users")
                                .param("id", "1")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-list",
                        responseFields(getUserResponseField(""))
                ));
    }

    private FieldDescriptor[] getUserResponseField(String prefix) {
        return new FieldDescriptor[] {
                fieldWithPath(prefix+"id").description("공지의 id"),
                fieldWithPath(prefix+"username").description("공지 제목"),
                fieldWithPath(prefix+"email").description("공지 내용")
        };
    }
}
