package com.glackfag.goalmate.bot.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

@Component
class ResponseFormer {
    private final Map<String, String> messages;

    private final MarkupSetter markupSetter;

    @Autowired
    ResponseFormer(@Qualifier("messages") Map<String, String> messages,
                   ApplicationContext applicationContext) {
        this.messages = messages; // don't remove @Qualifier, else it will always return null
        markupSetter = new MarkupSetter(applicationContext);
    }

     BotApiMethodMessage startMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), messages.get("greetings"));

         markupSetter.setRegisterMarkup(sendMessage);
        return sendMessage;
    }

     BotApiMethodMessage menuMessage(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId.toString(), messages.get("menu"));

         markupSetter.setMenuMarkup(sendMessage);
        return sendMessage;
    }

     BotApiMethodMessage newGoalEssenceForm(Long chatId){
        return new SendMessage(chatId.toString(), messages.get("newGoalEssenceInstruction"));
    }

    BotApiMethodMessage sendNewGoalTimeframeForm(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId.toString(), messages.get("newGoalTimeframeInstruction"));

        markupSetter.setTimeframeInstructionMarkup(sendMessage);
        return sendMessage;
    }

     BotApiMethodMessage errorMessage(Long chatId){
        return new SendMessage(chatId.toString(), messages.get("error"));
    }

    BotApiMethodMessage sendRetry(Long chatId){
        return new SendMessage(chatId.toString(), messages.get("retry"));
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
