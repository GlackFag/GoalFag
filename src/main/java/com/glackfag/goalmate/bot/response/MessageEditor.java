package com.glackfag.goalmate.bot.response;

import com.glackfag.goalmate.bot.Bot;
import com.glackfag.goalmate.util.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MessageEditor {
    private Bot bot;

    @Autowired
    public MessageEditor(@Lazy Bot bot) {
        this.bot = bot;
    }

    /**
     * removes markup and appends message with text of chosen inline button
     */
    public void removeInlineMarkup(Update update) {
        try {
            if (!update.hasCallbackQuery())
                return;

            CallbackQuery callback = update.getCallbackQuery();
            Message messageToEdit = callback.getMessage();

            String chosenButtonText = UpdateUtils.getUsedInlineButton(update).getText();

            EditMessageText editMessage = new EditMessageText();

            editMessage.setMessageId(messageToEdit.getMessageId());
            editMessage.setChatId(messageToEdit.getChatId());
            editMessage.setReplyMarkup(null);
            editMessage.setText(messageToEdit.getText() + "\n\n_" + chosenButtonText + "_");

            editMessage.setParseMode("Markdown");
            bot.execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }
}
