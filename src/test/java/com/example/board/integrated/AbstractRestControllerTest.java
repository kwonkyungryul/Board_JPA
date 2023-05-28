package com.example.board.integrated;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.board.auth.jwt.MyJwtProvider;
import com.example.board.auth.session.MyUserDetails;
import com.example.board.auth.session.MyUserDetailsService;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Objects;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class AbstractRestControllerTest {
    // build 위치
    //build/generated-snippets

    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    protected FieldDescriptor[] getPageResponseField() {
        return new FieldDescriptor[] {
                subsectionWithPath("content").description("내용 배열"),
                subsectionWithPath("pageable").description("page 종합 정보"),
                fieldWithPath("last").description("마지막 페이지 여부"),
                fieldWithPath("totalPages").description("총 페이지 수"),
                fieldWithPath("totalElements").description("총 요소 수"),
                fieldWithPath("size").description("페이지당 요소 수"),
                fieldWithPath("number").description("현재 페이지"),
                subsectionWithPath("sort").description("정렬"),
                fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                fieldWithPath("first").description("첫 페이지 여부"),
                fieldWithPath("empty").description("빈 페이지 여부")
        };
    }

    protected FieldDescriptor[] getFailResponseField = {
            fieldWithPath("type")
                    .type(JsonFieldType.STRING)
                    .attributes(getFormatAttribute(""))
                    .description("type"),
            fieldWithPath("title")
                    .type(JsonFieldType.STRING)
                    .attributes(getFormatAttribute(""))
                    .description("에러 코드 (이름)"),
            fieldWithPath("status")
                    .type(JsonFieldType.NUMBER)
                    .attributes(getFormatAttribute(""))
                    .description("에러 코드"),
            fieldWithPath("detail")
                    .type(JsonFieldType.STRING)
                    .attributes(getFormatAttribute(""))
                    .description("에러 메세지 (중요)"),
            fieldWithPath("instance")
                    .type(JsonFieldType.STRING)
                    .attributes(getFormatAttribute(""))
                    .description("요청 경로")
    };

    protected ParameterDescriptor[] getPageParameterDescriptors = {
            parameterWithName("page").description("페이지 번호"),
            parameterWithName("size").description("한 페이지에 보여줄 개수"),
            parameterWithName("sort").description("정렬 방식")
    };

    protected RequestHeadersSnippet getJwtRequestHeadersSnippet () {
        return requestHeaders(
            headerWithName("Authorization").description("토큰 정보")
        );
    }

    protected LinkDescriptor[] commonPageLinkDescriptor = {
            linkWithRel("self").description("현 페이지 링크"),
            linkWithRel("next").description("다음 페이지 링크 [다음페이지 존재하지 않으면 null]"),
            linkWithRel("last").description("마지막 페이지 링크"),
            linkWithRel("first").description("처음 페이지 링크"),
            linkWithRel("prev").description("이전 페이지 링크 [이전페이지 존재하지 않으면 null]"),
    };

    public Attributes.Attribute getFormatAttribute(String value){
        return new Attributes.Attribute("format", value);
    }

    protected FieldDescriptor[] pageResponseFieldDescriptors = {
            subsectionWithPath("page")
                    .type(JsonFieldType.OBJECT)
                    .attributes(getFormatAttribute(""))
                    .description("페이지 정보"),
            fieldWithPath("page.size")
                    .type(JsonFieldType.NUMBER)
                    .attributes(getFormatAttribute(""))
                    .description("한 페이지 리스트 개수"),
            fieldWithPath("page.totalElements")
                    .type(JsonFieldType.NUMBER)
                    .attributes(getFormatAttribute(""))
                    .description("리스트 총 사이즈"),
            fieldWithPath("page.totalPages")
                    .type(JsonFieldType.NUMBER)
                    .attributes(getFormatAttribute("개수 합계"))
                    .description("총 페이지"),
            fieldWithPath("page.number")
                    .type(JsonFieldType.NUMBER)
                    .attributes(getFormatAttribute("[주의] 0부터 시작"))
                    .description("현재 페이지")
    };

    protected FieldDescriptor[] linkPageResponseFieldDescriptors = {
            subsectionWithPath("_links")
                    .type(JsonFieldType.OBJECT)
                    .attributes(getFormatAttribute("[주의] Sort, Page, Size 정보만 들어가있고 그 외 Param은 없음"))
                    .description("링크 정보")
    };

    protected FieldDescriptor[] linkResponseFieldsDescriptorsIgnored = {
            subsectionWithPath("_links").ignored()
    };

    public String getUserToken() throws Exception {
        SecurityContextHolder.createEmptyContext();

        String token = this.mockMvc.perform(
                        post("/signIn")
                                .content(objectMapper.writeValueAsString(new LoginRequest("kkr", "1234")))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        String accessToken = Objects.requireNonNull(token).replace(MyJwtProvider.TOKEN_PREFIX, "");
        DecodedJWT verify = MyJwtProvider.verify(accessToken);
        Long id = verify.getClaim("id").asLong();
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return token;
    }

    protected LinkDescriptor[] commonLinkDescriptor = {
            linkWithRel("self").description("현재 페이지 링크"),
    };

    protected FieldDescriptor[] getUserResponseFieldDescriptor(String prefix) {
        return new FieldDescriptor[] {
                fieldWithPath(prefix + "id")
                        .type(JsonFieldType.NUMBER)
                        .attributes(getFormatAttribute(""))
                        .description("유저의 id"),
                fieldWithPath(prefix + "username")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("유저의 유저네임"),
                fieldWithPath(prefix + "email")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("유저의 이메일"),
                fieldWithPath(prefix + "createDate")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute("2023-05-24 20:08:00"))
                        .description("가입일자"),
                fieldWithPath(prefix + "_links.self.href")
                        .type(JsonFieldType.STRING)
                        .attributes(getFormatAttribute(""))
                        .description("상세링크")
        };
    }
}
