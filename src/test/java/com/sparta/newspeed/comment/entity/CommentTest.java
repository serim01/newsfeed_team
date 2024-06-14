package com.sparta.newspeed.comment.entity;

import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.testdata.CommentMock;
import com.sparta.newspeed.testdata.NewsFeedMock;
import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.entity.User;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentTest implements UserMock, NewsFeedMock, CommentMock {

    private Comment comment;
    private User user;
    private Newsfeed newsfeed;

    @BeforeEach
    void setUp() {
        user = userMock;
        newsfeed = newsfeedMock;
        comment = commentMock;
    }

    @DisplayName("Comment 엔티티 생성 테스트")
    @Test
    @Order(1)
    void createComment() {
        // given
        Comment comment = this.comment;

        // then
        assertThat(comment.getCommentSeq()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo(COMMENT_COTENT);
        assertThat(comment.getLike()).isEqualTo(COMMENT_LIKE);
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getNewsfeed()).isEqualTo(newsfeed);
    }

    @DisplayName("update 메서드 테스트")
    @Test
    @Order(2)
    void update() {
        // given
        CommentRequestDto updateDto = CommentRequestDto.builder().content("Updated Comment Content").build();

        // when
        comment.update(updateDto);

        // then
        assertThat(comment.getContent()).isEqualTo("Updated Comment Content");
    }

    @DisplayName("increaseLike 메서드 테스트")
    @Test
    @Order(3)
    void increaseLike() {
        // when
        comment.increaseLike();

        // then
        assertThat(comment.getLike()).isEqualTo(1L);

        //한번에 돌릴때를 대비해서 decreaseLike 테스트때문에 미리 초기화 시켜놓음
        comment.decreaseLike();
    }

    @DisplayName("decreaseLike 메서드 테스트")
    @Test
    @Order(4)
    void decreaseLike() {
        // given
        comment.increaseLike(); // Like를 1 증가시킴

        // when
        comment.decreaseLike();

        // then
        assertThat(comment.getLike()).isEqualTo(0L);
    }
}