package com.sparta.newspeed.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.comment.service.CommentService;
import com.sparta.newspeed.config.WebSecurityConfig;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.testdata.CommentMock;
import com.sparta.newspeed.testdata.MockSpringSecurityFilter;
import com.sparta.newspeed.testdata.NewsFeedMock;
import com.sparta.newspeed.testdata.UserMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = {CommentController.class},
        // 제외 필터 지정
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class CommentControllerTest implements UserMock, NewsFeedMock, CommentMock {
    private MockMvc mvc;

    private Principal mockPrincipal; //가짜 인증이 필요해서

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        UserDetailsImpl testUserDetails = new UserDetailsImpl(userMock);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }
    @Test
    @DisplayName("댓글 등록")
    @Order(1)
    void createComment() throws Exception {
        // given
        this.mockUserSetup();
        Long newsfeedId = 1L;
        CommentRequestDto requestDto = CommentRequestDto.builder().content(CONTENT).build();

        String postInfo = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(post("/api/newsfeeds/"+newsfeedId + "/comments")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(2)
    @DisplayName("댓글 조회")
    void getComments() throws Exception {
        //given
        Long newsfeedId = 1L;
        // when - then
        mvc.perform(get("/api/newsfeeds/" + newsfeedId + "/comments")
        ).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        //given
        this.mockUserSetup();
        Long newsfeedId = 1L;
        Long commentsId = 1L;
        CommentRequestDto requestDto = CommentRequestDto.builder().content("update").build();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(put("/api/newsfeeds/" + newsfeedId +"/comments/" + commentsId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(mockPrincipal)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("뉴스피드 게시물 삭제")
    void deleteComment() throws Exception {
        this.mockUserSetup();
        Long newsfeedId = 1L;
        Long commentsId = 1L;
        // when - then
        mvc.perform(delete("/api/newsfeeds/" + newsfeedId +"/comments/" + commentsId)
                .principal(mockPrincipal)
        ).andExpect(status().is2xxSuccessful());
    }
}