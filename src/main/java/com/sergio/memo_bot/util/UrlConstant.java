package com.sergio.memo_bot.util;

public class UrlConstant {
    public static final String CREATE_USER_URL = "/telegram/user/create";
    public static final String GET_USER_URL = "/telegram/user?telegramUserId=%s";
    public static final String CREATE_SET_URL = "/telegram/set/save";
    public static final String GET_ALL_SETS_URL = "/telegram/sets-by-chat?telegramChatId=%s";
    public static final String GET_ALL_SETS_BY_CATEGORY_ID_URL = "/telegram/sets-by-category?categoryId=%s";
    public static final String GET_CARDS_URL = "/telegram/get-cards?cardSetId=%s";
    public static final String GET_SET_AND_CARDS_URL = "/telegram/set-and-cards-by-set-id?cardSetId=%s";
    public static final String UPDATE_SET_URL = "/telegram/set/update";
    public static final String UPDATE_CARD_URL = "/telegram/card/update";
    public static final String ADD_CARD_URL = "/telegram/card/add?cardSetId=%s";
    public static final String DELETE_CARD_SET_URL = "/telegram/set/delete?cardSetId=%s";
    public static final String DELETE_CARD_URL = "/telegram/card/delete?cardId=%s";

    public static final String CREATE_CATEGORY_URL = "/telegram/category/save?chatId=%s";
    public static final String UPDATE_CATEGORY_URL = "/telegram/category/update";
    public static final String GET_CATEGORY_BY_ID_URL = "/telegram/category?categoryId=%s";
    public static final String GET_CATEGORY_BY_CHAT_ID_URL = "/telegram/category/by-chat?chatId=%s";
    public static final String DELETE_CATEGORY_URL = "/telegram/category/delete?categoryId=%s&keepSets=%s";
    public static final String UPDATE_CATEGORY_FOR_SETS = "/telegram/set/update-category-batch?categoryId=%s";

}
