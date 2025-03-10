package com.sergio.memo_bot.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    private String baseUrl;

    private String apiKey;

}
