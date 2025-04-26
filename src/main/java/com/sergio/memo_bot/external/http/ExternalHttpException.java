package com.sergio.memo_bot.external.http;

public class ExternalHttpException extends RuntimeException {

    public ExternalHttpException(String message) {
        super(message);
    }

    public ExternalHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalHttpException(Throwable cause) {
        super(cause);
    }
}
