package com.glackfag.goalmate.bot.action;

import java.util.Arrays;

public enum Action {
    SEND_GREETINGS,
    REGISTER,
    SHOW_MENU,
    SEND_NEW_GOAL_ESSENCE_FORM,
    SEND_NEW_GOAL_TIMEFRAME_FORM,
    SAVE_GOAL,
    FINISH_GOAL,
    DELETE_GOAL,
    FAIL_GOAL,
    PROVIDE_STATISTICS,
    SEND_ERROR_MESSAGE,
    SEND_RETRY;

    /**
     *
     * @return Enum element's name in camelCase
     */
    @Override
    public String toString() {
        String[] words = name().toLowerCase().split("_");
        return words.length == 1 ? words[0] :
                words[0] + String.join("",
                        Arrays.stream(words).skip(1).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                                .toList());
    }
}
