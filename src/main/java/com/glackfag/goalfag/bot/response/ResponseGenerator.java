package com.glackfag.goalfag.bot.response;

import com.glackfag.goalfag.bot.action.Action;
import com.glackfag.goalfag.models.Goal;
import com.glackfag.goalfag.services.GoalsService;
import com.glackfag.goalfag.statistics.ChartFormer;
import com.glackfag.goalfag.util.Commands;
import com.glackfag.goalfag.util.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class ResponseGenerator {
    private final Map<String, String> messages;
    private final MarkupSetter markupSetter;
    private final GoalsService goalsService;
    private final ChartFormer chartFormer;

    public ResponseGenerator(@Qualifier("messages") Map<String, String> messages,
                             ApplicationContext applicationContext,
                             GoalsService goalsService, MarkupFormer markupFormer, ChartFormer chartFormer) {
        this.messages = messages;
        this.goalsService = goalsService;
        this.chartFormer = chartFormer;
        markupSetter = new MarkupSetter(applicationContext, goalsService, markupFormer);
    }

    public BotApiMethodMessage generate(Update update, Action action) {
        String chatId = UpdateUtils.extractChatId(update).toString();
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        String messageText = prepareText(update, action);
        SendMessage sendMessage = new SendMessage(chatId, messageText);

        switch (action) {
            case SEND_GREETINGS -> markupSetter.setRegisterMarkup(sendMessage);
            case SHOW_MENU -> markupSetter.setMenuMarkup(sendMessage);
            case SEND_NEW_GOAL_TIMEFRAME_FORM -> markupSetter.setTimeframeInstructionMarkup(sendMessage);
            case SHOW_GOAL_LIST -> markupSetter.setGoalsListMarkup(sendMessage, UpdateUtils.extractUserId(update));
            case SHOW_GOAL_DESCRIPTION -> {
                String goalId = callbackDataText.replaceFirst(Commands.SHOW_GOAL_DESCRIPTION, "");
                markupSetter.setGoalDescriptionMarkup(sendMessage, Long.parseLong(goalId));
            }

            case SEND_NEW_GOAL_ESSENCE_FORM, SEND_RETRY, SEND_ERROR_MESSAGE, REGISTER, SHOW_NO_GOALS_MESSAGE -> {
            }
        }

        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    private String prepareText(Update update, Action action) {
        String text = messages.get(action.toString());
        String callbackDataText = UpdateUtils.extractCallbackDataText(update);

        if (action == Action.SHOW_GOAL_DESCRIPTION) {
            String goalId = callbackDataText.replaceFirst(Commands.SHOW_GOAL_DESCRIPTION, "");
            Goal goal = goalsService.findOne(Long.parseLong(goalId));
            text = String.format(text, goal.getEssence(), goal.getExpiredDate(), goal.getCreationDate(), goal.getTimeframe(), goal.getState());
        }

        if (text == null)
            throw new Error("Error: Unable to find '" + action + "' in messages.properties");

        return text;
    }

    public SendPhoto generateSendPhotoWithPiePlot(Long chatId, Map<String, Integer> dataset) {
        PieChart chart = chartFormer.formPieChartFromDataset(dataset);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            return new SendPhoto(chatId.toString(), new InputFile(inputStream, "chart.png"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Error: unable to save chart as image");
        }
    }

    private record MarkupSetter(ApplicationContext applicationContext, GoalsService goalsService,
                                MarkupFormer markupFormer) {
        public void setRegisterMarkup(SendMessage sendMessage) {
            InlineKeyboardMarkup markup = (InlineKeyboardMarkup) applicationContext.getBean("registerMarkup");
            sendMessage.setReplyMarkup(markup);
        }

        public void setTimeframeInstructionMarkup(SendMessage sendMessage) {
            InlineKeyboardMarkup markup = (InlineKeyboardMarkup) applicationContext.getBean("timeframeInstructionMarkup");
            sendMessage.setReplyMarkup(markup);
        }

        public void setMenuMarkup(SendMessage sendMessage) {
            InlineKeyboardMarkup markup = (InlineKeyboardMarkup) applicationContext.getBean("menuMarkup");
            sendMessage.setReplyMarkup(markup);
        }

        public void setGoalDescriptionMarkup(SendMessage sendMessage, long goalId) {
            sendMessage.setReplyMarkup(markupFormer.formGoalDescriptionMarkup(goalId));
        }

        public void setGoalsListMarkup(SendMessage sendMessage, long userId) {
            sendMessage.setReplyMarkup(markupFormer.fromGoalListMarkup(userId));
        }

    }
}
