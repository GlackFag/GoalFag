package com.glackfag.goalmate.bot;

import com.glackfag.goalmate.bot.action.Action;
import com.glackfag.goalmate.bot.action.ActionExecutor;
import com.glackfag.goalmate.bot.action.ActionRecognizer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;

    @Getter
    private final String token;

    private final ActionExecutor actionExecutor;

    private final ActionRecognizer actionRecognizer;

    @Autowired
    public Bot(@Qualifier("token") String botToken,
               @Qualifier("botUsername") String botUsername,
               TelegramBotsApi api, ActionRecognizer actionRecognizer,
               ActionExecutor actionExecutor) {
        super(botToken);
        this.token = botToken;
        this.botUsername = botUsername;
        this.actionExecutor = actionExecutor;
        this.actionRecognizer = actionRecognizer;

        try {
            api.registerBot(this);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Action action = actionRecognizer.recognize(update);

            actionExecutor.execute(action, update);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPhoto(SendPhoto photo) throws TelegramApiException {
        execute(photo);
    }
}
