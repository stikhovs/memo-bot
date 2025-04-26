package com.sergio.memo_bot.external.helper;

import com.sergio.memo_bot.external.http.ExternalHttpException;
import com.sergio.memo_bot.external.http.card.CardHttpException;
import io.github.resilience4j.core.functions.CheckedSupplier;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpCallHelper {

    private final Retry retry;

    public  <T> T callOrThrow(CheckedSupplier<T> supplier, Class<? extends ExternalHttpException> exception, String errorMessage) {
        try {
            return decorateWithRetry(supplier).get();
        } catch (Throwable ex) {
            log.error(errorMessage, ex);
            try {
                throw exception.getDeclaredConstructor(String.class, Throwable.class).newInstance(errorMessage, ex);
            } catch (Throwable throwable) {
                throw new ExternalHttpException(errorMessage, ex);
            }
        }
    }

    public void runOrThrow(Runnable runnable, Class<? extends ExternalHttpException> exception, String errorMessage) {
        try {
            decorateWithRetry(runnable).run();
        } catch (Throwable ex) {
            log.error(errorMessage, ex);
            try {
                throw exception.getDeclaredConstructor(String.class, Throwable.class).newInstance(errorMessage, ex);
            } catch (Throwable throwable) {
                throw new ExternalHttpException(errorMessage, ex);
            }
        }
    }

    private  <T> CheckedSupplier<T> decorateWithRetry(CheckedSupplier<T> supplier) {
        return Retry.decorateCheckedSupplier(retry, supplier);
    }

    private Runnable decorateWithRetry(Runnable runnable) {
        return Retry.decorateRunnable(retry, runnable);
    }

}
