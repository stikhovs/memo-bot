package com.sergio.memo_bot.reply_text;

import com.sergio.memo_bot.util.EmojiConverter;

public final class ReplyTextConstant {

    public static final String START = "Добро пожаловать!";

    public static final String MAIN_MENU = """
            Выберите одно из действий:
            
            1. <strong>Наборы карточек</strong> — основа вашего обучения и прогресса. С помощью наборов с карточками вы можете учить, повторять и вспоминать неограниченное количество слов, определений, дат или терминов. Создавайте наборы и используйте их в упражнениях.
            
            2. <strong>Категории</strong> — для удобства наборы можно объединять в категории.
            
            3. <strong>Упражнения</strong> — тренируйте свою память и оттачивайте знания с помощью различных упражнений.
            """;

    public static final String CARD_SETS = EmojiConverter.getEmoji("U+1F4DD") + " Наборы карточек";
    public static final String CATEGORIES = EmojiConverter.getEmoji("U+1F516") + " Категории";
    public static final String EXERCISES = EmojiConverter.getEmoji("U+1F393") + " Упражнения";

    public static final String BACK = EmojiConverter.getEmoji("U+2B05") + " Назад";

    public static final String CREATE_SET = EmojiConverter.getEmoji("U+270F") + " Создать набор";
    public static final String CHOOSE_SET = EmojiConverter.getEmoji("U+1F4D1") + " Выбрать набор";
    public static final String IMPORT_SET = EmojiConverter.getEmoji("U+1FA84") + " Импортировать набор";


    public static final String CHOOSE_CATEGORY = "Выберите категорию";
    public static final String WHAT_IS_THE_NAME_OF_THE_SET = "Как будет называться ваш новый набор?";
    public static final String SET_WILL_BE_CREATED_WITH_CATEGORY = "Набор будет создан под категорией \"<strong>%s</strong>\"";
    public static final String SET_WILL_HAVE_TITLE = EmojiConverter.getEmoji("U+2705") + " Будет создан набор: \"<strong>%s</strong>\"";

    public static final String LETS_ADD_FIRST_CARD = "\nТеперь давайте добавим в него карточки";
    public static final String LETS_ADD_CARD = "Добавим карточку";
    public static final String CARD_CREATION_RESPONSE = """
                                Предварительный набор "<strong>%s</strong>"
                                Карточки: \n%s
                                """;

    public static final String SOMETHING_WENT_WRONG = "Что-то пошло не так";
    public static final String ADD_ONE_MORE_CARD = EmojiConverter.getEmoji("U+2705") + " Добавить еще карточку";
    public static final String SAVE_CARD_SET = EmojiConverter.getEmoji("U+1F4BE") + " Сохранить набор";
    public static final String CARD_WAS_ADDED = EmojiConverter.getEmoji("U+2705") + " Добавлена карточка: %s - %s";
    public static final String INSERT_BACK_SIDE = "Введите заднюю сторону карточки";
    public static final String INSERT_FRONT_SIDE = "Введите переднюю сторону карточки";
    public static final String CARD_SET_SUCCESSFULLY_SAVED = EmojiConverter.getEmoji("U+2705") + " Набор карточек успешно сохранен!";

    public static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_CARD_SET = EmojiConverter.getEmoji("U+26A0") + " Вы уверены, что хотите удалить набор?";
    public static final String YES = "Да";
    public static final String NO = "Нет";

    public static final String CARD_SET_IS_DELETED = "Набор удален";

    public static final String YOU_DO_NOT_HAVE_CATEGORIES_YET = "Вы пока не создали категорий. Хотите создать сейчас?";

    public static final String WHAT_DO_YOU_WANT_TO_EDIT = "Что хотите отредактировать?";
    public static final String RENAME_CARD_SET = EmojiConverter.getEmoji("U+1F524") + " Переименовать набор: %s";
    public static final String EDIT_CARD = EmojiConverter.getEmoji("U+270F") + " Изменить карточку: %s — %s";
    public static final String ADD_CARD = EmojiConverter.getEmoji("U+1F4DD") + " Добавить карточку";
    public static final String MOVE_CARD_SET_TO_ANOTHER_CATEGORY = EmojiConverter.getEmoji("U+1F516") + " Перенести набор в другую категорию";
    public static final String DELETE_CARD_SET = EmojiConverter.getEmoji("U+1F5D1") + " Удалить набор";
    public static final String INSERT_NEW_CARD_SET_TITLE = "Введите новое название набора";
    public static final String NEW_CARD_SET_TITLE_SUCCESSFULLY_SAVED = "Новое название успешно сохранено";

    public static final String CHOOSE_CARD_SET = "Выберите набор";
    public static final String CARD_SET_INFO = """
            Набор "<strong>%s</strong>".
            
            🟣 Категория <strong>%s</strong>.
            
            Количество карточек: %s.
            """;
    public static final String YOU_DO_NOT_HAVE_CARD_SETS_YET = "Вы пока не создали ни одного набора. Хотите создать сейчас?";
    public static final String SEE_CARDS = EmojiConverter.getEmoji("U+1F440") + " Посмотреть карточки";
    public static final String EDIT_CARD_SET = EmojiConverter.getEmoji("U+1F527") + " Редактировать набор";

    public static final String IMPORT_README_1 = "Шаг 1";
    public static final String IMPORT_README_2 = "Шаг 2";
    public static final String IMPORT_README_3 = "Шаг 3";

    public static final String CHOSEN_CATEGORY = "Выбрана категория \"%s\"";
    public static final String IMPORT_CARD_SET_FROM_QUIZLET = "Импортировать набор из Quizlet";
    public static final String IMPORT_INSTRUCTION = "Инструкция";
    public static final String CARD_SET_SUCCESSFULLY_IMPORTED = "Набор успешно импортирован";

