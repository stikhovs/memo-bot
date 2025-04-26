package com.sergio.memo_bot.external;

public class RepeatableHttpException extends RuntimeException {

    public RepeatableHttpException(String message) {
        super(message);
    }
}
