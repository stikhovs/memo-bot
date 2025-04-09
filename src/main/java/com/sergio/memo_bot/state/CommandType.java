package com.sergio.memo_bot.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum CommandType {

    MAIN_MENU("/main_menu"),
    CLOSE("/close"),
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
    GET_CARD_SET_INFO("/get_card_set__%s"),
    GET_CARDS("/get_cards"),
    EDIT_SET("/edit_set"),
    EDIT_TITLE_REQUEST("/edit_title_request"),
    EDIT_TITLE_RESPONSE("/edit_title_response"),
    EDIT_CARD_REQUEST("/edit_card_request__%s"),
    EDIT_CARD_FRONT_SIDE_REQUEST("/edit_card_front_request"),
    EDIT_CARD_FRONT_SIDE_RESPONSE("/edit_card_front_response"),
    EDIT_CARD_BACK_SIDE_REQUEST("/edit_card_back_request"),
    EDIT_CARD_BACK_SIDE_RESPONSE("/edit_card_back_response"),
    EDIT_CARD_RESPONSE("/edit_card_response"),

    REMOVE_SET_REQUEST("/remove_set_request"),
    REMOVE_SET_RESPONSE("/remove_set_response"),

    REMOVE_CARD_REQUEST("/remove_card_request"),
    REMOVE_CARD_RESPONSE("/remove_card_response"),

    GET_EXERCISES("/get_exercises"),

    FLASH_CARDS_PREPARE("/flash_cards_prepare"),
    FLASH_CARD("/flash_card"),
    FLIP_REQUEST("/flip_request"),
    NEXT_FLASH_CARD_REQUEST("/next_flash_card_request"),
    PREVIOUS_FLASH_CARD_REQUEST("/previous_flash_card_request"),

    QUIZ_PREPARE("/quiz_prepare"),
    QUIZ("/quiz"),

    ANSWER_INPUT_PREPARE("/answer_input_prepare"),
    ANSWER_INPUT_REQUEST("/answer_input_request"),
    ANSWER_INPUT_RESPONSE("/answer_input_response"),

    ;

    private final String commandText;

    private static final Map<String, CommandType> BY_LABEL = new HashMap<>();

    static {
        for (CommandType e : values()) {
            BY_LABEL.put(e.commandText, e);
        }
    }

    public static boolean isCommandType(String text) {
        return BY_LABEL.containsKey(text)
                || containsGetCardSet(text)
                || containsEditCard(text);
    }

    public static CommandType getByCommandText(String text) {
        CommandType commandType = BY_LABEL.get(text);
        if (commandType != null) {
            return commandType;
        }
        if (containsGetCardSet(text)) return GET_CARD_SET_INFO;
        if (containsEditCard(text)) return EDIT_CARD_REQUEST;

        throw new UnsupportedOperationException("Couldn't map text [%s] to CommandType".formatted(text));
    }

    private static boolean containsGetCardSet(String text) {
        return text.contains("/get_card_set__");
    }

    private static boolean containsEditCard(String text) {
        return text.contains("/edit_card_request__");
    }

}
