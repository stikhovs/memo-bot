package com.sergio.memo_bot.external.configuration;

import com.sergio.memo_bot.external.NonRepeatableHttpException;
import com.sergio.memo_bot.external.RepeatableHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class HttpConfig {

    @Bean
    public RestClient restClient(BackendProperties backendProperties) {
        return RestClient.builder()
                .baseUrl(backendProperties.getUrl())
                .defaultHeader("X-Internal-Auth", backendProperties.getBotSecretHeader())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseBody = new String(response.getBody().readAllBytes());
                    String errorMessage = "Error while executing [%s %s]. Response: [%s - %s]. Response body: %s".formatted(
                            request.getMethod(),
                            request.getURI(),
                            response.getStatusCode(),
                            response.getStatusText(),
                            responseBody
                    );
                    log.error(errorMessage);
                    if (response.getStatusCode().is4xxClientError()) {
                        throw new NonRepeatableHttpException(errorMessage);
                    }
                    throw new RepeatableHttpException(errorMessage);
                })
                .build();
    }

}
