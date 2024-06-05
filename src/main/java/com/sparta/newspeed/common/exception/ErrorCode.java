package com.sparta.newspeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAIL(500, "실패했습니다."),
    USER_NOT_FOUND(400, "해당하는 유저를 찾을 수 없습니다."),
    INCORRECT_PASSWORD(400, "입력하신 비밀번호가 일치하지 않습니다."),
    NEWSFEED_NOT_FOUND(404,"뉴스피드를 찾을 수 없습니다."),
    NEWSFEED_NOT_USER(400, "해당 뉴스피드의 작성자가 아닙니다."),
    NEWSFEED_REMAIN_MEMBER_OVER(400, "남은 인원수가 전체 인원수를 초과했습니다."),
    OTT_NOT_FOUND(404,"해당 이름의 OTT 를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404,"댓글을 찾을 수 없습니다."),
    COMMENT_NOT_USER(400, "해당 댓글의 작성자가 아닙니다.")
    ;
    private int status;
    private String msg;
}