package com.sergio.memo_bot.util;

public class UrlConstant {
    public static final String CREATE_USER_URL = "/telegram/user/create";
    public static final String GET_USER_URL = "/telegram/user?telegramUserId=%s";
    public static final String CREATE_SET_URL = "/telegram/set/save";
    public static final String GET_ALL_SETS_URL = "/telegram/sets-by-chat?telegramChatId=%s";
    public static final String GET_CARDS_URL = "/telegram/get-cards?cardSetId=%s";
    public static final String GET_SET_AND_CARDS_URL = "/telegram/set-and-cards-by-set-id?cardSetId=%s";
    public static final String UPDATE_SET_URL = "/telegram/set/update";
    public static final String UPDATE_CARD_URL = "/telegram/card/update";

}
