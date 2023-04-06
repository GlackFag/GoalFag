package com.glackfag.goalmate.bot.response;

import com.glackfag.goalmate.bot.Bot;
import com.glackfag.goalmate.bot.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ResponseSender {
    private final ResponseGenerator responseGenerator;
    private Bot bot;

    @Autowired
    public ResponseSender(ResponseGenerator responseGenerator, @Lazy Bot bot) {
        this.responseGenerator = responseGenerator;
        this.bot = bot;
    }

    public void sendMenuMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.SHOW_MENU);
        bot.execute(response);
    }

    public void sendGreetingsMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.SEND_GREETINGS);
        bot.execute(response);
    }

    public void sendRegisterMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.REGISTER);
        bot.execute(response);
    }

    public void sendNewGoalEssenceFormMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.SEND_NEW_GOAL_ESSENCE_FORM);
        bot.execute(response);
    }

    public void sendNewGoalTimeframeFormMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.SEND_NEW_GOAL_TIMEFRAME_FORM);
        bot.execute(response);
    }

    public void sendErrorMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.ERROR_MESSAGE);
        bot.execute(response);
    }

    public void sendRetryMessage(Update update) throws TelegramApiException {
        BotApiMethodMessage response = responseGenerator.generate(update, Action.SEND_RETRY);
        bot.execute(response);
    }
}
