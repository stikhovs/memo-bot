package com.sergio.memo_bot.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum CommandType {

    MAIN_MENU("/main-menu"),
    CLOSE ("/close"),
    START("/start"),
    IMPORT_SET("/import-set"),
    CREATE_SET("/create-set"),
    ACCEPT_SET_NAME("/accept-set-name"),
    DECLINE_SET_NAME("/decline-set-name"),
    ADD_CARD("/add-card"),
    ACCEPT_FRONT_SIDE("/accept-front-side"),
    DECLINE_FRONT_SIDE("/decline-front-side"),
    ACCEPT_BACK_SIDE("/accept-back-side"),
    DECLINE_BACK_SIDE("/decline-back-side"),
    SAVE_CARD_SET("/save-card-set"),

    GET_ALL_SETS("/get-all-sets"),

    ;

    private final String commandText;

    private static final Map<String, CommandType> BY_LABEL = new HashMap<>();

    static {
        for (CommandType e: values()) {
            BY_LABEL.put(e.commandText, e);
        }
    }

    public static boolean isCommandType(String text) {
        return BY_LABEL.containsKey(text);
    }

    public static CommandType getByCommandText(String text) {
        return BY_LABEL.get(text);
    }

}