    public static final String INSERT_CARD_SET_TITLE = "Введите название набора";
    public static final String COPY_HERE_CARD_SET_CONTENT_FROM_QUIZLET = "Скопируйте сюда содержимое набора из Quizlet";

    public static final String CHOOSE_SETS_WHICH_WILL_BE_MOVED_TO_CATEGORY_1 = "Выберите наборы, которые будут перенесены в категорию \"<strong>%s</strong>\"";
    public static final String CHOOSE_SETS_WHICH_WILL_BE_MOVED_TO_CATEGORY_2 = """
            Выберите наборы, которые будут перенесены в категорию "<strong>%s</strong>"
            
            Выбранные наборы:
            
            %s
            """;
    public static final String SAVE = EmojiConverter.getEmoji("U+1F4BE") + " Сохранить";
    public static final String SUCCESSFULLY_SAVED = "Успешно сохранено";

    public static final String INSERT_CATEGORY_TITLE = "Введите название категории";
    public static final String INSERT_CATEGORY_NEW_TITLE = "Введите новое название категории";
    public static final String CATEGORY_SUCCESSFULLY_CREATED = "Категория \"<strong>%s</strong>\" успешно создана";
    public static final String CHOOSE_OPTION_TO_DELETE = "Выберите вариант для удаления";
    public static final String DELETE_CATEGORY_AND_KEEP_CARD_SETS = "Оставить наборы";
    public static final String DELETE_CATEGORY_AND_DELETE_CARD_SETS = "Удалить наборы";
    public static final String CATEGORY_SUCCESSFULLY_DELETED = "Категория удалена";
    public static final String CATEGORY_SUCCESSFULLY_RENAMED = "Категория успешно переименована в \"<strong>%s</strong>\"";
    public static final String EDIT_CATEGORY = "Редактирование категории \"<strong>%s</strong>\"";
    public static final String RENAME_CATEGORY = "Переименовать категорию";
    public static final String DELETE_CATEGORY = "Удалить категорию";
    public static final String CATEGORY_INFO_WITH_CARD_SETS = """
            Категория "<strong>%s</strong>".
            
            Количество наборов: %s.
            
            Наборы:
            %s
            """;
    public static final String CATEGORY_INFO_WITHOUT_CARD_SETS = """
            Категория "<strong>%s</strong>".
            
            Количество наборов: %s.
            """;
    public static final String CHOOSE_CARD_SETS = "Выбрать наборы";
    public static final String CREATE_CARD_SET_IN_THIS_CATEGORY = "Создать набор в этой категории";
    public static final String MOVE_CARD_SETS_TO_THIS_CATEGORY = "Перенести наборы в эту категорию";
    public static final String EDIT_THIS_CATEGORY = "Редактировать категорию";
    public static final String CHOOSE_CATEGORY_MENU = "Выбрать категорию";
    public static final String CREATE_CATEGORY = "Создать категорию";
    public static final String YOU_DO_NOT_HAVE_CARD_SETS_IN_THIS_CATEGORY_YET = "В этой категории пока нет ни одного набора. Хотите создать сейчас?";

    public static final String EXERCISE_FINISHED = EmojiConverter.getEmoji("U+1F3C6") + " Упражнение завершено!";
    public static final String INSERT_ANSWER_FOR = "Введите ответ для: <strong>%s</strong>";
    public static final String CORRECT = "Верно! " + EmojiConverter.getEmoji("U+1F389");
    public static final String INCORRECT = EmojiConverter.getEmoji("U+1F648") + " Неверно. Правильный ответ: <strong>%s</strong>";

    public static final String FIND_ALL_PAIRS = EmojiConverter.getEmoji("U+1F440") + " Найдите все пары";
    public static final String FIND_ALL_PAIRS_1 = """
            👀 Сейчас ищем пару для: <strong>%s</strong>
                
            🙈 Количество ошибок: %s
                
            ✅ Найденные пары:
                 %s
            """;
    public static final String FIND_ALL_PAIRS_2 = """
            👀 Сейчас ищем пару для: <strong>%s</strong>
                
            🙈 Количество ошибок: %s
            """;
    public static final String FIND_ALL_PAIRS_3 = """
            👀 Найдите все пары
                
            🙈 Количество ошибок: %s
                
            ✅ Найденные пары:
                 %s
            """;

    public static final String NEXT_ARROW = EmojiConverter.getEmoji("U+27A1");
    public static final String BACK_ARROW = EmojiConverter.getEmoji("U+2B05");
    public static final String FLIP_CARD = EmojiConverter.getEmoji("U+2195") + " Перевернуть";
    public static final String TO_EXERCISES_LIST = "К списку упражнений";


    public static final String QUIZ_FINISHED = EmojiConverter.getEmoji("U+1F3C6") + " Квиз завершен!";
    public static final String THERE_SHOULD_BE_TWO_OR_MORE_CARDS_FOR_QUIZ =  EmojiConverter.getEmoji("U+1F645") + " Для квиза в наборе должно быть 2 и более карточек";

    public static final String CHOOSE_EXERCISE = "Выберите упражнение";
    public static final String FLASH_CARDS = EmojiConverter.getEmoji("U+1F9D1") + " Флеш-карточки";
    public static final String QUIZ = EmojiConverter.getEmoji("U+1F3B0") + " Квиз";
    public static final String INPUT_ANSWER = EmojiConverter.getEmoji("U+270F") + " Ввод ответа";
    public static final String FIND_PAIRS = """
            🅰 - 🅱 Найти пары
            """;

}
