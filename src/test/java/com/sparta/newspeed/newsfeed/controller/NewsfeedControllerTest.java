package com.sparta.newspeed.newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.config.WebSecurityConfig;
import com.sparta.newspeed.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.testdata.MockSpringSecurityFilter;
import com.sparta.newspeed.testdata.NewsFeedMock;
import com.sparta.newspeed.testdata.UserMock;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
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
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = {NewsfeedController.class},
        // 제외 필터 지정
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class NewsfeedControllerTest implements UserMock, NewsFeedMock {
    private MockMvc mvc;

    private Principal mockPrincipal; //가짜 인증이 필요해서

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    NewsfeedService newsfeedService;

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
    @DisplayName("뉴스피드 등록")
    @Order(1)
    void createNewsFeed() throws Exception {
        // given
        this.mockUserSetup();
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder().title(TITLE).content(CONTENT).ottName(TEST_OTT.getOttName()).remainMember(MEMBER).build();

        String postInfo = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(post("/api/newsfeeds")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스피드 목록 조회")
    @Order(2)
    void getNewsfeeds() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 6, 10);

        // NewsfeedMock 인터페이스가 제공하는 mock 객체를 사용합니다.
        NewsfeedResponseDto newsfeed1 = mock(NewsfeedResponseDto.class);
        when(newsfeed1.getTitle()).thenReturn("Title1");
        when(newsfeed1.getContent()).thenReturn("Content1");

        NewsfeedResponseDto newsfeed2 = mock(NewsfeedResponseDto.class);
        when(newsfeed2.getTitle()).thenReturn("Title2");
        when(newsfeed2.getContent()).thenReturn("Content2");

        List<NewsfeedResponseDto> responseDtoList = List.of(newsfeed1, newsfeed2);

        given(newsfeedService.getNewsfeeds(Mockito.anyInt(), Mockito.anyString(), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .willReturn(responseDtoList);

        // when
        var action = mvc.perform(get("/api/newsfeeds")
                .param("page", "0")
                .param("sortBy", "createdAt")
                .param("startDate", date.toString())
                .param("endDate", date.toString())
                .accept(MediaType.APPLICATION_JSON));

        // then
        action
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Title1")))
                .andExpect(jsonPath("$[0].content", is("Content1")))
                .andExpect(jsonPath("$[1].title", is("Title2")))
                .andExpect(jsonPath("$[1].content", is("Content2")));
    }

    @DisplayName("뉴스피드 선택조회")
    @Nested
    @Order(3)
    class getNewsfeed {
        @Test
        @DisplayName("뉴스피드 선택조회_성공")
        void getNewsfeed_success() throws Exception {
            // given
            given(newsfeedService.getNewsfeed(eq(NEWSFEED_SEQ))).willReturn(new NewsfeedResponseDto(newsfeedMock));

            // when
            var action = mvc.perform(get("/api/newsfeeds/{newsfeedId}", NEWSFEED_SEQ)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            action
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(TITLE))
                    .andExpect(jsonPath("$.content").value(CONTENT));
        }

        @Test
        @DisplayName("뉴스피드 선택조회_실패")
        void getNewsfeed_fail() throws Exception {
            // given
            when(newsfeedService.getNewsfeed(eq(NEWSFEED_SEQ))).thenThrow(new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));

            // when
            var action = mvc.perform(get("/api/newsfeeds/{newsfeedId}", NEWSFEED_SEQ)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            action
                    .andExpect(status().isNotFound());
        }

    }

    @Order(4)
    @DisplayName("뉴스피드 게시물 수정")
    @Test
    void test3() throws Exception {
        // given
        this.mockUserSetup();

        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder().title(TITLE).content("update").ottName(TEST_OTT.getOttName()).remainMember(4).build();

        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(put("/api/newsfeeds/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(mockPrincipal)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("뉴스피드 게시물 삭제")
    void deleteNewsfeed() throws Exception{
        this.mockUserSetup();
        // when - then
        mvc.perform(delete("/api/newsfeeds/" + 1L)
                .principal(mockPrincipal)
        ).andExpect(status().is2xxSuccessful());
    }
}