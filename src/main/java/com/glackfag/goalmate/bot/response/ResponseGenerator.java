package com.glackfag.goalmate.bot.response;

import com.glackfag.goalmate.bot.action.Action;
import com.glackfag.goalmate.util.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ResponseGenerator {
    private final ResponseFormer responseFormer;

    @Autowired
    public ResponseGenerator(ResponseFormer template) {
        this.responseFormer = template;
    }

    public BotApiMethodMessage generate(Update update, Action action) {
        Long chatId = UpdateUtils.extractChatId(update);

        return switch (action) {
            case SEND_GREETINGS -> responseFormer.startMessage(chatId);
            case REGISTER, SHOW_MENU -> responseFormer.menuMessage(chatId);
            case SEND_NEW_GOAL_ESSENCE_FORM -> responseFormer.newGoalEssenceForm(chatId);
            case SEND_NEW_GOAL_TIMEFRAME_FORM -> responseFormer.sendNewGoalTimeframeForm(chatId);
            case SEND_RETRY -> responseFormer.sendRetry(chatId);

            case ERROR_MESSAGE -> responseFormer.errorMessage(chatId);
            default -> null;
        };
    }
}
