package com.sergio.memo_bot.external.http.card;

import com.sergio.memo_bot.external.http.ExternalHttpException;

public class CardHttpException extends ExternalHttpException {

    public CardHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
