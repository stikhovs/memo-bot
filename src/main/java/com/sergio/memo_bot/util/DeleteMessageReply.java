package com.sergio.memo_bot.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class DeleteMessageReply implements Reply {

    private Long chatId;

    private List<Integer> messageIds;

}
