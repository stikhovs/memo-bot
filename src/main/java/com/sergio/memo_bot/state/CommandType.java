package com.sergio.memo_bot.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum CommandType {

    MAIN_MENU("/main_menu"),
    CLOSE ("/close"),
    START("/start"),
    IMPORT_SET("/import_set"),
    CREATE_SET("/create_set"),
    NAME_SET("/name_set"),
    ACCEPT_SET_NAME("/accept_set_name"),
    DECLINE_SET_NAME("/decline_set_name"),
    ADD_CARD_REQUEST("/add_card_request"),
    INSERT_FRONT_SIDE("/insert_front_side"),
    FRONT_SIDE_RECEIVED("/front_side_received"),
    INSERT_BACK_SIDE("/insert_back_side"),
    BACK_SIDE_RECEIVED("/back_side_received"),
    ADD_CARD_RESPONSE("/add_card_response"),

    ACCEPT_FRONT_SIDE("/accept_front_side"),
    DECLINE_FRONT_SIDE("/decline_front_side"),
    ACCEPT_BACK_SIDE("/accept_back_side"),
    DECLINE_BACK_SIDE("/decline_back_side"),
    SAVE_CARD_SET_REQUEST("/request_to_save_card_set"),
    SAVE_CARD_SET_RESPONSE("/save_card_set_response"),

    GET_ALL_SETS("/get_all_sets"),
    GET_CARD_SET_INFO("/get_card_set__%s");

    private final String commandText;

    private static final Map<String, CommandType> BY_LABEL = new HashMap<>();

    static {
        for (CommandType e: values()) {
            BY_LABEL.put(e.commandText, e);
        }
    }

    public static boolean isCommandType(String text) {
        return BY_LABEL.containsKey(text) || text.contains("/get_card_set__");
    }

    public static CommandType getByCommandText(String text) {
        return BY_LABEL.getOrDefault(text, GET_CARD_SET_INFO);
    }

}
