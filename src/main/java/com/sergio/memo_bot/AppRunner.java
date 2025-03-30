package com.sergio.memo_bot;

import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    /*private final MemoBot memoBot;
    private final BotProperties botProperties;*/

    private final ChatAwaitsInputRepository chatAwaitsInputRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Started application");
        System.out.println(chatAwaitsInputRepository.findAll());
        /*try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botProperties.getApiKey(), memoBot);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
