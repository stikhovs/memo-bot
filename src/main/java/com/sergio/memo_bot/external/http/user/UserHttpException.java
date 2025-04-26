package com.sergio.memo_bot.external.http.user;

import com.sergio.memo_bot.external.http.ExternalHttpException;

public class UserHttpException extends ExternalHttpException {

    public UserHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
