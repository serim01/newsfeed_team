package com.sparta.newspeed.newsfeed.service;

import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.newsfeed.repository.OttRepository;
import com.sparta.newspeed.testdata.NewsFeedMock;
import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NewsfeedServiceTest implements UserMock, NewsFeedMock {
    private List<Newsfeed> newsfeeds;

    @Autowired
    NewsfeedService newsfeedService;

    @Autowired
    NewsfeedRespository newsfeedRespository;
    @Autowired
    UserRepository userRepository;
    @SpyBean
    OttRepository ottRepository;
    User user;
    NewsfeedResponseDto createdNewsfeed = null;
    int page = 0;
    String sortBy = "createdAt";

    @BeforeAll
    void init() {
        user = userRepository.save(userMock);
    }

    @BeforeEach
    void setup(){
        newsfeeds = newsfeedRespository.findAll();
    }

    @Nested
    @Order(1)
    @DisplayName("뉴스피드 생성")
    class createNewsFeed {
        @Test
        @DisplayName("뉴스피드 생성_성공")
        void createNewsFeed_success() {
            // given
            NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder().title(TITLE).content(CONTENT).ottName(TEST_OTT.getOttName()).remainMember(MEMBER).build();

            // when
            NewsfeedResponseDto responseDto = newsfeedService.createNewsFeed(requestDto, user);

            // then
            Newsfeed createNewsFeed = newsfeedRespository.findById(responseDto.getNewsFeedSeq()).orElse(null);

            assertEquals(requestDto.getTitle(), createNewsFeed.getTitle());
            assertEquals(requestDto.getContent(), createNewsFeed.getContent());
            assertEquals(requestDto.getOttName(), createNewsFeed.getOtt().getOttName());
            assertEquals(requestDto.getRemainMember(), createNewsFeed.getRemainMember());
            assertEquals(user.getUserName(), createNewsFeed.getUser().getUserName());
            createdNewsfeed = responseDto;
        }

        @Test
        @DisplayName("뉴스피드 생성_최대인원 초과")
        void createNewsFeed_NEWSFEED_REMAIN_MEMBER_OVER() {
            // given
            NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder().title(TITLE).content(CONTENT).ottName(TEST_OTT.getOttName()).remainMember(TEST_OTT.getMaxMember()+1).build();

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.createNewsFeed(requestDto, user));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("뉴스피드 전체 조회")
    class getNewsfeeds {
        @Test
        @DisplayName("뉴스피드 전체 조회_성공_날짜X")
        void getNewsfeeds_success_nonDate() {
            // when
            List<NewsfeedResponseDto> responseDto = newsfeedService.getNewsfeeds(page, sortBy, null, null);

            // then
            assertNotNull(responseDto);
        }
        @Test
        @DisplayName("뉴스피드 전체 조회_성공_좋아요기준")
        void getNewsfeeds_success_like() {
            // when
            List<NewsfeedResponseDto> responseDto = newsfeedService.getNewsfeeds(page, "like", null, null);

            // then
            assertNotNull(responseDto);
        }

        @Test
        @DisplayName("뉴스피드 전체 조회_성공_날짜O")
        void getNewsfeeds_success_Date() {
            //given
            LocalDate currentDate = LocalDate.now();
            // when
            List<NewsfeedResponseDto> responseDto = newsfeedService.getNewsfeeds(page, sortBy, currentDate, currentDate);

            // then
            assertNotNull(responseDto);
        }
        @Test
        @DisplayName("뉴스피드 전체 조회_실패_날짜X")
        void getNewsfeeds_nonDate_NEWSFEED_EMPTY() {
            //given
            int page = newsfeeds.size()/10+2;
            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(page, sortBy, null, null));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_EMPTY);
        }

        @Test
        @DisplayName("뉴스피드 전체 조회_실패_날짜O")
        void getNewsfeeds_Date_NEWSFEED_PERIOD_EMPTY() {
            //given
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(page, sortBy, tomorrow, tomorrow));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_PERIOD_EMPTY);
        }

        @Test
        @DisplayName("뉴스피드 전체 조회_기간입력 누락")
        void getNewsfeeds_MISSING_PERIOD_INPUT() {
            //given
            LocalDate currentDate = LocalDate.now();
            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(page, sortBy, currentDate,null));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.MISSING_PERIOD_INPUT);
        }
    }
    @Nested
    @Order(3)
    @DisplayName("뉴스피드 선택 조회")
    class getNewsfeed {
        @Test
        @DisplayName("뉴스피드 선택 조회_성공")
        void getNewsfeed_success() {
            // given
            Long newsFeedSeq = createdNewsfeed.getNewsFeedSeq();

            // when
            NewsfeedResponseDto responseDto = newsfeedService.getNewsfeed(newsFeedSeq);

            // then
            assertEquals(newsFeedSeq, responseDto.getNewsFeedSeq());
            assertEquals(createdNewsfeed.getTitle(), responseDto.getTitle());
            assertEquals(createdNewsfeed.getContent(), responseDto.getContent());
            assertEquals(createdNewsfeed.getOttName(), responseDto.getOttName());
            assertEquals(createdNewsfeed.getRemainMember(), responseDto.getRemainMember());
            assertEquals(createdNewsfeed.getUserName(), responseDto.getUserName());
        }
    }

    @Nested
    @Order(4)
    @DisplayName("뉴스피드 수정")
    class updateNewsFeed {
        @Test
        @DisplayName("뉴스피드 수정-내용변경_성공")
        void updateNewsFeed_success() {
            // given
            Long newsFeedSeq = createdNewsfeed.getNewsFeedSeq();
            NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder().title(createdNewsfeed.getTitle()).content("update").ottName(createdNewsfeed.getOttName()).remainMember(createdNewsfeed.getRemainMember()).build();

            // when
            NewsfeedResponseDto responseDto = newsfeedService.updateNewsFeed(newsFeedSeq, requestDto, user);

            // then
            assertEquals(newsFeedSeq, responseDto.getNewsFeedSeq());
            assertEquals(createdNewsfeed.getTitle(), responseDto.getTitle());
            assertEquals("update", responseDto.getContent());
            assertEquals(createdNewsfeed.getOttName(), responseDto.getOttName());
            assertEquals(createdNewsfeed.getRemainMember(), responseDto.getRemainMember());
            assertEquals(createdNewsfeed.getUserName(), responseDto.getUserName());
        }

        @Test
        @DisplayName("뉴스피드 수정_최대인원 초과")
        void updateNewsFeed_NEWSFEED_REMAIN_MEMBER_OVER() {
            // given
            Long newsFeedSeq = createdNewsfeed.getNewsFeedSeq();
            NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder().title(createdNewsfeed.getTitle()).content("update").ottName(createdNewsfeed.getOttName()).remainMember(TEST_OTT.getMaxMember()+1).build();

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.updateNewsFeed(newsFeedSeq, requestDto, user));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }
        }

    @Test
    @Order(5)
    @DisplayName("좋아요 증가")
    void increaseNewsfeedLike() {
        // given
        Long newsFeedSeq = newsfeeds.get(0).getNewsFeedSeq();
        Long likeCount = newsfeeds.get(0).getLike();

        // when
        newsfeedService.increaseNewsfeedLike(newsFeedSeq);

        // then
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsFeedSeq);
        assertEquals(likeCount + 1, newsfeed.getLike());
    }

    @Test
    @Order(6)
    @DisplayName("좋아요 감소")
    void decreaseNewsfeedLike() {
        // given
        Newsfeed newsfeed = newsfeeds.get(0);
        Long newsFeedSeq = newsfeed.getNewsFeedSeq();
        Long likeCount = newsfeed.getLike();

        // when
        newsfeedService.decreaseNewsfeedLike(newsFeedSeq);

        // then
        Newsfeed findNewsfeed = newsfeedService.findNewsfeed(newsFeedSeq);
        assertEquals(likeCount - 1, findNewsfeed.getLike());
    }

    @Nested
    @Order(7)
    @DisplayName("뉴스피드 삭제")
    class deleteNewsFeed {
        @Test
        @DisplayName("뉴스피드 삭제")
        void deleteNewsFeed_success() {
            // given
            Long newsFeedSeq = createdNewsfeed.getNewsFeedSeq();
            Newsfeed newsfeed = newsfeedService.findNewsfeed(newsFeedSeq);
            User user = newsfeed.getUser();

            //when
            newsfeedService.deleteNewsFeed(newsFeedSeq, user);

            // then
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.findNewsfeed(newsFeedSeq));

            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_NOT_FOUND);
        }

    }
}