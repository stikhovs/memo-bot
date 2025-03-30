package com.sergio.memo_bot.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.net.http.HttpClient;

@Configuration
public class HttpConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder().build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .uriTemplateHandler(getUriTemplateHandler())
                .build();
    }

    private UriTemplateHandler getUriTemplateHandler() {
        return new DefaultUriBuilderFactory("http://localhost:8081");
    }

}
