package com.sparta.newspeed.comment.service;

import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.comment.dto.CommentResponseDto;
import com.sparta.newspeed.comment.entity.Comment;
import com.sparta.newspeed.comment.repository.CommentRepository;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.testdata.CommentMock;
import com.sparta.newspeed.testdata.NewsFeedMock;
import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentServiceTest implements NewsFeedMock, UserMock, CommentMock {

    @Autowired
    CommentService commentService;
    @Autowired
    NewsfeedService newsfeedService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NewsfeedRespository newsfeedRepository;
    List<Comment> comments;
    Long newsFeedSeq;
    User user;
    CommentResponseDto createdComment = null;

    @BeforeAll
    public void init() {
        user = userRepository.save(userMock);
        Newsfeed newsfeed = newsfeedRepository.save(newsfeedMock);
        newsFeedSeq = newsfeed.getNewsFeedSeq();
    }

    @BeforeEach
    public void setup() {
        commentRepository.save(commentMock);
        comments = commentRepository.findAll();
    }

    @Test
    @Order(1)
    @DisplayName("댓글 등록")
    void createComment() {
        // given
        String content = "댓글";
        CommentRequestDto requestDto = CommentRequestDto.builder().content(content).build();

        //when
        CommentResponseDto commentResponseDto = commentService.createComment(newsFeedSeq, requestDto, user);

        //then
        assertEquals(content, commentResponseDto.getContent());
        createdComment = commentResponseDto;
    }

    @Nested
    @DisplayName("댓글 조회")
    @Order(2)
    class findAll_1 {
        @Test
        @DisplayName("댓글 조회_성공")
        void findAll_success() {
            //when
            List<CommentResponseDto> commentList = commentService.findAll(comments.get(0).getNewsfeed().getNewsFeedSeq());
            //then
            assertNotNull(commentList);
        }
    }


    @Nested
    @DisplayName("댓글 수정")
    @Order(3)
    class updateComment {
        @Test
        @DisplayName("댓글 수정_성공")
        void updateComment_success() {
            //given
            Long commentSeq = createdComment.getCommentSeq();
            CommentRequestDto requestDto = CommentRequestDto.builder().content("update").build();
            //when
            CommentResponseDto commentResponseDto = commentService.updateComment(newsFeedSeq, commentSeq, requestDto, user);
            //then
            assertEquals("update", commentResponseDto.getContent());
        }

        @Test
        @DisplayName("댓글 수정_사용자 일치X")
        void updateComment_COMMENT_NOT_USER() {
            //given
            Long commentSeq = createdComment.getCommentSeq();
            CommentRequestDto requestDto = CommentRequestDto.builder().content("update").build();
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> commentService.updateComment(newsFeedSeq, commentSeq, requestDto, another));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.COMMENT_NOT_USER);
        }
    }

    @Test
    @Order(4)
    @DisplayName("댓글 좋아요 증가")
    void increaseCommentLike() {
        //given
        Long commentSeq = comments.get(0).getCommentSeq();
        Long likeCount = comments.get(0).getLike();

        // when
        commentService.increaseCommentLike(commentSeq);

        // then
        Comment comment = commentRepository.findById(commentSeq).orElse(null);
        assertEquals(likeCount + 1, comment.getLike());
    }

    @Test
    @Order(5)
    @DisplayName("댓글 좋아요 감소")
    void decreaseCommentLike() {
        //given
        Long commentSeq = comments.get(0).getCommentSeq();
        Long likeCount = comments.get(0).getLike();

        // when
        commentService.decreaseCommentLike(commentSeq);

        // then
        Comment comment = commentRepository.findById(commentSeq).orElse(null);
        assertEquals(likeCount - 1, comment.getLike());
    }

    @Nested
    @DisplayName("댓글 삭제")
    @Order(6)
    class deleteComment {
        @Test
        @DisplayName("댓글 삭제_성공")
        void deleteComment_success() {
            //given
            Long commentSeq = createdComment.getCommentSeq();
            //when
            commentService.deleteComment(newsFeedSeq, commentSeq, user);
            // then
            assertNull(commentRepository.findById(commentSeq).orElse(null));
        }

        @Test
        @DisplayName("댓글 삭제_사용자 일치X")
        void deleteComment_COMMENT_NOT_USER() {
            //given
            Long commentSeq = createdComment.getCommentSeq();
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () ->   commentService.deleteComment(newsFeedSeq, commentSeq, another));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.COMMENT_NOT_USER);
        }
    }

    @Nested
    @DisplayName("댓글 조회")
    @Order(7)
    class findAll_2 {
        @Test
        @DisplayName("댓글 조회_실패")
        void findAll_COMMENT_NOT_FOUND() {
            //given
            commentRepository.deleteAll();
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> commentService.findAll(newsFeedSeq));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
        }
    }

}