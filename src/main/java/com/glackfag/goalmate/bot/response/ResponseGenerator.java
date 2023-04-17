package com.glackfag.goalmate.bot.response;

import com.glackfag.goalmate.bot.action.Action;
import com.glackfag.goalmate.util.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

@Component
@Slf4j
public class ResponseGenerator {
    private final Map<String, String> messages;
    private final MarkupSetter markupSetter;

    public ResponseGenerator(@Qualifier("messages") Map<String, String> messages,
                             ApplicationContext applicationContext) {
        this.messages = messages;
        markupSetter = new MarkupSetter(applicationContext);
    }


    public BotApiMethodMessage generate(Update update, Action action) {
        String chatId = UpdateUtils.extractChatId(update).toString();
        String actionAsString = action.toString();

        SendMessage sendMessage = new SendMessage(chatId, messages.get(actionAsString));

        switch (action) {
            case SEND_GREETINGS -> markupSetter.setRegisterMarkup(sendMessage);
            case SHOW_MENU -> markupSetter.setMenuMarkup(sendMessage);
            case SEND_NEW_GOAL_TIMEFRAME_FORM -> markupSetter.setTimeframeInstructionMarkup(sendMessage);
            case SEND_NEW_GOAL_ESSENCE_FORM, SEND_RETRY, SEND_ERROR_MESSAGE, REGISTER -> {
            }

            default -> throw new Error("Error: Unable to find '" + action + "' in messages.properties");
        }
        return sendMessage;
    }

    private record MarkupSetter(ApplicationContext applicationContext) {

        public void setRegisterMarkup(SendMessage sendMessage) {
            InlineKeyboardMarkup markup = (InlineKeyboardMarkup) applicationContext.getBean("registerMarkup");
            sendMessage.setReplyMarkup(markup);
        }

        private void setTimeframeInstructionMarkup(SendMessage sendMessage) {
            InlineKeyboardMarkup markup = (InlineKeyboardMarkup) applicationContext.getBean("timeframeInstructionMarkup");
            sendMessage.setReplyMarkup(markup);
        }

        private void setMenuMarkup(SendMessage sendMessage) {
            InlineKeyboardMarkup markup = (InlineKeyboardMarkup) applicationContext.getBean("menuMarkup");
            sendMessage.setReplyMarkup(markup);
        }
    }
}
