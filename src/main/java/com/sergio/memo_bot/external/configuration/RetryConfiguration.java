package com.sergio.memo_bot.external.configuration;

import com.sergio.memo_bot.external.RepeatableHttpException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfiguration {

    @Bean
    public RetryRegistry registry() {
        return RetryRegistry.of(
                RetryConfig.custom()
                        .maxAttempts(3)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(IntervalFunction.DEFAULT_INITIAL_INTERVAL, 2d))
                        .retryOnException(e -> e instanceof RepeatableHttpException)
                        .failAfterMaxAttempts(true)
                        .build()
        );
    }

    @Bean
    public Retry retry(RetryRegistry registry) {
        return registry.retry("Bot retry");
    }

}
