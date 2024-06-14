package com.sparta.newspeed.testdata;

import com.sparta.newspeed.comment.entity.Comment;

public interface CommentMock {
    String COMMENT_COTENT = "댓글";
    Long COMMENT_LIKE = 0L;

    Comment commentMock = Comment.builder().commentSeq(1L).content(COMMENT_COTENT).like(COMMENT_LIKE).user(UserMock.userMock).newsfeed(NewsFeedMock.newsfeedMock).build();
}
