package com.sergio.memo_bot.external;

public class NonRepeatableHttpException extends RuntimeException {

    public NonRepeatableHttpException(String message) {
        super(message);
    }
}
