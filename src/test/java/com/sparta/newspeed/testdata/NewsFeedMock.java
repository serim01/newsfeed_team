package com.sparta.newspeed.testdata;

import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.entity.Ott;
import com.sparta.newspeed.user.entity.User;

public interface NewsFeedMock {
    String TITLE = "넷플릭스 볼사람";
    String CONTENT = "넷플 같이 봅시다.";
    Integer MEMBER = 4;
    Long LIKE = 0L;
    User TEST_USER = UserMock.userMock;

    Ott TEST_OTT = Ott.builder()
            .ottSeq(1L)
            .ottName("Netflix")
            .price(30000)
            .maxMember(4)
            .build();

    Newsfeed newsfeedMock = Newsfeed.builder()
            .newsFeedSeq(1L)
            .title(TITLE).content(CONTENT)
            .remainMember(MEMBER)
            .like(LIKE).user(TEST_USER).ott(TEST_OTT).build();
}
