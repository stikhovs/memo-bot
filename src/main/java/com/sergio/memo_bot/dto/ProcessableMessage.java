package com.sergio.memo_bot.dto;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ProcessableMessage {

    private boolean processable;

    private Long userId;

    private Long chatId;

    private String username;

    private boolean fromPartReply;

    private String text;

    private Integer messageId;

    private boolean hasPhoto;

    private List<PhotoSize> photos;

    private Integer replyToMessageId;

}
