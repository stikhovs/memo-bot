package com.sergio.memo_bot.external.configuration;

import com.sergio.memo_bot.external.http.card.CardHttpClient;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpClient;
import com.sergio.memo_bot.external.http.category.CategoryHttpClient;
import com.sergio.memo_bot.external.http.user.UserHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientsConfig {

    @Bean
    public UserHttpClient userHttpClient(RestClient restClient) {
        return getHttpServiceProxyFactory(restClient)
                .createClient(UserHttpClient.class);
    }

    @Bean
    public CategoryHttpClient categoryHttpClient(RestClient restClient) {
        return getHttpServiceProxyFactory(restClient)
                .createClient(CategoryHttpClient.class);
    }

    @Bean
    public CardSetHttpClient cardSetHttpClient(RestClient restClient) {
        return getHttpServiceProxyFactory(restClient)
                .createClient(CardSetHttpClient.class);
    }

    @Bean
    public CardHttpClient cardHttpClient(RestClient restClient) {
        return getHttpServiceProxyFactory(restClient)
                .createClient(CardHttpClient.class);
    }


    private HttpServiceProxyFactory getHttpServiceProxyFactory(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
    }

}
