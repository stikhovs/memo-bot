package com.sergio.memo_bot.state;

import lombok.Getter;

import java.util.List;

@Getter
public enum UserStateType {

    MAIN_MENU(CommandType.MAIN_MENU),

    CLOSE (CommandType.CLOSE),

    START (CommandType.START),

    IMPORT_SET (CommandType.IMPORT_SET),

    CREATE_SET (CommandType.CREATE_SET),
    GIVE_NAME_TO_SET,
    SET_NAME_APPROVAL (CommandType.ACCEPT_SET_NAME),
    SET_NAME_DECLINE (CommandType.DECLINE_SET_NAME),
    ADD_CARD_REQUEST (CommandType.ADD_CARD),
    FRONT_SIDE_CARD_CHECK,
    FRONT_SIDE_CARD_ACCEPT (CommandType.ACCEPT_FRONT_SIDE),
    FRONT_SIDE_CARD_DECLINE (CommandType.DECLINE_FRONT_SIDE),
    BACK_SIDE_CARD_CHECK,
    BACK_SIDE_CARD_ACCEPT (CommandType.ACCEPT_BACK_SIDE),
    BACK_SIDE_CARD_DECLINE (CommandType.DECLINE_BACK_SIDE),
    CARD_SET_SAVE (CommandType.SAVE_CARD_SET),

    SHOW_SET_REQUESTED (CommandType.GET_ALL_SETS),

    ;

    private List<UserStateType> possibleNextStates;
    private final CommandType commandType;

    static {
        MAIN_MENU.possibleNextStates = List.of(START, IMPORT_SET, CREATE_SET);
        START.possibleNextStates = List.of(IMPORT_SET, CREATE_SET);
        CREATE_SET.possibleNextStates = List.of(GIVE_NAME_TO_SET);
        GIVE_NAME_TO_SET.possibleNextStates = List.of(SET_NAME_APPROVAL, SET_NAME_DECLINE);
        SET_NAME_DECLINE.possibleNextStates = List.of(GIVE_NAME_TO_SET);
        SET_NAME_APPROVAL.possibleNextStates = List.of(ADD_CARD_REQUEST);
        ADD_CARD_REQUEST.possibleNextStates = List.of(FRONT_SIDE_CARD_CHECK);
        FRONT_SIDE_CARD_CHECK.possibleNextStates = List.of(FRONT_SIDE_CARD_ACCEPT, FRONT_SIDE_CARD_DECLINE);
        FRONT_SIDE_CARD_DECLINE.possibleNextStates = List.of(ADD_CARD_REQUEST);
        FRONT_SIDE_CARD_ACCEPT.possibleNextStates = List.of(BACK_SIDE_CARD_CHECK);
        BACK_SIDE_CARD_CHECK.possibleNextStates = List.of(BACK_SIDE_CARD_ACCEPT, BACK_SIDE_CARD_DECLINE);
        BACK_SIDE_CARD_DECLINE.possibleNextStates = List.of(FRONT_SIDE_CARD_ACCEPT);
        BACK_SIDE_CARD_ACCEPT.possibleNextStates = List.of(CARD_SET_SAVE, ADD_CARD_REQUEST);
        CARD_SET_SAVE.possibleNextStates = List.of(MAIN_MENU);
    }

    UserStateType(CommandType commandType) {
        this.commandType = commandType;
    }

    UserStateType() {
        this.commandType = null;
    }

    public boolean filterByCommand(CommandType commandType) {
        return this.commandType == commandType;
    }
}
