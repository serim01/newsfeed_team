package com.sparta.newspeed.newsfeed.entity;

import com.sparta.newspeed.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.testdata.NewsFeedMock;
import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.entity.User;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NewsfeedTest implements UserMock, NewsFeedMock {

    private Newsfeed newsfeed;
    private User user;

    @BeforeEach
    void setUp() {
        user = userMock;
        newsfeed = newsfeedMock;
    }

    @DisplayName("Newsfeed 엔티티 생성 테스트")
    @Test
    @Order(1)
    void createNewsfeed() {
        // given
        Newsfeed newsfeed = this.newsfeed;

        // then
        assertThat(newsfeed.getNewsFeedSeq()).isEqualTo(1L);
        assertThat(newsfeed.getTitle()).isEqualTo(TITLE);
        assertThat(newsfeed.getContent()).isEqualTo(CONTENT);
        assertThat(newsfeed.getRemainMember()).isEqualTo(MEMBER);
        assertThat(newsfeed.getLike()).isEqualTo(LIKE);
        assertThat(newsfeed.getUser()).isEqualTo(user);
        assertThat(newsfeed.getOtt()).isEqualTo(TEST_OTT);
    }

    @DisplayName("updateNewsfeed 메서드 테스트")
    @Test
    @Order(2)
    void updateNewsfeed() {
        // given
        NewsfeedRequestDto updateDto = NewsfeedRequestDto.builder().title("Updated Title").content("Updated Content").remainMember(3).build();
        Ott newOtt = Ott.builder()
                .ottSeq(2L)
                .ottName("Disney+")
                .price(9000)
                .build();

        // when
        newsfeed.updateNewsfeed(updateDto, newOtt);

        // then
        assertThat(newsfeed.getTitle()).isEqualTo("Updated Title");
        assertThat(newsfeed.getContent()).isEqualTo("Updated Content");
        assertThat(newsfeed.getRemainMember()).isEqualTo(3);
        assertThat(newsfeed.getOtt()).isEqualTo(newOtt);
    }

    @DisplayName("increaseLike 메서드 테스트")
    @Test
    @Order(3)
    void increaseLike() {
        // when
        newsfeed.increaseLike();

        // then
        assertThat(newsfeed.getLike()).isEqualTo(1L);

        //한번에 돌릴때를 대비해서 decreaseLike 테스트때문에 미리 초기화 시켜놓음
        newsfeed.decreaseLike();
    }

    @DisplayName("decreaseLike 메서드 테스트")
    @Test
    @Order(4)
    void decreaseLike() {
        // given
        newsfeed.increaseLike();

        // when
        newsfeed.decreaseLike();

        // then
        assertThat(newsfeed.getLike()).isEqualTo(0L);
    }
}