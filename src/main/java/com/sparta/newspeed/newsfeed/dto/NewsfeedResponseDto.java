package com.sparta.newspeed.newsfeed.dto;

import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import lombok.Getter;

@Getter
public class NewsfeedResponseDto {

    private Long newsFeedSeq;
    private String title;
    private String content;
    private int remainMember;
    private String userName;
    private String ottName;

    public NewsfeedResponseDto(Newsfeed newsfeed){
        this.newsFeedSeq = newsfeed.getNewsFeedSeq();
        this.title = newsfeed.getTitle();
        this.content = newsfeed.getContent();
        this.remainMember = newsfeed.getRemainMember();
        this.userName = newsfeed.getUser().getUserName();
        this.ottName = newsfeed.getOtt().getOttName();
    }

}
