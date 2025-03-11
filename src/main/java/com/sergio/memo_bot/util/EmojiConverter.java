package com.sergio.memo_bot.util;

public final class EmojiConverter {

    public static String getEmoji(String unicode) {
        int codePoint = Integer.parseInt(unicode.replace("U+", ""), 16);
        return new String(Character.toChars(codePoint));
    }
    public static String getEmoji(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

}
