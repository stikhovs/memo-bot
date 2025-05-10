package com.sergio.memo_bot.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum CommandType {

    MAIN_MENU("/main_menu"),
    CATEGORY_MENU("/category_menu"),
    CARD_SET_MENU("/card_set_menu"),
    EXERCISES_FROM_CARD_SET("/exercises_from_card_set"),
    EXERCISES_FROM_CATEGORY("/exercises_from_category"),
    EXERCISES_FROM_MAIN_MENU("/exercises_from_main_menu"),

    CATEGORY_MENU_DATA("/category_menu_data"),
    CARD_SET_MENU_DATA("/card_set_menu_data"),

    START("/start"),
    SET_CREATION_START("/set_creation_start"),
    NAME_SET_REQUEST("/name_set_request"),
    NAME_SET_RESPONSE("/name_set_response"),
    ADD_CARD_REQUEST("/add_card_request"),
    INSERT_FRONT_SIDE("/insert_front_side"),
    FRONT_SIDE_RECEIVED("/front_side_received"),
    INSERT_BACK_SIDE("/insert_back_side"),
    BACK_SIDE_RECEIVED("/back_side_received"),
    ADD_CARD_RESPONSE("/add_card_response"),

    SAVE_CARD_SET_REQUEST("/request_to_save_card_set"),
    SAVE_CARD_SET_RESPONSE("/save_card_set_response"),

    GET_ALL_SETS("/get_all_sets"),
    CHOOSE_CARD_SET_IN_CATEGORY("/choose_card_set_in_category"),
    GET_CARD_SET_INFO("/get_card_set__%s"),
    GET_CARDS("/get_cards"),
    EDIT_SET("/edit_set"),
    EDIT_TITLE_REQUEST("/edit_title_request"),
    EDIT_TITLE_RESPONSE("/edit_title_response"),
    EDIT_CARDS_REQUEST("/edit_cards_request"),
    EDIT_CARD_REQUEST("/edit_card_request__%s"),
    EDIT_CARD_FRONT_SIDE_REQUEST("/edit_card_front_request"),
    EDIT_CARD_FRONT_SIDE_RESPONSE("/edit_card_front_response"),
    EDIT_CARD_BACK_SIDE_REQUEST("/edit_card_back_request"),
    EDIT_CARD_BACK_SIDE_RESPONSE("/edit_card_back_response"),

    REMOVE_SET_REQUEST("/remove_set_request"),
    REMOVE_SET_RESPONSE("/remove_set_response"),

    REMOVE_CARD_REQUEST("/remove_card_request"),
    REMOVE_CARD_RESPONSE("/remove_card_response"),

    EXERCISES_DATA_PREPARE("/prepare_exercise_data"),
    EXERCISES_DATA_SIZE_CHECK("/check_exercise_data_size"),
    EXERCISES_RESPONSE("/exercises_response"),

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

    CONNECT_WORDS_PREPARE("/connect_words_prepare"),
    CONNECT_WORDS_REQUEST("/connect_words_request"),
    CONNECT_WORDS_RESPONSE("/connect_words_response__%s"),

    CREATE_CATEGORY_REQUEST("/create_category_request"),
    CREATE_CATEGORY_USER_INPUT_TITLE("/create_category_user_input_title"),
    CREATE_CATEGORY_RESPONSE("/create_category_response"),

    EDIT_CATEGORY_REQUEST("/edit_category_request"),

    RENAME_CATEGORY_REQUEST("/rename_category_request"),
    RENAME_CATEGORY_USER_INPUT_TITLE("/rename_category_user_input_title"),
    RENAME_CATEGORY_RESPONSE("/rename_category_response"),

    GET_CATEGORY_INFO_REQUEST("/get_category_info_request__%s"),
    GET_CATEGORY_INFO_RESPONSE("/get_category_info_response"),

    DELETE_CATEGORY_REQUEST("/delete_category_request"),
    DELETE_CATEGORY_WITH_SETS_REQUEST("/delete_category_with_sets_request"),
    DELETE_CATEGORY_WITHOUT_SETS_REQUEST("/delete_category_without_sets_request"),
    DELETE_CATEGORY_RESPONSE("/delete_category_response"),

    CHOOSE_SETS_FOR_CATEGORY_PREPARE("/choose_sets_for_category__%s"),
    CHOOSE_SETS_FOR_CATEGORY_REQUEST("/choose_sets_for_category_request"),
    SET_CHOSEN_FOR_CATEGORY("/set_chosen_for_category__%s"),
    SAVE_NEW_CATEGORY_FOR_SETS("/save_new_category_for_sets"),

    CHOOSE_CATEGORY_REQUEST("/choose_category_request"),

    GET_CATEGORY_CARD_SET_INFO("/get_category_card_set_info"),
    CREATE_SET_FOR_CHOSEN_CATEGORY("/create_card_set_for_chosen_category"),

    SET_CATEGORY_REQUEST("/set_category_request"),
    SET_CATEGORY_RESPONSE("/set_category_response__%s"),

    CHOOSE_SET_REQUEST("/choose_set_request"),

    MOVE_SET_TO_ANOTHER_CATEGORY("/move_set_to_another_category"),
    CHOOSE_CATEGORY_FOR_SET_MOVING("/choose_category_for_set_moving__%s"),

    EXERCISES_FROM_MAIN_MENU_CHOOSE_SET("/exercises_from_main_menu_set_id__%s"),

    IMPORT_CARD_SET_MENU("/import_card_set_menu"),
    IMPORT_CARD_SET_FROM_QUIZLET_MENU("/import_card_set_from_quizlet_menu"),
    IMPORT_CARD_SET_README_1("/import_card_set_readme1"),
    IMPORT_CARD_SET_README_2("/import_card_set_readme2"),
    IMPORT_CARD_SET_README_3("/import_card_set_readme3"),
    IMPORT_CARD_SET_RESPONSE("/import_card_set_response"),
    IMPORT_CARD_SET_TITLE_REQUEST("/import_card_set_title_request"),
    IMPORT_CARD_SET_TITLE_RESPONSE("/import_card_set_title_response"),
    IMPORT_CARDS_REQUEST("/import_cards_request"),
    IMPORT_CARDS_RESPONSE("/import_cards_response"),
    CHOOSE_CATEGORY_FOR_IMPORT_REQUEST("/choose_category_for_import_request"),
    CHOOSE_CATEGORY_FOR_IMPORT_RESPONSE("/choose_category_for_import__%s"),

    IMPORT_CARD_SET_FROM_MESSAGE_MENU("/import_card_set_from_message_menu"),
    INSERT_IMPORT_MESSAGE_REQUEST("/insert_import_message_request"),
    IMPORT_MESSAGE_CHECK("/import_message_check"),
    IMPORT_MESSAGE_RESPONSE("/import_message_response"),
    IMPORT_FROM_MESSAGE_README("/import_from_message_readme"),
    CHOOSE_CATEGORY_FOR_MESSAGE_IMPORT_REQUEST("/choose_category_for_message_import_request"),
    CHOOSE_CATEGORY_FOR_MESSAGE_IMPORT_RESPONSE("/choose_category_for_message_import__%s"),

    EXERCISE_DATA_OPTIONS("/exercise_data_options"),
    SWAP_SIDES_REQUEST("/swap_sides_request"),
    SHUFFLE_CARDS_REQUEST("/shuffle_cards_request"),

    INFO("/bot_info"),

    EXPORT_CARD_SET_REQUEST("/export_card_set_request"),
    EXPORT_CARD_SET_RESPONSE("/export_card_set_response"),

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
                || containsEditCard(text)
                || containsConnectWordsResponse(text)
                || containsCategoryInfoRequest(text)
                || containsChooseSetsForCategory(text)
                || containsSetChosenForCategory(text)
                || containsSetCategoryResponse(text)
                || containsExFromMainMenuChooseSet(text)
                || containsChooseCategoryForSetMoving(text)
                || containsChooseCategoryForImport(text)
                || containsChooseCategoryForMessageImport(text);
    }

    public static CommandType getByCommandText(String text) {
        CommandType commandType = BY_LABEL.get(text);
        if (commandType != null) {
            return commandType;
        }
        if (containsGetCardSet(text)) return GET_CARD_SET_INFO;
        if (containsEditCard(text)) return EDIT_CARD_REQUEST;
        if (containsConnectWordsResponse(text)) return CONNECT_WORDS_RESPONSE;
        if (containsCategoryInfoRequest(text)) return GET_CATEGORY_INFO_REQUEST;
        if (containsChooseSetsForCategory(text)) return CHOOSE_SETS_FOR_CATEGORY_PREPARE;
        if (containsSetChosenForCategory(text)) return SET_CHOSEN_FOR_CATEGORY;
        if (containsSetCategoryResponse(text)) return SET_CATEGORY_RESPONSE;
        if (containsExFromMainMenuChooseSet(text)) return EXERCISES_FROM_MAIN_MENU_CHOOSE_SET;
        if (containsChooseCategoryForSetMoving(text)) return CHOOSE_CATEGORY_FOR_SET_MOVING;
        if (containsChooseCategoryForImport(text)) return CHOOSE_CATEGORY_FOR_IMPORT_RESPONSE;
        if (containsChooseCategoryForMessageImport(text)) return CHOOSE_CATEGORY_FOR_MESSAGE_IMPORT_RESPONSE;

        throw new UnsupportedOperationException("Couldn't map text [%s] to CommandType".formatted(text));
    }

    private static boolean containsGetCardSet(String text) {
        return text.contains("/get_card_set__");
    }

    private static boolean containsEditCard(String text) {
        return text.contains("/edit_card_request__");
    }

    private static boolean containsConnectWordsResponse(String text) {
        return text.contains("/connect_words_response__");
    }

    private static boolean containsCategoryInfoRequest(String text) {
        return text.contains("/get_category_info_request__");
    }

    private static boolean containsChooseSetsForCategory(String text) {
        return text.contains("/choose_sets_for_category__");
    }

    private static boolean containsSetChosenForCategory(String text) {
        return text.contains("/set_chosen_for_category__");
    }

    private static boolean containsSetCategoryResponse(String text) {
        return text.contains("/set_category_response__");
    }

    private static boolean containsChooseCategoryForSetMoving(String text) {
        return text.contains("/choose_category_for_set_moving__");
    }

    private static boolean containsExFromMainMenuChooseSet(String text) {
        return text.contains("/exercises_from_main_menu_set_id__");
    }

    private static boolean containsChooseCategoryForImport(String text) {
        return text.contains("/choose_category_for_import__");
    }

    private static boolean containsChooseCategoryForMessageImport(String text) {
        return text.contains("/choose_category_for_message_import__");
    }

}
