package com.sparta.newspeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAIL(500, "실패했습니다.")
    ;
    private int status;
    private String msg;
}